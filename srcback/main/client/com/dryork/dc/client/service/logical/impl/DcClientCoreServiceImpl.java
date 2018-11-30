package com.dryork.dc.client.service.logical.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.RegistryService;
import com.dryork.dc.client.service.logical.DcClientCoreService;
import com.dryork.dc.core.constants.DcConstants;
import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import com.dryork.dc.core.exception.DcRequestExecuteException;
import com.dryork.dc.core.exception.DcRequestParseException;
import com.dryork.dc.core.service.dubbo.DcDubboExecuteService;
import com.dryork.dc.core.utils.*;
import com.dryork.dc.core.zk.DcZookeeperRegistry;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
@Service
public class DcClientCoreServiceImpl implements DcClientCoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DcClientCoreServiceImpl.class);

    @Value("${dc.zk.registry}")
    String zkUrl;

    @Reference(check = false)
    DcDubboExecuteService dcDubboExecuteService;

    private RegistryService dcZookeeperRegistry;

    @PostConstruct
    public void init() {
        dcZookeeperRegistry = new DcZookeeperRegistry(URL.valueOf(zkUrl));
    }
    /**
     * 客户端执行sql入口
     *
     * @param dcRequest
     * @return
     */
    @Override
    public DcReturnMessage exec(DcRequest dcRequest) {
        DcMessage dcMessage = new DcMessage().setActionType(dcRequest.getActionType()).setApp(dcApp.getName());
        Set<DcRecord> dcRecordSet = Sets.newHashSet();
        dcMessage.setDcRecordSet(dcRecordSet);
        if (dcRequest.getActionType() == ActionType.BATCH_INSERT) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = simpleTable(table);

                Map<String, DcColumn> dcColumnSet = dcTable.getDcColumnMap();
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());

                simpleDcParam(dcRecord, dcColumnSet, item);
                dcRecordSet.add(dcRecord);
            });
        } else if (dcRequest.getActionType() == ActionType.BATCH_UPDATE) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = simpleTable(table);

                Map<String, DcColumn> dcColumnSet = dcTable.getDcColumnMap();
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());

                simpleDcId(dcRecord, item);

                simpleDcParam(dcRecord, dcColumnSet, item);
                dcRecordSet.add(dcRecord);
            });
        } else if (dcRequest.getActionType() == ActionType.BATCH_DELETE) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = dcTableSet.get(table);
                if (dcTable == null) {
                    throw new DcRequestParseException("dc table : " + table + " not exist");
                }
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());
                simpleDcId(dcRecord, item);
                dcRecordSet.add(dcRecord);
            });
        } else {
            throw new DcRequestParseException("can not find action : " + dcRequest.getActionType());
        }

        // do
        try {
            return dcDubboExecuteService.execute(dcMessage);
        } catch (Exception e) {
            throw  new DcRequestExecuteException(e.getMessage());
        }
    }

    @Autowired
    private ApplicationContext applicationContext;
    private Map<String, DcTable> dcTableSet = Maps.newHashMap();
    private DcApp dcApp;
    /**
     * 客户端注册到zk
     */
    @Override
    public void clientRegisterToZK() {
        LOGGER.info("dc client start register");
        // 注册所有table和column信息
        dcApp = applicationContext.getBean(DcApp.class);
        for (Map.Entry<String, DcColumn> entry: applicationContext.getBeansOfType(DcColumn.class).entrySet()) {
            DcColumn dcColumn = entry.getValue();
            dcColumn.dumpToTable();
            dcColumn.dumpToMap(dcTableSet);
            dcZookeeperRegistry.register(dcColumn.genURL(dcApp));
        }
    }





    private DcTable simpleTable(String table) throws DcRequestParseException {
        DcTable dcTable = dcTableSet.get(table);
        if (dcTable == null) {
            throw new DcRequestParseException("dc table : " + table + " not exist");
        }
        return dcTable;
    }

    private void simpleDcParam(DcRecord dcRecord, Map<String, DcColumn> dcColumnSet, Map<String, Object> item) throws DcRequestParseException {
        item.forEach((column, value) -> {
            DcColumn dcColumn = dcColumnSet.get(column);
            if (dcColumn == null) {
                throw new DcRequestParseException("dc column : " + column + " not exist");
            }
            simple(dcRecord, dcColumn, value);
        });
    }

    private void simpleDcId(DcRecord dcRecord, Map<String, Object> item) throws DcRequestParseException {
        Object dcIdObj = item.get(DcConstants.DC.DC_ID);
        if (dcIdObj instanceof Long) {
            dcRecord.setDcId((Long) dcIdObj);
        } else {
            throw new DcRequestParseException("dc id is not Long type");
        }
        item.remove(DcConstants.DC.DC_ID);
    }

    /**
     * 添加一条column道record中
     * @param dcRecord
     * @param dcColumn
     * @param value
     * @throws DcRequestParseException
     */
    private void simple(DcRecord dcRecord, DcColumn dcColumn, Object value) throws DcRequestParseException {
        if (dcColumn.getKey()) {
            if (!dcRecord.simpleKey(dcColumn.getRemote(), value)) {
                throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
            }
            return;
        }
        switch (dcColumn.getGroupType()) {
            case DcConstants.GroupType.SUM:
                if (!dcRecord.simpleSum(dcColumn.getRemote(), value)) {
                    throw new DcRequestParseException(dcColumn.getLocal() + " column not a sum type value");
                }
                break;
            case DcConstants.GroupType.IGNORE:
                if (!dcRecord.simpleSum(dcColumn.getRemote(), value)) {
                    throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
                }
                break;
            case DcConstants.GroupType.REPLACE:
                if (!dcRecord.simpleReplace(dcColumn.getRemote(), value)) {
                    throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
                }
                break;
            default:
                throw new DcRequestParseException("can not parse group type : " + dcColumn.getGroupType());
        }
    }

}
