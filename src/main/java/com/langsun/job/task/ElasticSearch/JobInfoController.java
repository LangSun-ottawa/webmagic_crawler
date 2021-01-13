package com.langsun.job.task.ElasticSearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langsun.job.pojo.*;
import com.langsun.job.service.JobInfoService;
import com.langsun.job.service.JobRepositoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/myElastic")
public class JobInfoController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @PutMapping
    public void createIndex() {
        elasticsearchTemplate.createIndex(JobInfoField.class);
        elasticsearchTemplate.putMapping(JobInfoField.class);
    }

    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private JobRepositoryService jobRepositoryService;

    @PostMapping
    public String jobInfoDataInsert() {
        //initial page
        int p = 0;

        int pageSize =0;

        do {
            Page<JobInfo> page = jobInfoService.findJobInfoByPage(p, 500);
            List<JobInfoField> list = new ArrayList<>();

            for (JobInfo jobInfo : page.getContent()) {
                JobInfoField jobInfoField = new JobInfoField();
                BeanUtils.copyProperties(jobInfo, jobInfoField);
                list.add(jobInfoField);
            }
            jobRepositoryService.saveAll(list);
            p++;
            pageSize = page.getContent().size();
        } while (pageSize == 500);

        return "insert done";
    }

    @DeleteMapping(value ="/d1l1te")
    public String deleteAll() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_YEAR, -12);
        Date time = rightNow.getTime();
        String format = simpleDateFormat.format(time);
        jobRepositoryService.deleteByDate(format);
        return "delete done";
    }

    @PostMapping(value = "/test")
    @CrossOrigin
    public String test(@RequestBody MyTest test) {
        System.out.println(test);
        test.setState("success!!");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String result = objectMapper.writeValueAsString(test);
            return result;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "convert failure";
        }
    }

    @PostMapping(value = "/search")
    @CrossOrigin
    public String search(@RequestBody Params params) {
        System.out.println(params);
        Integer page = params.getPage() - 1;

        if (page == null || page < 0) {
            params.setPage(0);
        }else {
            params.setPage(page);
        }
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JobResult result = jobRepositoryService.search(params.getLocation(), params.getPage(), params.getRate(), params.getKeyWord());
            System.out.println(result);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":\"no\"}";
        }
    }

}
