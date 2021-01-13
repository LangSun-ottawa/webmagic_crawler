package com.langsun.job.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Document(indexName = "jobinfo",type = "jobInfoField")
public class JobInfoField {
    @Id
    @Field(index = true, store = true, type = FieldType.Long)
    private Long id;

    @Field(index = false,store = true,type =FieldType.Text)
    private String companyName;

    private String companyAddr;
    private String companyInfo;

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String jobTitle;

    @Field(index = true,store = true,type =FieldType.Text)
    private String jobAddr;

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String jobInfo;

    private String jobType;

    @Field(index = true, store = true,type = FieldType.Text)
    private String url;

    @Field(index = true, store = true, type = FieldType.Text)
    private String time;

    private Integer salaryMin;
    private Integer salaryMax;

    @Field(index = true, store = true, type = FieldType.Text)
    private String rate;

    @Override
    public String toString() {
        return "JobInfo{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", companyAddr='" + companyAddr + '\'' +
                ", companyInfo='" + companyInfo + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobAddr='" + jobAddr + '\'' +
                ", jobInfo='" + jobInfo + '\'' +
                ", jobType='" + jobType + '\'' +
                ", url='" + url + '\'' +
                ", time='" + time + '\'' +
                ", rate=" + rate +
                '}';
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(String companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobAddr() {
        return jobAddr;
    }

    public void setJobAddr(String jobAddr) {
        this.jobAddr = jobAddr;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }
}
