package com.dryork.dc.core.utils.help;

import com.alibaba.dubbo.common.URL;
import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;


/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public class UrlEntityHelp {

    public static DcApp urlToApp(URL url) {
        DcApp dcApp = new DcApp();
        String app = url.getParameter("app", "hold.hold");
        String[] aps = app.split("\\.");
        dcApp.setName(aps[0]).setType(aps[1]);
        return dcApp;
    }

    public static String AppToUrl(DcApp dcApp) {
        return dcApp.getName() + "." + dcApp.getType();
    }

    public static DcTable urlToTable(URL url) {
        DcTable dcTable = new DcTable();
        String table = url.getParameter("table", "hold.hold");
        String[] tbs = table.split("\\.");
        dcTable.setLocal(tbs[0]).setRemote(tbs[1]);
        return dcTable;
    }

    public static String tableToUrl(DcTable dcTable) {
        return dcTable.getLocal() + "." + dcTable.getRemote();
    }

    public static DcColumn urlToColumn(URL url) {
        DcColumn dcColumn = new DcColumn();
        String column = url.getParameter("column", "hold.hold.false.ignore.hold.svarchar(256)");
        String[] cns = column.split("\\.");
        dcColumn.setLocal(cns[0]).setRemote(cns[1]).setKey(Boolean.valueOf(cns[2])).setGroupType(cns[3]).setGroup(cns[4])
                .setType(cns[5]).setFk(Boolean.valueOf(cns[6])).setFkTable(cns[7]).setFkColumn(cns[8]);
        return dcColumn;
    }

    public static String columnToUrl(DcColumn dcColumn) {
        return dcColumn.getLocal() + "." + dcColumn.getRemote() +
                "." + dcColumn.getKey() + "." + dcColumn.getGroupType() + "." + dcColumn.getGroup() +
                "." + dcColumn.getType() + "." + dcColumn.isFk() + "." + dcColumn.getFkTable() + "." + dcColumn.getFkColumn();
    }

}
