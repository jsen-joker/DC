package com.dryork.dc.core.service.logical;

import com.dryork.dc.core.utils.DcLogger;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *     dc 数据库处理，客户端和dc服务端都要实现这个接口，并注册到spring
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
public interface DcCoreDbHandleService {

    /**
     * 获取一个list
     * @param sql sql
     * @return result
     */
    List<Map> sqlList(String sql, DcLogger.LOG log);
    /**
     * 获取一条记录
     * @param sql sql
     * @return result
     */
    Map sqlOne(String sql, DcLogger.LOG log);
    /**
     * 执行非等幂操作
     * @param sql sql
     * @return eff
     */
    int sqlCUD(String sql, DcLogger.LOG log);
}
