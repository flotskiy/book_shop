package com.github.flotskiy.bookshop.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class AnnotatedLoggingAspect {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "@annotation(com.github.flotskiy.bookshop.aspect.annotations.EntityCreationControllable)")
    public void entityCreationControlPointcut() {
    }

    @AfterReturning(pointcut = "entityCreationControlPointcut()")
    public void afterReturningEntityCreation() {
        logger.info("ENTITY CREATED SUCCESSFULLY");
    }

    @AfterThrowing(pointcut = "entityCreationControlPointcut()", throwing = "exception")
    public void afterThrowingEntityCreation(Exception exception) {
        logger.info(exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }

    @Pointcut(value = "@annotation(com.github.flotskiy.bookshop.aspect.annotations.EntityAccessControllable)")
    public void entityAccessControlPointcut() {
    }

    @AfterThrowing(pointcut = "entityAccessControlPointcut()", throwing = "exception")
    public void afterThrowingEntityAccess(Exception exception) {
        logger.info(exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }
}
