package org.lmmarise.springfamework.orm.annotation;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 10:29 上午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String name();
}
