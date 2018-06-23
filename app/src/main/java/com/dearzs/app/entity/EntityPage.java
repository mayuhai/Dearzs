package com.dearzs.app.entity;

/**
 * 接口分页实体类
 * @author 张海龙
 * @version 1.0
 */
public class EntityPage {

    private int PageCount;
    private int PageIndex;
    private int PageSize;
    private int RowCount;

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public int getRowCount() {
        return RowCount;
    }

    public void setRowCount(int rowCount) {
        RowCount = rowCount;
    }
}
