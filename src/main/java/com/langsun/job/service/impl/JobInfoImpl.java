package com.langsun.job.service.impl;

import com.langsun.job.dao.JobInfoDao;
import com.langsun.job.pojo.JobInfo;
import com.langsun.job.service.JobInfoService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class JobInfoImpl implements JobInfoService {
    @Autowired
    private JobInfoDao jobInfoDao;

    @Override
    public void save(JobInfo jobInfo) {
        JobInfo param = new JobInfo();
        param.setJobAddr(jobInfo.getJobAddr());
        param.setCompanyName(jobInfo.getCompanyName());
        param.setJobTitle(jobInfo.getJobTitle());
//        param.setTime(jobInfo.getTime());
        List<JobInfo> list = findJobInfo(param);
        if (list.size() == 0) {
            jobInfoDao.saveAndFlush(jobInfo);
        }else {
            for (JobInfo info : list) {
                jobInfoDao.deleteById(info.getId());
                info.setJobInfo(jobInfo.getJobInfo());
            }
            jobInfoDao.saveAndFlush(jobInfo);
        }
    }

    @Override
    public List<JobInfo> findJobInfo(JobInfo jobInfo) {
        Example<JobInfo> example = Example.of(jobInfo);
        List<JobInfo> list = jobInfoDao.findAll(example);
        return list;
    }

    @Override
    public void delete(String oldTime) {
        jobInfoDao.deleteByTimeIsLessThanEqual(oldTime);
    }

    @Override
    public Page<JobInfo> findJobInfoByPage(int pageNum, int pageSize) {
        Page<JobInfo> jobInfos = jobInfoDao.findAll(PageRequest.of(pageNum, pageSize));
        return jobInfos;
    }
}
