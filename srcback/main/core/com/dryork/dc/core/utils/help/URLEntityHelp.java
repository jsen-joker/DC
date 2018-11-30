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
public class URLEntityHelp {

    public static DcApp urlToApp(URL url) {
        DcApp dcApp = new DcApp();
        String app = url.getParameter("app", "hold.hold");
        String[] aps = app.split("\\.");
        dcApp.setName(aps[0]).setType(aps[1]);
        return dcApp;
    }

    public static DcTable urlToTable(URL url) {
        DcTable dcTable = new DcTable();
        String table = url.getParameter("table", "hold.hold");
        String[] tbs = table.split("\\.");
        dcTable.setLocal(tbs[0]).setRemote(tbs[1]);
        return dcTable;
    }

    public static DcColumn urlToColumn(URL url) {
        DcColumn dcColumn = new DcColumn();
        String column = url.getParameter("column", "hold.hold.false.ignore.hold.svarchar(256)");
        String[] cns = column.split("\\.");
        dcColumn.setLocal(cns[0]).setRemote(cns[1]).setKey(Boolean.valueOf(cns[2])).setGroupType(cns[3]).setGroup(cns[4])
                .setType(cns[5]);
        return dcColumn;
    }

}
