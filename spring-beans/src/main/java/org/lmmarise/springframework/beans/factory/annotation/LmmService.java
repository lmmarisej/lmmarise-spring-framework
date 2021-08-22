package org.lmmarise.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑，注入接口
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 9:37 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LmmService {
    String value() default "";
}
