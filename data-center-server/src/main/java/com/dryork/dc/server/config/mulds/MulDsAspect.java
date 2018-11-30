package com.dryork.dc.server.config.mulds;

import com.dryork.config.mulds.DataSourceContextHolder;
import com.dryork.constants.Constants;
import com.google.common.base.Throwables;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * <p>
 *     自动切换到dc存储数据的数据源
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
@Component
@Aspect
public class MulDsAspect {


    @Around("execution(public * com.dryork.dc.server.service.logical.impl.DcServerCoreServiceImpl.*(..)) || execution(public * com.dryork.dc.server.service.logical.impl.DcCoreDbHandleServiceImpl.*(..))")
    public Object doDsChangeAspect(ProceedingJoinPoint pjp) {
        DataSourceContextHolder.setDB(Constants.DC);
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            Throwables.throwIfUnchecked(throwable);
            throwable.printStackTrace();
        } finally {
            DataSourceContextHolder.clearDB();
        }
        return null;
    }
}
