package org.lmmarise.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 9:47 下午
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LmmRequestParam {
    String value() default "";
}
