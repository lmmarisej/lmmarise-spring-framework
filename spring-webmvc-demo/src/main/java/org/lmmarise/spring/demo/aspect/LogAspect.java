package org.lmmarise.spring.demo.aspect;

import org.lmmarise.aopalliance.intercept.LmmJoinpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 织入切入面逻辑，针对目标方法进行增强
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 7:03 下午
 */
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 调用方法前执行
     */
    public void before(LmmJoinpoint joinpoint) {
        joinpoint.setUserAttribute("startTime_" + joinpoint.getMethod().getName(), System.currentTimeMillis());
        log.info("前置通知：Invoker Before Method." + "\nTargetObject:" + joinpoint.getThis() + "\nArgs:" + Arrays.toString(joinpoint.getArguments()));
    }

    /**
     * 调用方法后执行
     */
    public void after(LmmJoinpoint joinpoint) {
        log.info("后置通知：Invoker After Method." + "\nTargetObject:" + joinpoint.getThis() + "\nArgs:" + Arrays.toString(joinpoint.getArguments()));
        long startTime = (long) joinpoint.getUserAttribute("startTime_" + joinpoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("use time:" + (endTime - startTime));
    }

    public void afterThrowing(LmmJoinpoint joinpoint, Throwable ex) {
        log.warn("异常通知：" + "\nTargetObject:" + joinpoint.getThis() + "\nArgs:" + Arrays.toString(joinpoint.getArguments()) + "\nThrows:" + ex.getMessage());
    }

}
