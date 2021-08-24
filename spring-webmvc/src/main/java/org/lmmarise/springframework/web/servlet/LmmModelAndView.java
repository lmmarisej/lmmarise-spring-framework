package org.lmmarise.springframework.web.servlet;

import java.util.Map;

/**
 * 封装页面模板和页面参数
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 2:10 下午
 */
public class LmmModelAndView {
    private Object viewName;        // 页面模板名称
    private Map<String, ?> model;   // 页面参数

    public LmmModelAndView(Object viewName) {
        this(viewName, null);
    }

    public LmmModelAndView(Object viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public LmmModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return (this.viewName instanceof String ? (String) this.viewName : null);
    }

    public void setViewName(Object viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
