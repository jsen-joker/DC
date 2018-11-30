package com.dryork.dc.core.utils;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * <p>
 *     一条执行记录
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcRecord {
    /**
     * 全局dcId
     */
    private Long dcId;
    /**
     * 表名字
     */
    private String table;
    /**
     * 作为记录关键字
     */
    private Map<String, Object> keys;
    /**
     * 替换字段，更新的时候要更新的字段放在这里
     */
    private Map<String, Object> replace;
    /**
     * 忽略字段值
     */
    private Map<String, Object> ignore;
    /**
     * sum 记录值
     */
    private Map<String, Object> sum;

    public boolean simpleKey(String key, Object value) {
        if (keys == null) {
            keys = Maps.newHashMap();
        }
        keys.put(key, value);
        return true;
    }

    public boolean simpleReplace(String key, Object value) {
        if (replace == null) {
            replace = Maps.newHashMap();
        }
        replace.put(key, value);
        return true;
    }

    public boolean simpleSum(String key, Object value) {
        if (!(value instanceof Long || value instanceof Integer)) {
            return false;
        }
        if (sum == null) {
            sum = Maps.newHashMap();
        }
        sum.put(key, value);
        return true;
    }
}
