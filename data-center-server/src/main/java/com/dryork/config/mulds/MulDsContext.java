package com.dryork.config.mulds;

import com.google.common.collect.Maps;

import javax.sql.DataSource;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
public class MulDsContext {

    /**
     * 保存app的ds
     */
    private static final Map<String, DataSource> dsPool = Maps.newHashMap();
    /**
     * 保存配置的默认数据源
     * sys 和 dc
     */
    private static final Map<String, DataSource> stPool = Maps.newHashMap();

//    public static void addDS(String key, DataSource dataSource) {
//        dsPool.put(key, dataSource);
//    }
    public static void addDsSt(String key, DataSource dataSource) {
        stPool.put(key, dataSource);
    }
//    public static void remove(String key) {
//        dsPool.remove(key);
//    }
    public static DataSource get(String key) {
        return dsPool.getOrDefault(key, stPool.getOrDefault(key, stPool.get("default")));
    }

//    public static void clear() {
//        dsPool.clear();
//    }
//    public static void createDatasource(DcApp dcApp) {
//        DataSource dataSource = DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class)
//                .driverClassName("com.mysql.jdbc.Driver")
//                .type(com.zaxxer.hikari.HikariDataSource.class)
//                .username(dcApp.getUsername().trim())
//                .password(dcApp.getPassword().trim())
//                .url(dcApp.getDs()).build();
//
//        MulDsContext.addDS(dcApp.getAppName(), dataSource);
//    }

}
