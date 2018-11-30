package com.dryork.dc.client.service.logical.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.RegistryService;
import com.dryork.dc.client.service.logical.DcClientCoreService;
import com.dryork.dc.core.constants.ConstantSql;
import com.dryork.dc.core.constants.DcConstants;
import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import com.dryork.dc.core.exception.DcConfigException;
import com.dryork.dc.core.exception.DcRequestExecuteException;
import com.dryork.dc.core.exception.DcRequestParseException;
import com.dryork.dc.core.service.dubbo.DcDubboExecuteService;
import com.dryork.dc.core.service.logical.DcCoreDbHandleService;
import com.dryork.dc.core.utils.*;
import com.dryork.dc.core.utils.help.ParameterHelp;
import com.dryork.dc.core.zk.DcZookeeperRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 *     数据返回成功后应该处理 dc_id更新问题，暂未处理
 *     wait
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
    private DcDubboExecuteService dcDubboExecuteService;
    @Autowired
    private DcCoreDbHandleService dcCoreDbHandleService;

    private RegistryService dcZookeeperRegistry;

    @PostConstruct
    public void init() {
        dcZookeeperRegistry = new DcZookeeperRegistry(URL.valueOf(zkUrl));
    }

    /**
     * 客户端执行sql入口
     * 注意客户端不需要执行本地更新的sql，
     * 这里远程执行成功后才会执行本地sql
     *
     * @param dcRequest
     * @param onlyUpdateMetas 是否为复制，区别在于，复制的情况下，CUD操作成功后只会更新对应的dc字段，eg. dc_id etc.
     *               而非复制的情况下，会执行对应的CUD操作，这意味着，不需要再本地执行对应的CUD操作，
     *
     * @return
     */
    @Override
    public DcReturnMessage exec(DcRequest dcRequest, boolean onlyUpdateMetas) {
        DcLogger.LOG log = DcLogger.startEmpty();
        DcMessage dcMessage = new DcMessage().setActionType(dcRequest.getActionType()).setApp(dcApp.getName());
        List<DcRecord> dcRecordSet = Lists.newArrayList();
        Map<Long, DcRecord> cacheMap = Maps.newHashMap();
        AtomicReference<Long> mapKye = new AtomicReference<>(0L);
        dcMessage.setDcRecordList(dcRecordSet);
        if (dcRequest.getActionType() == ActionType.BATCH_INSERT) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = simpleTable(table);

                Map<String, DcColumn> dcColumnSet = dcTable.getDcColumnMap();
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());
                dcRecord.setMapKey(mapKye.getAndSet(mapKye.get() + 1));
                DcRecord dcRecordLocal = new DcRecord().setTable(dcTable.getLocal());
                dcRecordLocal.setMapKey(dcRecord.getMapKey());
                cacheMap.put(dcRecordLocal.getMapKey(), dcRecordLocal);


                simpleDcParam(dcRecordLocal, dcRecord, dcColumnSet, item, false, true, log);
                dcRecordSet.add(dcRecord);
            });
        } else if (dcRequest.getActionType() == ActionType.BATCH_UPDATE) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = simpleTable(table);

                Map<String, DcColumn> dcColumnSet = dcTable.getDcColumnMap();
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());
                dcRecord.setMapKey(mapKye.getAndSet(mapKye.get() + 1));
                DcRecord dcRecordLocal = new DcRecord().setTable(dcTable.getLocal());
                dcRecordLocal.setMapKey(dcRecord.getMapKey());
                cacheMap.put(dcRecordLocal.getMapKey(), dcRecordLocal);

                simpleDcId(dcRecordLocal, dcRecord, item);

                simpleDcParam(dcRecordLocal, dcRecord, dcColumnSet, item, true, false, log);
                dcRecordSet.add(dcRecord);
            });
        } else if (dcRequest.getActionType() == ActionType.BATCH_DELETE) {

            dcRequest.getParams().forEach((table, item) -> {
                DcTable dcTable = dcTableSet.get(table);
                if (dcTable == null) {
                    throw new DcRequestParseException("dc table : " + table + " not exist");
                }
                DcRecord dcRecord = new DcRecord().setTable(dcTable.getRemote());
                dcRecord.setMapKey(mapKye.getAndSet(mapKye.get() + 1));
                DcRecord dcRecordLocal = new DcRecord().setTable(dcTable.getLocal());
                dcRecordLocal.setMapKey(dcRecord.getMapKey());
                cacheMap.put(dcRecordLocal.getMapKey(), dcRecordLocal);

                simpleDcId(dcRecordLocal, dcRecord, item);
                dcRecordSet.add(dcRecord);
            });
        } else {
            throw new DcRequestParseException("can not find action : " + dcRequest.getActionType());
        }

        // do
        try {
            DcReturnMessage dcReturnMessage = dcDubboExecuteService.execute(dcMessage);
            // 更新本地记录
            if (dcRequest.getActionType() == ActionType.BATCH_INSERT) {
                // do update metas
                dcReturnMessage.getDcReturnRecordList().forEach(item -> {
                    DcRecord dcRecordLocal = cacheMap.get(item.getMapKey());
                    if (dcRecordLocal.getDcId() != null) {
                        // do delete
                        String query = ParameterHelp.genUpdateSql(dcRecordLocal.getKeys(), " and ");
                        String set = "";

                        String sql = String.format(ConstantSql.DATA_UPDATE_METAS, dcRecordLocal.getTable(), dcRecordLocal.getDcId(), dcApp.getName(), query);
                        dcCoreDbHandleService.sqlCUD(sql, log);
                    }
                });
//                if (!onlyUpdateMetas) {
//                    // insert
//
//                } else {
//                    // update meta
//                }
            } else if (dcRequest.getActionType() == ActionType.BATCH_UPDATE) {
                // do update meta
//                if (!onlyUpdateMetas) {
//                    // update
//                    dcReturnMessage.getDcReturnRecordList().forEach(item -> {
//                        DcRecord dcRecordLocal = cacheMap.get(item.getMapKey());
//                        if (dcRecordLocal.getDcId() != null) {
//                            // do delete
//                            String sql = String.format(ConstantSql.DATA_UPDATE, dcRecordLocal.getTable(), , dcRecordLocal.getDcId());
//                            dcCoreDbHandleService.sqlCUD(sql, log);
//                        }
//                    });
//                } else {
//                    // update meta
//                }
            } else if (dcRequest.getActionType() == ActionType.BATCH_DELETE) {
//                if (!onlyUpdateMetas) {
//                    // do delete
//                    dcReturnMessage.getDcReturnRecordList().forEach(item -> {
//                        DcRecord dcRecordLocal = cacheMap.get(item.getMapKey());
//                        if (dcRecordLocal.getDcId() != null) {
//                            // do delete
//                            String sql = String.format(ConstantSql.DATA_DELETE, dcRecordLocal.getTable(), dcRecordLocal.getDcId());
//                            dcCoreDbHandleService.sqlCUD(sql, log);
//                        }
//                    });
//                } else {
//                    // skip delete
//                }
            } else {
                throw new DcRequestParseException("can not find action : " + dcRequest.getActionType());
            }
            return dcReturnMessage;
        } catch (Exception e) {
            throw  new DcRequestExecuteException(e.getMessage());
        }
    }

    @Override
    public String echo(String name) {
        return dcDubboExecuteService.echo(name);
    }

    @Autowired
    private ApplicationContext applicationContext;
    private Map<String, DcTable> dcTableSet = Maps.newHashMap();
    private DcApp dcApp;
    /**
     * 客户端注册到zk
     * 修改本地数据库，增加dc_id, dc_app_name fk等
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

        // check table column information
        DcLogger.LOG log = DcLogger.startEmpty();
        dcTableSet.forEach((local, dcTable) -> {
            Map table = dcCoreDbHandleService.sqlOne(String.format(ConstantSql.TABLE_EXIST, local), log);
            if (table == null) {
                throw new DcConfigException("table: " + local + " in local is null", log);
            }

            // 创建 dc meta 字段
            checkColumn(local, ConstantSql.Column.DC_ID, ConstantSql.Type.DC_ID, log);
            checkColumn(local, ConstantSql.Column.APP_NAME, ConstantSql.Type.APP_NAME, log);

            if (dcTable.getDcColumnMap() != null) {
                dcTable.getDcColumnMap().forEach((localColumnName, dcColumn) -> {
                    // 检查创建外键字段
                    if (dcColumn.isFk()) {
                        checkColumn(localColumnName, ConstantSql.Column.FK_PREFIX + localColumnName, dcColumn.getType(), log);
                    }
                });
            }
        });

    }

    private void checkColumn(String tableName, String columnName, String type, DcLogger.LOG log) {
        Map column = dcCoreDbHandleService.sqlOne(String.format(ConstantSql.COLUMN_EXIST, tableName, columnName), log);
        if (column == null) {
            String comment = "dc auto create at " + DateUtil.simpleDate(new Date());
            dcCoreDbHandleService.sqlCUD(String.format(ConstantSql.CREATE_COLUMN, tableName, columnName, type, comment), log);

        }
    }

    private DcTable simpleTable(String table) throws DcRequestParseException {
        DcTable dcTable = dcTableSet.get(table);
        if (dcTable == null) {
            throw new DcRequestParseException("dc table : " + table + " not exist");
        }
        return dcTable;
    }

    private void simpleDcParam(DcRecord dcRecordLocal, DcRecord dcRecord, Map<String, DcColumn> dcColumnSet, Map<String, Object> item,
                               boolean ignoreGroup, boolean resolveFk, DcLogger.LOG log) throws DcRequestParseException {
        item.forEach((column, value) -> {
            DcColumn dcColumn = dcColumnSet.get(column);
            if (dcColumn == null) {
                return;
//                throw new DcRequestParseException("dc column : " + column + " not exist");
            }
            if (dcColumn.isFk()) {
                if (resolveFk) {
                    // get fk column
                    DcTable fkDcTable = dcTableSet.get(dcColumn.getFkTable());
                    if (fkDcTable == null) {
                        throw new DcConfigException("can not fount fk table : " + dcColumn.getFkTable(), log);
                    }
                    DcColumn fkDcColumn = fkDcTable.getDcColumnMap().get(dcColumn.getFkColumn());
                    if (fkDcColumn == null) {
                        throw new DcConfigException("can not fount fk column : " + dcColumn.getFkTable() + "-" + dcColumn.getFkColumn(), log);
                    }
                    String sql = String.format(ConstantSql.SELECT_BY_FK, fkDcTable.getLocal(), dcApp.getName(), fkDcColumn.getLocal(), value);
                    Map origin = dcCoreDbHandleService.sqlOne(sql, log);
                    if (origin == null || !origin.containsKey(ConstantSql.Column.DC_ID)) {
                        throw new DcRequestParseException("can not get origin record for foreign key");
                    }
                    item.put(column, origin.get(ConstantSql.Column.DC_ID));
                }

            }
            simple(dcRecordLocal, dcRecord, dcColumn, value, ignoreGroup);
        });
    }

    /**
     * 设置dc id
     * @param dcRecordLocal
     * @param dcRecord
     * @param item
     * @throws DcRequestParseException
     */
    private void simpleDcId(DcRecord dcRecordLocal, DcRecord dcRecord, Map<String, Object> item) throws DcRequestParseException {
        Object dcIdObj = item.get(ConstantSql.Column.DC_ID);
        if (dcIdObj instanceof Long) {
            dcRecord.setDcId((Long) dcIdObj);
            dcRecordLocal.setDcId((Long) dcIdObj);
        } else {
            throw new DcRequestParseException("dc id is not Long type");
        }
        item.remove(ConstantSql.Column.DC_ID);
    }

    /**
     * 添加一条column道record中
     * @param dcRecordLocal
     * @param dcRecord
     * @param dcColumn
     * @param value
     * @throws DcRequestParseException
     */
    private void simple(DcRecord dcRecordLocal, DcRecord dcRecord, DcColumn dcColumn, Object value, boolean ignoreGroup) throws DcRequestParseException {
        if (dcColumn.getKey()) {
            if (!dcRecord.simpleKey(dcColumn.getRemote(), value) || !dcRecordLocal.simpleKey(dcColumn.getLocal(), value)) {
                throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
            }
            return;
        }
        if (!ignoreGroup) {
            switch (dcColumn.getGroupType()) {
                case DcConstants.GroupType.SUM:
                    if (!dcRecord.simpleSum(dcColumn.getRemote(), value) || !dcRecordLocal.simpleSum(dcColumn.getLocal(), value)) {
                        throw new DcRequestParseException(dcColumn.getLocal() + " column not a sum type value");
                    }
                    break;
                case DcConstants.GroupType.IGNORE:
                    if (!dcRecord.simpleSum(dcColumn.getRemote(), value) || !dcRecordLocal.simpleSum(dcColumn.getLocal(), value)) {
                        throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
                    }
                    break;
                case DcConstants.GroupType.REPLACE:
                    if (!dcRecord.simpleReplace(dcColumn.getRemote(), value) || !dcRecordLocal.simpleReplace(dcColumn.getLocal(), value)) {
                        throw new DcRequestParseException(dcColumn.getLocal() + " add failed");
                    }
                    break;
                default:
                    throw new DcRequestParseException("can not parse group type : " + dcColumn.getGroupType());
            }
        } else {
            dcRecord.simpleReplace(dcColumn.getRemote(), value);
            dcRecordLocal.simpleReplace(dcColumn.getLocal(), value);
        }
    }

    // sql handle
    private void delete(DcRecord dcRecord, DcReturnRecord dcReturnRecord) {
//        String sql = String.format(ConstantSql.DATA_DELETE, )
    }

}
