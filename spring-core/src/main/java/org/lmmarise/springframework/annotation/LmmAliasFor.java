package org.lmmarise.springframework.annotation;

import java.lang.annotation.*;

/**
 * 声明注释属性别名的注释。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/27 4:24 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LmmAliasFor {

    Class<? extends Annotation> annotation() default Annotation.class;

}
