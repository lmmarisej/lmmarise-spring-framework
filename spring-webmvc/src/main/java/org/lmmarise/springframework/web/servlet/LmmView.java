package org.lmmarise.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义模板解析引擎
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 2:16 下午
 */
public class LmmView {

    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private final File viewFile;

    public LmmView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    /**
     * 模板渲染，通过 Response 返回浏览器可识别的字符串
     */
    public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r")) {
            String line;
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                Pattern pattern = Pattern.compile("\\$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("\\$\\{|\\}", "");
                    Object paramValue = model.get(paramName);
                    if (paramValue == null) {
                        continue;
                    }
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        }
        resp.setCharacterEncoding("utf-8");
        resp.setContentType(DEFAULT_CONTENT_TYPE);
        resp.getWriter().write(sb.toString());
    }

    /**
     * 处理特殊字符串
     */
    private String makeStringForRegExp(String str) {
        return str
                .replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("^", "\\^")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("?", "\\?")
                .replace("$", "\\$")
                .replace(".", "\\.")
                .replace("&", "\\&")
                .replace(",", "\\,");
    }
}
