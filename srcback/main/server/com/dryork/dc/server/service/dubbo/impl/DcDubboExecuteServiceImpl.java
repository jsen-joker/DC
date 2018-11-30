package com.dryork.dc.server.service.dubbo.impl;

import com.dryork.constants.ConstantSql;
import com.dryork.dc.core.service.dubbo.DcDubboExecuteService;
import com.dryork.dc.core.utils.DcMessage;
import com.dryork.dc.core.utils.DcReturnMessage;
import com.dryork.dc.core.utils.DcReturnRecord;
import com.dryork.mapper.DcCoreMapper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
@com.alibaba.dubbo.config.annotation.Service(timeout = 5000)
public class DcDubboExecuteServiceImpl implements DcDubboExecuteService {

    @Autowired
    private DcCoreMapper dcCoreMapper;

    @Override
    public DcReturnMessage execute(DcMessage dcMessage) {
        switch (dcMessage.getActionType()) {
            case BATCH_INSERT:
                return handleInsert(dcMessage);
            case BATCH_UPDATE:
                return handleUpdate(dcMessage);
            case BATCH_DELETE:
                return handleDelete(dcMessage);
            default:
                return null;

        }
    }

    private DcReturnMessage handleInsert(DcMessage dcMessage) {
        List<DcReturnRecord> list =  dcMessage.getDcRecordSet().stream().map(dcRecord -> {
            DcReturnRecord dcReturnRecord = new DcReturnRecord();
            dcReturnRecord.setApp(dcMessage.getApp());
            // 1、根据keys检查数据是否存在
            String querySql = genUpdateSql(dcRecord.getKeys(), " and ");
            if (querySql != null) {
                Map record = dcCoreMapper.sqlOne(String.format(ConstantSql.SELECT_BY_KEY, dcRecord.getTable(), querySql));
                if (record == null) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.putAll(dcRecord.getKeys());
                    params.putAll(dcRecord.getReplace());
                    params.putAll(dcRecord.getIgnore());
                    params.putAll(dcRecord.getSum());
                    // do insert
                    SqlPair sqlPair = genSqlPair(params);
                    if (sqlPair != null) {
                        dcCoreMapper.sqlCUD(String.format(ConstantSql.DATA_INSERT, dcRecord.getTable(), sqlPair.keys, sqlPair.values));
                        Map recordNew = dcCoreMapper.sqlOne(String.format(ConstantSql.SELECT_BY_KEY, dcRecord.getTable(), querySql));
                        dcReturnRecord.setDcId((Long) recordNew.get(ConstantSql.Column.DC_ID));
                    } else {
                        dcReturnRecord.setDcId(null);
                    }
                } else {
                    // do update
                    dcRecord.getReplace().forEach((key, value) -> {
                        if (record.containsKey(key)) {
                            record.put(key, value);
                        }
                    });
                    dcRecord.getSum().forEach((key, value) -> {
                        Object oValue = record.get(key);
                        if (oValue != null) {
                            if (oValue instanceof Long && value instanceof Long) {
                                record.put(key, ((Long) oValue) + ((Long) value));
                            } else if (oValue instanceof Integer && value instanceof Integer) {
                                record.put(key, ((Integer) oValue) + ((Integer) value));
                            }
                        }
                    });
                    String updateSql = genUpdateSql(record, ",");
                    if (updateSql != null) {
                        dcCoreMapper.sqlCUD(String.format(ConstantSql.DATA_UPDATE, dcRecord.getTable(), updateSql, record.get(ConstantSql.Column.DC_ID)));
                        dcReturnRecord.setDcId((Long) record.get(ConstantSql.Column.DC_ID));
                    } else {
                        dcReturnRecord.setDcId((Long) record.get(ConstantSql.Column.DC_ID));
                    }
                }
            } else {
                dcReturnRecord.setDcId(null);
            }
            return dcReturnRecord;

        }).collect(Collectors.toList());

        return new DcReturnMessage().setDcReturnRecordList(list).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
    }

    private DcReturnMessage handleUpdate(DcMessage dcMessage) {
        return new DcReturnMessage().setDcReturnRecordList(dcMessage.getDcRecordSet().stream().map(dcRecord -> {
            String updateSql = genUpdateSql(dcRecord.getReplace(), ",");
            if (updateSql != null) {
                dcCoreMapper.sqlCUD(String.format(ConstantSql.DATA_UPDATE, dcRecord.getTable(), updateSql, dcRecord.getDcId()));
            }
            return new DcReturnRecord().setDcId(dcRecord.getDcId()).setApp(dcMessage.getApp());
        }).collect(Collectors.toList())).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
    }

    private DcReturnMessage handleDelete(DcMessage dcMessage) {
        return new DcReturnMessage().setDcReturnRecordList(dcMessage.getDcRecordSet().stream().map(dcRecord -> {
            dcCoreMapper.sqlCUD(String.format(ConstantSql.DATA_DELETE, dcRecord.getTable(), dcRecord.getDcId()));
            return new DcReturnRecord().setDcId(dcRecord.getDcId()).setApp(dcMessage.getApp());
        }).collect(Collectors.toList())).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
    }

    /**
     * 创建sql create pair key1,key2,keys      value1,value2,value3
     * @param params
     * @return
     */
    private SqlPair genSqlPair(Map<String, Object> params) {
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        params.forEach((key, value) -> {
            keys.append(key).append(",");
            values.append("'").append(value).append("',");
        });
        if (keys.length() > 0) {
            return new SqlPair(keys.substring(0, keys.length() - 1), values.substring(0, values.length() - 1));
        }
        return null;
    }

    private String genUpdateSql(Map params, String split) {
        StringBuilder build = new StringBuilder();
        params.forEach((key, value) -> {
            build.append("`").append(key).append("`='").append(value).append("'").append(split);
        });
        if (build.length() > 0) {
            return build.substring(0, build.length() - split.length());
        }
        return null;
    }

    private static class SqlPair {
        private String keys;
        private String values;

        SqlPair(String keys, String values) {
            this.keys = keys;
            this.values = values;
        }
    }
}
