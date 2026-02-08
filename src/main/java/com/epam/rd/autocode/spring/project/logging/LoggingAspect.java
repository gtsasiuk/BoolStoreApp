package com.epam.rd.autocode.spring.project.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("within(com.epam.rd.autocode.spring.project.controller..*)")
    public void controllerLayer() {}

    @Pointcut("within(com.epam.rd.autocode.spring.project.service.impl..*)")
    public void serviceLayer() {}

    @Before("controllerLayer()")
    public void logControllerBefore(JoinPoint joinPoint) {
        log.info("[CONTROLLER] Enter: {} | args={}",
                joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(value = "controllerLayer()", returning = "result")
    public void logControllerAfter(JoinPoint joinPoint, Object result) {
        log.info("[CONTROLLER] Exit: {} | result={}",
                joinPoint.getSignature(), result);
    }

    @Before("serviceLayer()")
    public void logServiceBefore(JoinPoint joinPoint) {
        log.info("[SERVICE] Enter: {} | args={}",
                joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(value = "serviceLayer()", returning = "result")
    public void logServiceAfter(JoinPoint joinPoint, Object result) {
        log.info("[SERVICE] Exit: {} | result={}",
                joinPoint.getSignature(), result);
    }

    @AfterThrowing(value = "controllerLayer() || serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("[ERROR] {} threw {}: {}",
                joinPoint.getSignature(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }
}
