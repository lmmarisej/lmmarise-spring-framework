package org.lmmarise.springframework.web.servlet;

import org.lmmarise.springframework.beans.factory.annotation.LmmRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求传递到服务端的参数列表与 Method 实例列表的对应关系，完成参数值的类型转换工作
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 2:16 下午
 */
public class LmmHandlerAdapter {

    Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理本次请求的策略
        LmmHandlerMapping handlerMapping = (LmmHandlerMapping) handler;

        // 方法形参列表
        HashMap<String, Integer> paramMapping = new HashMap<>();

        // 记录注解参数顺序；注解参数下标与方法中位置一样，前面没有使用注解的参数也会占用位置。
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            Annotation[] annotations = pa[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof LmmRequestParam) {
                    String paramName = ((LmmRequestParam) annotation).value();
                    paramMapping.put(paramName, i);
                }
            }
        }

        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();

        // 记录未使用参数注解的参数顺序
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramMapping.put(type.getName(), i);
            }
        }

        Map<String, String[]> reqParamMap = request.getParameterMap();
        // 实参列表
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : reqParamMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramMapping.get(param.getKey());
            paramValues[index] = value;
        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        // 取出 Controller、Method 使用反射进行调用
        return handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
    }

    boolean supports(Object handler) {
        return (handler instanceof LmmHandlerMapping);
    }

}
