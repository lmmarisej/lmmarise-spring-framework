package org.lmmarise.springframework.context.stereotype;

import java.lang.annotation.*;

/**
 * 指示带注释的元素表示索引的原型。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:19 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Indexed {
}
