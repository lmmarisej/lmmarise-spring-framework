package org.lmmarise.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 5:35 下午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LmmResponseBody {
}
