package org.lmmarise.springframework.context.stereotype;

import org.lmmarise.springframework.core.annotation.LmmAliasFor;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:21 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@LmmComponent
public @interface LmmService {

    @LmmAliasFor(annotation = LmmComponent.class)
    String value() default "";

}
