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
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
@Component
@Aspect
public class MulDsAspect {


    @Around("execution(public * com.dryork.dc.server.service.logical.impl.DcServerCoreServiceImpl.listenColumnChange(..))")
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
