package com.dryork.config.mulds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/2
 */
public class DataSourceContextHolder {
    private static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);

    /**
     * 默认数据源
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    static {
//        CONTEXT_HOLDER.set("default");
    }

    /**
     * 设置数据源名
     */
    public static void setDB(String dbType) {
        CONTEXT_HOLDER.set(dbType);
        log.info("切换到{}数据源", CONTEXT_HOLDER.get());
    }

    /**
     * 获取数据源名
     */
    public static String getDB() {
        log.info("get db " + CONTEXT_HOLDER.get());
        return (CONTEXT_HOLDER.get());
    }

    /**
     * 清除数据源名
     */
    public static void clearDB() {
        CONTEXT_HOLDER.set("default");
    }
}
