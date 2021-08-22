package org.lmmarise.springframework.beans.factory.annotation;

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
public @interface LmmController {
    String value() default "";
}
