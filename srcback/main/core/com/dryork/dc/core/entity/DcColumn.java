package com.dryork.dc.core.entity;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcColumn extends AbstractDcBean {
    private String local;
    private String remote;
    private Boolean key;
    /**
     * sum ignore replace
     */
    private String groupType;
    /**
     * mysql 数据类型
     */
    private String type;
    private String group;

    private DcTable dcTable;

    public void dumpToTable() {
        if (dcTable == null) {
            return;
        }
        Map<String, DcColumn> dcColumnMap = dcTable.getDcColumnMap();
        if (dcColumnMap == null) {
            dcColumnMap = Maps.newHashMap();
            dcTable.setDcColumnMap(dcColumnMap);
        }
        dcColumnMap.put(getLocal(), this);
    }
    public void dumpToMap(Map<String, DcTable> map) {
        if (dcTable != null && !map.containsKey(dcTable.getLocal())) {
            map.put(dcTable.getLocal(), dcTable);
        }
    }
    public URL genURL(DcApp app) {
        StringBuilder builder = new StringBuilder();
        builder.append("dc://").append(app.getName());
        builder.append("?app=").append(app.getName()).append(".").append(app.getType());
        builder.append("&table=").append(dcTable.getLocal()).append(".").append(dcTable.getRemote());
        builder.append("&column=").append(getLocal()).append(".").append(getRemote())
                .append(".").append(getKey()).append(".").append(getGroupType()).append(".").append(getGroup())
                .append(".").append(type)
        .append("&category=column");
        return URL.valueOf(builder.toString());
    }
}
