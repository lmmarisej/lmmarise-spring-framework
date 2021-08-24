package org.lmmarise.springframework.web.servlet;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

/**
 * 模板名称和模板解析引擎匹配
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 2:11 下午
 */
public class LmmViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private final File templateRootDir;
    private String viewName;

    public LmmViewResolver(String templateRoot) {
        String templateRootPath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(templateRoot)).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public LmmView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new LmmView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
