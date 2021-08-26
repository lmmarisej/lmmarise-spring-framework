package org.lmmarise.springfamework.orm.annotation;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 8:11 下午
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
}
