package com.langsun.job.dao;

import com.langsun.job.pojo.JobInfoField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JobRepository extends ElasticsearchRepository<JobInfoField, Long> {
    void deleteByTimeIsLessThanEqual(String time);

}
