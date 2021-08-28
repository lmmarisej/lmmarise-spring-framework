package org.lmmarise.springframework.context.stereotype;

import org.lmmarise.springframework.context.LmmApplicationContext;
import org.lmmarise.springframework.core.annotation.LmmAliasFor;

import java.lang.annotation.*;

/**
 * 只要使用了本注解就的类，就属于能被 IoC 容器托管的 Bean
 *
 * <code>@LmmService、@LmmRepository</code> 等都是 @LmmComponent 的扩展，想要自定义扩展 @LmmComponent 注解以得到 lmmarise-spring
 * 的支持，请参考 {@link LmmController} 搭配 {@link LmmAliasFor} 的实现 {@link LmmComponent} 的扩展方法。以同样的方法进行扩展自定义
 * 注解，将同样能得到 lmmarise-spring 容器按照 spring-bean 处理被注解的类的支持。
 *
 * <pre>
 * 实现与 LmmComponent 功能相同的自定义注解的步骤：
 *      1.直接在自定义注解上加上 @LmmComponent。
 *          即可实现判断是否作为Spring-Bean解析CLass，判断条件将递归自底向上地查找是否有 @LmmComponent 注解。
 *          有则是 Spring-Bean，将会自动注入 Bean 的依赖，并将其放入 IoC 容器。
 * </pre>
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

    /**
     * todo 待实现：根据value值自定义放入 IoC 容器的 BeanName
     * 思路：当 Autowired 处理完成，判断 value 值，根据 value 值设置 BeanName
     */
    String value() default "";

}
