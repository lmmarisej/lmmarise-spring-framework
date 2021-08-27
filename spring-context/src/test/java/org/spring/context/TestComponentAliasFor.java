package org.spring.context;

import org.junit.Test;
import org.lmmarise.springframework.annotation.LmmAliasFor;
import org.lmmarise.springframework.context.stereotype.LmmComponent;
import org.lmmarise.springframework.context.stereotype.LmmController;
import org.lmmarise.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/27 4:30 下午
 */
public class TestComponentAliasFor {

    private static final Logger log = LoggerFactory.getLogger(TestComponentAliasFor.class);

    @LmmController("a")
    static class A {
    }

    @Test
    public void getAnnotationMethod() throws NoSuchMethodException {
        LmmController controller = A.class.getAnnotation(LmmController.class);
        Class<? extends Annotation> controllerAnnoType = controller.annotationType();
        Assert.isTrue(controllerAnnoType.isAnnotationPresent(LmmComponent.class), "LmmController注解上没有LmmComponent注解");
        Method value = controllerAnnoType.getMethod("value");
        Assert.isTrue(value.isAnnotationPresent(LmmAliasFor.class), "LmmComponent注解value方法上没有LmmAliasFor注解");
    }
    
}
