package org.lmmarise.springframework.context.stereotype;

import org.lmmarise.springframework.context.LmmApplicationContext;

import java.lang.annotation.*;

/**
 * 只要使用了本注解就的类，就属于能被 IoC 容器托管的 Bean
 *
 * {@link LmmApplicationContext#populationBean(java.lang.String, java.lang.Object)}
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:18 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface LmmComponent {

    String value() default "";

}
