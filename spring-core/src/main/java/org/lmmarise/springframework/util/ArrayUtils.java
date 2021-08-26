package org.lmmarise.springframework.util;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 2:37 下午
 */
public abstract class ArrayUtils {
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
}
