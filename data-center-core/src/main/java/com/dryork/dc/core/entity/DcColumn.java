package com.dryork.dc.core.entity;

import com.alibaba.dubbo.common.URL;
import com.dryork.dc.core.constants.DcConstants;
import com.dryork.dc.core.utils.help.UrlEntityHelp;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

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
    /**
     * 是否作为业务上的关键字，目前关键字只支持 and
     */
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

    /**
     * 是否为外键
     */
    private boolean fk;
    /**
     * 外键关联的本地表真实名字
     */
    private String fkTable;
    /**
     * 外键关联的本地表的字段真实名字
     */
    private String fkColumn;

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
        String builder = DcConstants.DC_SCHEMA + DcConstants.DC_PROTOCOL_STR + app.getName() +
                "?app=" + UrlEntityHelp.AppToUrl(app) +
                "&table=" + UrlEntityHelp.tableToUrl(dcTable) +
                "&column=" + UrlEntityHelp.columnToUrl(this) +
                "&category=column";
        return URL.valueOf(builder);
    }
}
