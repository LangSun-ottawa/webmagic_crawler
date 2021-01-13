package com.langsun.job.pojo;

import java.io.Serializable;
import java.util.List;

public class JobResult implements Serializable {
    private List<JobInfoField> rows;
    private Integer pageTotal;
    private Integer totalHits;
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public List<JobInfoField> getRows() {
        return rows;
    }

    public void setRows(List<JobInfoField> rows) {
        this.rows = rows;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    @Override
    public String toString() {
        return "JobResult{" +
                "rows=" + rows +
                ", pageTotal=" + pageTotal +
                ", totalHits=" + totalHits +
                ", success='" + success + '\'' +
                '}';
    }
}
