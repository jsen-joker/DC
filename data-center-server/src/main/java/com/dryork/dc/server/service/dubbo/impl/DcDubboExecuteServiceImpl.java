package com.dryork.dc.server.service.dubbo.impl;

import com.dryork.dc.core.constants.ConstantSql;
import com.dryork.dc.core.exception.AbstractDcException;
import com.dryork.dc.core.exception.DcSqlEmptyException;
import com.dryork.dc.core.service.dubbo.DcDubboExecuteService;
import com.dryork.dc.core.service.logical.DcCoreDbHandleService;
import com.dryork.dc.core.utils.*;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *     dc dubbo 核心sql创建、转换、检验
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
@com.alibaba.dubbo.config.annotation.Service(timeout = 5000)
public class DcDubboExecuteServiceImpl implements DcDubboExecuteService {

    @Autowired
    private DcCoreDbHandleService dcCoreDbHandleService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
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

    @Override
    public String echo(String name) {
        return "echo " + name;
    }

    /**
     * 处理插入数据
     * @param dcMessage
     * @return
     */
    @SuppressWarnings("unchecked")
    private DcReturnMessage handleInsert(DcMessage dcMessage) throws AbstractDcException {
        DcLogger.LOG log = DcLogger.startLog(dcMessage);

        List<DcReturnRecord> list =  dcMessage.getDcRecordList().stream().map(dcRecord -> {
            log.startRecord(dcRecord);
            DcReturnRecord dcReturnRecord = new DcReturnRecord();
            dcReturnRecord.setApp(dcMessage.getApp()).setMapKey(dcRecord.getMapKey());
            // 1、根据keys检查数据是否存在，不支持无key插入
            String querySql = genUpdateSql(dcRecord.getKeys(), " and ");
            if (querySql != null) {
                Map record = dcCoreDbHandleService.sqlOne(String.format(ConstantSql.SELECT_SIMPLE, dcRecord.getTable(), querySql), log);
                // 根据关键字检查原记录是否已经插入
                if (record == null) {
                    Map<String, Object> params = Maps.newHashMap();
                    dumpMapAll(params, dcRecord.getKeys());
                    dumpMapAll(params, dcRecord.getReplace());
                    dumpMapAll(params, dcRecord.getIgnore());
                    dumpMapAll(params, dcRecord.getSum());
                    params.put(ConstantSql.Column.APP_NAME, dcMessage.getApp());
                    String date = DateUtil.sqlDatetime(new Date());
                    params.put(ConstantSql.Column.CREATE_AT, date);
                    params.put(ConstantSql.Column.UPDATE_AT, date);
                    // do insert
                    SqlPair sqlPair = genSqlPair(params);
                    // 插入的字段要求不为null，其实这个是不肯能出现的
                    if (sqlPair != null) {
                        log.info("insert record null");
                        dcCoreDbHandleService.sqlCUD(String.format(ConstantSql.DATA_INSERT, dcRecord.getTable(), sqlPair.keys, sqlPair.values), log);
                        Map recordNew = dcCoreDbHandleService.sqlOne(String.format(ConstantSql.SELECT_SIMPLE, dcRecord.getTable(), querySql), log);
                        dcReturnRecord.setDcId((Long) recordNew.get(ConstantSql.Column.DC_ID));
                    } else {
                        dcReturnRecord.setDcId(null);
                        throw new DcSqlEmptyException("insert param list is null", log);
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
                    String date = DateUtil.sqlDatetime(new Date());
                    record.put(ConstantSql.Column.UPDATE_AT, date);
                    String updateSql = genUpdateSql(record, ",");
                    // 其实这个字段也不可能为null
                    if (updateSql != null) {
                        log.info("insert update record not null");
                        dcCoreDbHandleService.sqlCUD(String.format(ConstantSql.DATA_UPDATE, dcRecord.getTable(), updateSql, record.get(ConstantSql.Column.DC_ID)), log);
                        dcReturnRecord.setDcId((Long) record.get(ConstantSql.Column.DC_ID));
                    } else {
                        dcReturnRecord.setDcId((Long) record.get(ConstantSql.Column.DC_ID));
                        throw new DcSqlEmptyException("insert update param list is null", log);
                    }
                }
            } else {
                dcReturnRecord.setDcId(null);
                throw new DcSqlEmptyException("insert key list is null", log);
            }
            return dcReturnRecord;

        }).collect(Collectors.toList());

        DcReturnMessage dcReturnMessage = new DcReturnMessage().setDcReturnRecordList(list).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
        log.finished();
        return dcReturnMessage;
    }

    private DcReturnMessage handleUpdate(DcMessage dcMessage) {
        DcLogger.LOG log = DcLogger.startLog(dcMessage);

        DcReturnMessage dcReturnMessage = new DcReturnMessage().setDcReturnRecordList(dcMessage.getDcRecordList().stream().map(dcRecord -> {
            log.startRecord(dcRecord);

            Map<String, Object> replace = dcRecord.getReplace();
            String date = DateUtil.sqlDatetime(new Date());
            replace.put(ConstantSql.Column.UPDATE_AT, date);
            String updateSql = genUpdateSql(replace, ",");
            if (updateSql != null) {
                log.info("update record");
                int eff = dcCoreDbHandleService.sqlCUD(String.format(ConstantSql.DATA_UPDATE, dcRecord.getTable(), updateSql, dcRecord.getDcId()), log);
                log.info("update eff: {}", eff);
            } else {
                throw new DcSqlEmptyException("update param list is null", log);
            }
            return new DcReturnRecord().setMapKey(dcRecord.getMapKey()).setDcId(dcRecord.getDcId()).setApp(dcMessage.getApp());
        }).collect(Collectors.toList())).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
        log.finished();
        return dcReturnMessage;
    }

    private DcReturnMessage handleDelete(DcMessage dcMessage) {
        DcLogger.LOG log = DcLogger.startLog(dcMessage);

        DcReturnMessage dcReturnMessage = new DcReturnMessage().setDcReturnRecordList(dcMessage.getDcRecordList().stream().map(dcRecord -> {
            log.startRecord(dcRecord);
            log.info("delete record");
            int eff = dcCoreDbHandleService.sqlCUD(String.format(ConstantSql.DATA_DELETE, dcRecord.getTable(), dcRecord.getDcId()), log);
            log.info("delete eff: {}", eff);
            return new DcReturnRecord().setMapKey(dcRecord.getMapKey()).setDcId(dcRecord.getDcId()).setApp(dcMessage.getApp());
        }).collect(Collectors.toList())).setActionType(dcMessage.getActionType()).setApp(dcMessage.getApp());
        log.finished();
        return dcReturnMessage;
    }

    /**
     *
     * @param target
     * @param source
     */
    @SuppressWarnings("unchecked")
    private void dumpMapAll(Map target, Map source) {
        if (source != null) {
            target.putAll(source);
        }
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

    /**
     * 生成更新sql
     * @param params
     * @param split
     * @return
     */
    @SuppressWarnings("unchecked")
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
