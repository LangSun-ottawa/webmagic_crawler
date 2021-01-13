package com.langsun.job.service.impl;

import com.langsun.job.dao.JobRepository;
import com.langsun.job.pojo.JobInfoField;
import com.langsun.job.pojo.JobResult;
import com.langsun.job.service.JobRepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JobRepositoryServiceImpl implements JobRepositoryService {
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void save(JobInfoField jobInfoField) {
        jobRepository.save(jobInfoField);
    }

    @Override
    public void saveAll(List<JobInfoField> list) {
        jobRepository.saveAll(list);
    }

    @Override
    public void deleteByDate(String date) {

    }

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public JobResult search(String location, Integer page, String rate, String keyWord) {
        int size = 15;
        JobResult frontPageResult = new JobResult();
        String rateMin;
        String rateMax;
        switch (rate) {
            case "4.5":
                rateMax = "5.0";
                rateMin = "4.5";
                break;
            case "4":
                rateMax = "4.5";
                rateMin = "4.0";
                break;
            case "3":
                rateMax = "4.0";
                rateMin = "3.0";
                break;
            case "2":
                rateMax = "3.0";
                rateMin = "0.0";
                break;
            default:
                rateMax = "5.0";
                rateMin = "0.0";
        }
//        System.out.println(rateMax + "~" + rateMin);

        if (StringUtils.isBlank(location) ||location.equals("0")) {
            location = "*";
        }

        if (StringUtils.isBlank(keyWord)) {
            keyWord = "*";
        }
        System.out.println("********");
        System.out.println(location);
        System.out.println(page);
        System.out.println(rateMax);
        System.out.println(rateMin);
        System.out.println(keyWord);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("rate").from(rateMin).to(rateMax).includeLower(true).includeUpper(true);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery().
                        must(QueryBuilders.queryStringQuery(keyWord).field("jobInfo"))
                        .must(QueryBuilders.queryStringQuery(location).field("jobAddr"))
                        .must(rangeQueryBuilder));
        NativeSearchQuery jobInfo = queryBuilder
                .withHighlightFields(new HighlightBuilder.Field("jobInfo").preTags("<font color='red'>").postTags("</font>"))
                .withPageable(PageRequest.of(page, size)).build();

        AggregatedPage<JobInfoField> jobInfoFields = elasticsearchTemplate.queryForPage(jobInfo, JobInfoField.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                int totalHits = (int)hits.getTotalHits();
                frontPageResult.setTotalHits(totalHits);
                frontPageResult.setPageTotal(pageHelper(totalHits,size));

                ArrayList<JobInfoField> list = new ArrayList<>();

                for (SearchHit hit : hits) {
                    Map<String, Object> result = hit.getSourceAsMap();
                    JobInfoField jobInfo = new JobInfoField();

                    Integer id = (Integer) result.get("id");
                    if (id != null) {
                        jobInfo.setId((long)id);
                    }
                    HighlightField hiLight = hit.getHighlightFields().get("jobInfo");
                    if (hiLight != null) {
                        jobInfo.setJobInfo(hiLight.getFragments()[0].toString());
                    }else{
                        jobInfo.setJobInfo("no keyword matched");
                    }
                    System.out.println("info");
                    if (jobInfo.getJobInfo() != null) {
                        System.out.println(jobInfo.getJobInfo());
                    }
                    String url = (String) result.get("url");
                    jobInfo.setUrl(url);

                    String searchRate = result.get("rate") + "";
                    jobInfo.setRate(searchRate);

                    String jobTitle = (String)result.get("jobTitle");
                    jobInfo.setJobTitle(jobTitle);

                    String companyName = (String) result.get("companyName");
                    jobInfo.setCompanyName(companyName);

                    String jobAddr = (String) result.get("jobAddr");
                    jobInfo.setJobAddr(jobAddr);
                    list.add(jobInfo);
                }
                if (list != null && list.size() > 0) {
                    return new AggregatedPageImpl(list);
                }
                return null;
            }
        });
        List<JobInfoField> content = jobInfoFields.getContent();
        frontPageResult.setRows(content);

        return frontPageResult;
    }

    public Integer pageHelper(Integer hits,int size) {
        if (hits % size == 0) {
            return (Integer) (hits / size);
        } else {
            return (Integer) (hits / size) + 1;
        }
    }

}
