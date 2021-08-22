package org.lmmarise.springframework.util;

/**
 * 对配置文件进行查找、读取、解析
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 6:21 下午
 */
public abstract class StringUtils {

    /**
     * 将字符串首字母转为小写
     */
    public static String toLowerCaseFirstCase(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }
        if (str.charAt(0) > 64 && str.charAt(0) < 91) {
            str = (char) (str.charAt(0) + 32) + str.substring(1);
        }
        return str;
    }

}