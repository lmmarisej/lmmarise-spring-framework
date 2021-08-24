package org.lmmarise.aopalliance.intercept;

import java.lang.reflect.Method;

/**
 * 回调连接点，获得被代理业务的业务方法的所有信息
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 1:33 下午
 */
public interface LmmJoinpoint {

    Method getMethod();         // 业务方法本身

    Object[] getArguments();    // 方法实参列表

    Object getThis();           // 该方法所属的实例对象

    void setUserAttribute(String key, Object value);    // Joinpoint 自定义属性

    Object getUserAttribute(String key);                // 从已添加的自定义属性中获取一个属性值

}
