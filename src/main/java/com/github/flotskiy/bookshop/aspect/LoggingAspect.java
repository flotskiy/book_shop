package com.github.flotskiy.bookshop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Before(value = "within(com.github.flotskiy.bookshop.*..*Service)")
    public void beforeServiceMethodFixingExecution(JoinPoint joinPoint) {
        logger.info("\u2B07\t STARTED: " + joinPoint.toShortString());
    }

    @Before(value = "within(com.github.flotskiy.bookshop.controllers.*..*Controller)")
    public void beforeControllerMethodFixingExecution(JoinPoint joinPoint) {
        logger.info("\u25B6\t STARTED: " + joinPoint.toShortString());
    }

    @Pointcut("execution(* com.github.flotskiy.bookshop.repository.*Repository.save(..))")
    public void saveEntityInRepository() {}

    @After("saveEntityInRepository()")
    public void afterEntitySavedInRepository(JoinPoint joinPoint) {
        logger.info("\uD83C\uDD97\t SAVED " + joinPoint.getArgs()[0].getClass().getSimpleName() + ": "
                + joinPoint.toShortString());
    }

    @Pointcut("execution(* com.github.flotskiy.bookshop.repository.*Repository.delete*(..))")
    public void deleteEntityFromRepository() {}

    @After("deleteEntityFromRepository()")
    public void afterEntityDeletedFromRepository(JoinPoint joinPoint) {
        logger.info("\u274C\t DELETED: " + joinPoint.toShortString());
    }

    @AfterReturning(value = "execution(public * changeBookStatus(..))")
    public void afterReturningHandleChangeBookStatus() {
        logger.info("Book status SUCCESSFULLY changed");
    }

    @AfterThrowing(value = "execution(public * changeBookStatus(..))", throwing = "throwable")
    public void afterThrowingHandleChangeBookStatus(Throwable throwable) {
        logger.info(throwable.getMessage());
    }
}
