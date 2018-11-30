package com.dryork.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
@Service
public interface DcCoreMapper {

    /**
     * 执行非等幂操作
     * @param sql sql
     * @return eff
     */
    int sqlCUD(@Param("sql") String sql);

    /**
     * 获取一个list
     * @param sql sql
     * @return result
     */
    List<Map> sqlList(@Param("sql") String sql);

    /**
     * 获取一条记录
     * @param sql sql
     * @return result
     */
    Map sqlOne(@Param("sql") String sql);

}
