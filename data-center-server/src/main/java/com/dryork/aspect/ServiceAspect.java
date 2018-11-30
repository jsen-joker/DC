package com.dryork.aspect;

import com.google.common.base.Throwables;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018/4/2
 */
@Aspect
@Component
public class ServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAspect.class);

    @Pointcut("execution(public * com.dryork.service.*.*.*(..))")
    public void serviceLog() {}


//    @Before("serviceLog()")
//    public void doBefore(JoinPoint joinPoint) {
//        logger.info("CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName());
//    }
//
//    @After("serviceLog()")
//    public void doAfter(JoinPoint joinPoint) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//        logger.info("APIAfter============APiiIAfter");
//        logger.info("trace: " + request.getAttribute("traceId") + " running time:" + (System.currentTimeMillis() - ((Long) request.getAttribute("start"))));
//        logger.info("APIAfter============APIAfter");
//    }

//    @AfterReturning(returning = "ret", pointcut = "webLog()")
//    public void doAfterReturn(Object ret) {
//        logger.warn("doAfterReturn start...");
//
//        logger.info("doAfterReturn return:" + ret);
//
//        logger.warn("doAfterReturn end...");
//
//    }

    @Around("serviceLog()")
    public Object doAround(ProceedingJoinPoint pjp) {
        logger.info("CLASS_METHOD:" + pjp.getSignature().getDeclaringTypeName());
        long start = System.currentTimeMillis();
        try {
            Object o = pjp.proceed();
            return o;
        } catch (Throwable throwable) {
            Throwables.throwIfUnchecked(throwable);
            throwable.printStackTrace();
            logger.warn("doAround end exception...");
        }

        logger.info(pjp.getSignature().getDeclaringTypeName() + " spend : " + (System.currentTimeMillis() - start));
        return null;

    }
//
//    @AfterThrowing("webLog()")
//    public void afterThrow(JoinPoint joinPoint) {
//        logger.warn("afterThrow start...");
//
//        logger.info("afterThrow exception");
//
//        logger.warn("afterThrow end...");
//
//    }

}
