package com.langsun.job.service;

import com.langsun.job.pojo.JobInfo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface JobInfoService {

    /**
     * @param
     * @author: LangSun
     * @createDate: 2020/12/5 10:20
     * @return:
     */
    public void save(JobInfo jobInfo);

    /**
     * @param jobInfo : JobInfo
     * @return : List
     * @author : LangSun
     * @date : 2020/12/5 10:31
     */
    public List<JobInfo> findJobInfo(JobInfo jobInfo);


    void delete(String oldTime);

    Page<JobInfo> findJobInfoByPage(int pageNum, int pageSize);
}
