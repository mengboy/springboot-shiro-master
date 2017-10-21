package com.study.model;

public class Request {
    private long pageSize;

    private long requestPage;

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getRequestPage() {
        return requestPage;
    }

    public void setRequestPage(long requestPage) {
        this.requestPage = requestPage;
    }
}
