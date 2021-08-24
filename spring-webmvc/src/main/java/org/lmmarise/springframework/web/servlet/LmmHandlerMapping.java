package org.lmmarise.springframework.web.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 策略模式。
 * <p>
 * 保存 URL 和 目标方法 的对应关系。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 1:20 下午
 */
public class LmmHandlerMapping {
    private Pattern pattern;    // URL 封装
    private Object controller;  // 目标方法所在的 Controller 对象
    private Method method;      // URL 对于的访问方法

    public LmmHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
