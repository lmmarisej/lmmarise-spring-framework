package org.lmmarise.springframework.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lmmarise.springframework.context.stereotype.LmmController;
import org.lmmarise.springframework.web.bind.annotation.LmmRequestMapping;
import org.lmmarise.springframework.web.bind.annotation.LmmResponseBody;
import org.lmmarise.springframework.context.LmmApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MVC 启动入口
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 3:33 下午
 */
public class LmmDispatcherServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LmmDispatcherServlet.class);

    private final String LOCATION = "contextConfigLocation";
    private final List<LmmHandlerMapping> handlerMappings = new ArrayList<>();
    private final Map<LmmHandlerMapping, LmmHandlerAdapter> handlerAdapters = new HashMap<>();
    private final List<LmmViewResolver> viewResolvers = new ArrayList<>();
    private LmmApplicationContext context;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        context = new LmmApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    /**
     * 九大组件
     */
    private void initStrategies(LmmApplicationContext context) {
        initMultipartResolver(context); // 文件上传
        initLocaleResolver(context);    // 本地化
        initThemeResolver(context);     // 主题解析
        initHandlerMappings(context);   // 请求映射到处理器
        initHandlerAdapters(context);   // 多类型参数动态匹配
        initHandlerExceptionResolvers(context);     // 处理执行过程中的异常
        initRequestToViewNameTranslator(context);   // 直接将请求解析到视图名
        initViewResolvers(context);     // 逻辑视图解析到具体视图实现
        initFlashMapManager(context);   // Flash 映射管理器
    }

    private void initFlashMapManager(LmmApplicationContext context) {
    }

    private void initViewResolvers(LmmApplicationContext context) {
        // 页面与模板关联
        String templateRoot = context.getConfig().getProperty("templateRoot");
        URL resource = this.getClass().getClassLoader().getResource(templateRoot);
        if (resource == null) {
            throw new RuntimeException("templateRoot[" + templateRoot + "] in classpath not found.");
        }
        String templateRootPath = resource.getFile();
        if (templateRootPath == null) {
            throw new RuntimeException("initViewResolvers Failed! Cause : The 'templateRoot=" + templateRoot + "' can't found in classpath.");
        }
        File templateRootDir = new File(templateRootPath);
        if (!templateRootDir.isDirectory()) {
            throw new RuntimeException("templateRootPath[" + templateRootPath + "] is not a directory!");
        }
        for (File template : Objects.requireNonNull(templateRootDir.listFiles())) {
            this.viewResolvers.add(new LmmViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(LmmApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(LmmApplicationContext context) {
    }

    /**
     * 初始化阶段，将参数名字或类型按照一定的顺序保存下来，便于反射调用时传参
     */
    private void initHandlerAdapters(LmmApplicationContext context) {
        for (LmmHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new LmmHandlerAdapter());
        }
    }

    private void initThemeResolver(LmmApplicationContext context) {
    }

    private void initLocaleResolver(LmmApplicationContext context) {
    }

    private void initMultipartResolver(LmmApplicationContext context) {
    }

    /**
     * Controller 中配置的 RequestMapping 和 Method 进行对应
     */
    private void initHandlerMappings(LmmApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if (!clazz.isAnnotationPresent(LmmController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(LmmRequestMapping.class)) {
                LmmRequestMapping requestMapping = clazz.getAnnotation(LmmRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = clazz.getMethods();      // 所有的 public 方法
            for (Method method : methods) {
                if (!method.isAnnotationPresent(LmmRequestMapping.class)) {
                    continue;
                }
                LmmRequestMapping requestMapping = method.getAnnotation(LmmRequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value())
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new LmmHandlerMapping(pattern, controller, method));
                log.info("Mapping: " + regex + ", " + method);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.setCharacterEncoding("utf-8");
            resp.getWriter().write(
                    "<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" +
                            Arrays.toString(e.getStackTrace())
                                    .replaceAll("\\[|\\]", "")
                                    .replaceAll("\\s", "\r\n") +
                            "<font color='green'><i>Copyright@Lmmarise.j<i/></font>"
            );
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        LmmHandlerMapping handler = getHandler(req);
        if (handler == null) {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("404");
            return;
        }
        LmmHandlerAdapter ha = getHandlerAdapter(handler);
        // 获得 controller 中具体调用的方法返回值
        Object result = Objects.requireNonNull(ha).handle(req, resp, handler);
        // 响应
        // json 格式响应
        if (handler.getController().getClass().isAnnotationPresent(LmmResponseBody.class) ||
                handler.getMethod().isAnnotationPresent(LmmResponseBody.class)) {
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(mapper.writeValueAsString(result));
        }
        // 模板引擎处理并响应
        else if (result instanceof LmmModelAndView) {
            templateProcessDispatchResult(req, resp, (LmmModelAndView) result);
        }
        // 普通字符串
        else {
            resp.setContentType("text/plain;charset=utf-8");
            resp.getWriter().write(result.toString());
        }
    }

    private void templateProcessDispatchResult(HttpServletRequest req, HttpServletResponse resp, LmmModelAndView mv) throws Exception {
        if (mv == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (LmmViewResolver viewResolver : this.viewResolvers) {
            LmmView view = viewResolver.resolveViewName(mv.getViewName(), null);
            if (view != null) {
                view.render(mv.getModel(), req, resp);
                return;
            }
        }
    }

    private LmmHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (LmmHandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    private LmmHandlerAdapter getHandlerAdapter(LmmHandlerMapping handler) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        LmmHandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);
        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class || clazz == int.class) {
            return Integer.valueOf(value);
        } else {
            return null;
        }
    }

}
