package org.lmmarise.springfamework.orm.common;

/**
 * 封装排序规则
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/25 8:38 下午
 */
public class Order {
    private String propertyName;    // 排序字段
    private boolean ascending;      // 升序降序

    @Override
    public String toString() {
        return propertyName + ' ' + (ascending ? "asc" : "desc");
    }

    public Order(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }


}
