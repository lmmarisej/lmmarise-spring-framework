package org.lmmarise.springframework.context.stereotype;

import java.lang.annotation.*;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:18 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@LmmComponent
public @interface LmmRepository {
}
