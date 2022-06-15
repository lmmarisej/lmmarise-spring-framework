## 介绍
    完全手写实现 spring framework 的核心模块。

## 约定
    所有的手写类都以 Lmm 开头，以区别于 Spring 框架中的原生类。

## 功能已实现
1. IoC。
   1. DI。
2. AOP。
   1. 前置通知。
   2. 后置通知。
   3. 异常通知。
3. MVC。
4. ORM，过于复杂，半成品。

## 功能待实现
1. 当populationBean时，若autowired字段Bean在容器中不存在，先尝试递归创建字段Bean。✅
2. 加入Bean的单例多例模式
3. 通知。
   1. 环绕通知。
   2. 最终通知。
4. 事件。
   1. 同步事件。✅
   2. 事件异步，多线程实现。
5. Bean注入容器自定义BeanName。
6. Bean根据抽象类型注入。✅
7. 根据泛型注入。
8. 使用三级缓存解决循环依赖。
9. 事物。

## 使用方法
参照demo模块：

![img_4.png](img_4.png)

## 测试MVC
### 启动
![img_2.png](img_2.png)

### DEMO

#### MVC

http://localhost:8080/web/queryAll.json <br/>
![img.png](img.png)

http://localhost:8080/web/first.html?name=cxk   <br/>
![img_1.png](img_1.png)

http://localhost:8080/web/findById.json?id=12   <br/>
![img_3.png](img_3.png)

#### 事件

http://localhost:8080/web/findById.json?id=12

![img_5.png](img_5.png)

![img_6.png](img_6.png)


