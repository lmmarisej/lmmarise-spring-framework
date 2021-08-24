package org.lmm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 9:54 下午
 */
public class Test {

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("\\$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("${addd}${aaaccc}");
        while (matcher.find()) {
            String paramName = matcher.group();
            paramName = paramName.replaceAll("\\$\\{|\\}", "cxk");
            System.out.println(paramName);
        }
    }

}
