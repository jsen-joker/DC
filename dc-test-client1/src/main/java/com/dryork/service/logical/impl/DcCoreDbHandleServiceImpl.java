package com.dryork.service.logical.impl;

import com.dryork.dc.core.exception.AbstractDcException;
import com.dryork.dc.core.exception.DcSqlException;
import com.dryork.dc.core.service.logical.DcCoreDbHandleService;
import com.dryork.dc.core.utils.DcLogger;
import com.dryork.mapper.DcCoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *     消极，一旦出错全部撤销
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class DcCoreDbHandleServiceImpl implements DcCoreDbHandleService {

    @Autowired
    private DcCoreMapper dcCoreMapper;

    /**
     * 获取一个list
     *
     * @param sql sql
     * @param log
     * @return result
     */
    @Override
    public List<Map> sqlList(String sql, DcLogger.LOG log) {
        throw new DcSqlException("not impl", log);
    }

    @Override
    public Map sqlOne(String sql, DcLogger.LOG log) throws AbstractDcException {
        try {
            return dcCoreMapper.sqlOne(sql);
        } catch (Exception e) {
            throw new DcSqlException("sql execute error", log);
        }
    }

    @Override
    public int sqlCUD(String sql, DcLogger.LOG log) throws AbstractDcException {
        try {
            return dcCoreMapper.sqlCUD(sql);
        } catch (Exception e) {
            throw new DcSqlException("sql execute error", log);
        }
    }
}
