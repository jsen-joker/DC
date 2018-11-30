package com.dryork.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018/4/2
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.dryork.controller.*.*.*(..))")
    public void webLog() {}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("API============API");
        logger.info("URL:" + request.getRequestURL().toString());
        logger.info("HTTP_METHOD:" + request.getMethod());
        logger.info("IP:" + request.getRemoteAddr());
        logger.info("CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName());
        logger.info("ARGS:" + Arrays.toString(joinPoint.getArgs()));
        String traceId = UUID.randomUUID().toString();
        logger.info("doBefore traceId:" + traceId);
        logger.info("API============API");


        request.setAttribute("start", System.currentTimeMillis());
        request.setAttribute("traceId", traceId);
    }

    @After("webLog()")
    public void doAfter(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("APIAfter============APiiIAfter");
        logger.info("trace: " + request.getAttribute("traceId") + " running time:" + (System.currentTimeMillis() - ((Long) request.getAttribute("start"))));
        logger.info("APIAfter============APIAfter");
    }

//    @AfterReturning(returning = "ret", pointcut = "webLog()")
//    public void doAfterReturn(Object ret) {
//        logger.warn("doAfterReturn start...");
//
//        logger.info("doAfterReturn return:" + ret);
//
//        logger.warn("doAfterReturn end...");
//
//    }

//    @Around("webLog()")
//    public Object doAround(ProceedingJoinPoint pjp) {
//        logger.warn("doAround start...");
//
//        try {
//            Object o = pjp.proceed();
//            logger.info("doAround result:" + o);
//
//            logger.warn("doAround end...");
//            return o;
//        } catch (Throwable throwable) {
//            Throwables.throwIfUnchecked(throwable);
//            throwable.printStackTrace();
//
//            logger.warn("doAround end exception...");
//            return null;
//        }
//
//    }
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
