package tech.zetapioneers.loan_application.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* tech.zetapioneers.loan_application.concreteservice.*.*(..))")
    public Object loggerBeforeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before method execution {}",joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("After method Execution {}",joinPoint.getSignature().getName());
        return result;
    }

    @AfterThrowing(pointcut = "execution(* tech.zetapioneers.loan_application.concreteservice.*.*(..))",throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint , Exception ex){
        logger.error("Method : " + joinPoint.getSignature().getName() + " threw exception : " + ex);
    }
}
