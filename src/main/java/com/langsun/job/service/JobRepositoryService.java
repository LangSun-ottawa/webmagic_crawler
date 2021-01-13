package com.langsun.job.service;

import com.langsun.job.pojo.JobInfoField;
import com.langsun.job.pojo.JobResult;

import java.util.List;

public interface JobRepositoryService {
    /**
     * @param jobInfoField : elastic Search jobInfo pojo
     * @date : 2020/12/17 9:48
     */
    void save(JobInfoField jobInfoField);

    void saveAll(List<JobInfoField> list);

    void deleteByDate(String date);

    /**
     * search according to requirements from frontend
     * @param
     * @return
     * @author : LangSun
     * @date : 2021/1/2 23:07
     */
    JobResult search(String location, Integer page, String rate, String keyWord);
}
