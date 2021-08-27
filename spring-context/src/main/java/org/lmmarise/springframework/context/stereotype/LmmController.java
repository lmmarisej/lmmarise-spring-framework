package org.lmmarise.springframework.context.stereotype;

import org.lmmarise.springframework.annotation.LmmAliasFor;

import java.lang.annotation.*;

/**
 * 页面交互
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 9:44 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@LmmComponent
public @interface LmmController {

    @LmmAliasFor(annotation = LmmComponent.class)
    String value() default "";

}
