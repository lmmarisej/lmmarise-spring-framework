package org.lmmarise.springfamework.orm.annotation;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 8:13 下午
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String name();

    boolean insertable() default true;

    boolean updatable() default true;
}
