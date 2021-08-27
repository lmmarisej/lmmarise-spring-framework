package org.lmmarise.springframework.context.stereotype;

import org.lmmarise.springframework.annotation.LmmAliasFor;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:18 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@LmmComponent
public @interface LmmRepository {

    /**
     * 实现 Spring 中注解的继承
     * <p>
     * LmmRepository 注解继承 LmmComponent，则拥有 LmmComponent 的功能
     */
    @LmmAliasFor(annotation = LmmComponent.class)
    String value() default "";

}
