package com.langsun.job.dao;

import com.langsun.job.pojo.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JobInfoDao extends JpaRepository<JobInfo, Long> {

    void deleteByTime(String oldTime);

    void deleteByTimeIsLessThanEqual(String oldTime);
}
