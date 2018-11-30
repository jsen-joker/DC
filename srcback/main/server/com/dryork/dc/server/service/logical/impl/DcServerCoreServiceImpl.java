package com.dryork.dc.server.service.logical.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;
import com.dryork.constants.ConstantSql;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import com.dryork.dc.core.utils.help.URLEntityHelp;
import com.dryork.dc.server.service.logical.DcServerCoreService;
import com.dryork.mapper.DcCoreMapper;
import com.dryork.utils.DateUtil;
import com.dryork.dc.core.zk.DcZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
@Service
public class DcServerCoreServiceImpl implements DcServerCoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DcServerCoreServiceImpl.class);

    @Value("${dc.zk.registry}")
    String zkUrl;
    @Autowired
    private DcCoreMapper dcCoreMapper;

    private RegistryService dcZookeeperRegistry;

    @PostConstruct
    public void init() {
        dcZookeeperRegistry = new DcZookeeperRegistry(URL.valueOf(zkUrl));
    }


    /**
     * 服务端处理zk文件变化
     *
     * @param urls
     */
    @Override
    public void listenColumnChange(List<URL> urls) {
        urls.forEach(url -> {
            DcTable dcTable = URLEntityHelp.urlToTable(url);
            DcColumn dcColumn = URLEntityHelp.urlToColumn(url);
            // check table exist
            String tableName = dcTable.getRemote();
            Map table = dcCoreMapper.sqlOne(String.format(ConstantSql.TABLE_EXIST, tableName));
            if (table == null) {
                // create table
                String comment = " COMMENT='dc auto create at " + DateUtil.simpleDate(new Date()) + "'";
                dcCoreMapper.sqlCUD(String.format(ConstantSql.CREATE_TABLE, dcTable.getRemote(), comment));
            }
            String columnName = dcColumn.getRemote();
            String type = dcColumn.getType();
            Map column = dcCoreMapper.sqlOne(String.format(ConstantSql.COLUMN_EXIST, tableName, columnName));
            if (column == null) {
                // create column
                String comment = "dc auto create at " + DateUtil.simpleDate(new Date());
                dcCoreMapper.sqlCUD(String.format(ConstantSql.CREATE_COLUMN, tableName, columnName, type, comment));
            }
        });
    }

    /**
     * 服务端注册监听zk变化，首次lookup
     */
    @Override
    public void serverListen() {
        LOGGER.info("dc center start listen");
        dcZookeeperRegistry.subscribe(URL.valueOf("dc://DC?category=column"), new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                LOGGER.info("get zookeeper info");
                listenColumnChange(urls);
            }
        });
        listenColumnChange(dcZookeeperRegistry.lookup(URL.valueOf("dc://DC?category=column")));

    }
}
