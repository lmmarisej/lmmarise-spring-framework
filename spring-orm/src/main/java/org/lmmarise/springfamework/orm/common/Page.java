package org.lmmarise.springfamework.orm.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象，包含当前页数据及分页信息
 * <p>
 * 支持 JQuery、EasyUI 直接对接，支持 BootStrap Table 直接对接
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/25 3:42 下午
 */
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private int pageSize = DEFAULT_PAGE_SIZE;   // 每页记录数
    private long start;     // 当前页第一条数据在 List 中的位置
    private List<T> rows;   // 存放当前页记录
    private long total;     // 总记录数

    public Page() {
        this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList<>());
    }

    public Page(long start, long totalSize, int pageSize, List<T> rows) {
        this.pageSize = pageSize;
        this.start = start;
        this.total = totalSize;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPageCount() {
        if (total % pageSize == 0) {
            return total / pageSize;
        } else {
            return total / pageSize + 1;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    /**
     * 当前页页码
     */
    public long getPageNo() {
        return start / pageSize + 1;
    }

    public boolean hasNextPage() {
        return this.getPageNo() < this.getTotalPageCount() - 1;
    }

    public boolean hasPreviousPage() {
        return this.getPageNo() > 1;
    }

    /**
     * 默认页大小，任意一页第一条数据在数据集中的位置
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    /**
     * 任意一页第一条数据在数据集的位置
     *
     * @param pageNo   页码，从1开始
     * @param pageSize 页大小
     * @return 该页第一条数据在数据集中的位置
     */
    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }

}
