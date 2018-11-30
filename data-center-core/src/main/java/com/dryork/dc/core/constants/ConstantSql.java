package com.dryork.dc.core.constants;

/**
 * <p>
 *     dc 核心sql format
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
public interface ConstantSql {

    String TABLE_EXIST = "show tables like '%s'";
    String CREATE_TABLE = "CREATE TABLE `%s`  (\n" +
            "  `" + Column.DC_ID + "` " + Type.DC_ID + " NOT NULL AUTO_INCREMENT COMMENT 'dc auto gen global id',\n" +
            "  `" + Column.APP_NAME + "` " + Type.APP_NAME + " NOT NULL COMMENT 'dc auto gen creator app name',\n" +
            "  `" + Column.CREATE_AT + "` " + Type.CREATE_AT + " NOT NULL COMMENT 'dc auto gen create time',\n" +
            "  `" + Column.UPDATE_AT + "` " + Type.UPDATE_AT + " NOT NULL COMMENT 'dc auto gen update time',\n" +
            "  PRIMARY KEY (`" + Column.DC_ID + "`)\n" +
            ") %s;";


    String COLUMN_EXIST = "show columns from `%s` like '%s'";
    String CREATE_COLUMN = "ALTER TABLE `%s` ADD COLUMN `%s` %s NULL COMMENT '%s'";

    /**
     * 根据key判断数据是否存在
     */
    String SELECT_SIMPLE = "select * from `%s` where %s";

    String SELECT_BY_FK = "select id from `%s` where `" + Column.APP_NAME + "`='%s' and `%s`=%s";
    /**
     * 插入数据
     */
    String DATA_INSERT = "insert into `%s` ( %s ) values ( %s )";
    /**
     * 更新数据
     */
    String DATA_UPDATE = "update `%s` set %s where `" + Column.DC_ID + "`=%s";
    String DATA_UPDATE_METAS = "update `%s` set `" + Column.DC_ID + "`=%s,`" + Column.APP_NAME + "`='%s' where %s";
    /**
     * 删除数据
     */
    String DATA_DELETE = "delete from `%s` where " + Column.DC_ID + "=%d";

    interface Column {
        String APP_NAME = "dc_app_name";
        String DC_ID = "dc_id";
        String CREATE_AT = "dc_create_at";
        String UPDATE_AT = "dc_update_at";
        String FK_PREFIX = "dc_fk_";
    }
    interface Type {
        String APP_NAME = "varchar(128)";
        String DC_ID = "bigint(11)";
        String CREATE_AT = "datetime(0)";
        String UPDATE_AT = "datetime(0)";
    }
}
