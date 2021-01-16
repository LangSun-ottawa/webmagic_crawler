package com.langsun.job.task.JobTask;

import com.langsun.job.pojo.JobInfo;
import com.langsun.job.service.IpProxyService;
import com.langsun.job.service.JobInfoService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class JobProcessor implements PageProcessor {
    private String urlHead = "https://www.glassdoor.ca/Job/software-engineer-jobs-SRCH_KO0,17_IP";
    private String urlTail = ".htm?jobType=fulltime&fromAge=3";
    private String url = "https://www.glassdoor.ca/Job/software-engineer-jobs-SRCH_KO0,17_IP2.htm?jobType=fulltime&fromAge=3";

    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().xpath("//*[@id=RawSalaryData]/div/table/tbody/tr").nodes();
        String url = page.getUrl().toString();
//        System.out.println("********current url" + url);

        //detail page --> to save
        if (nodes.size() == 0) {
//            System.out.println("*****save detail data******");
            saveJobInfo(page);
        } else {
//            System.out.println("****add next page*****");
            //list page, rawData
            for (Selectable node : nodes) {
                String jobInfoUrl = node.links().toString();
                page.addTargetRequest(jobInfoUrl);
            }

            if (url.contains(urlHead)) {
                String substring = url.split(urlHead)[1];
                String pageNum = substring.split("\\.")[0];
                int i = Integer.parseInt(pageNum) + 1;
                if (i <= 5) {
                    String npUrl = urlHead + i + urlTail;
//                    System.out.println(npUrl);
                    page.addTargetRequest(npUrl);
                }
            }
        }
    }

    /**
     * @param page :
     * @return : void
     * @author : LangSun
     * @date : 2020/12/6 23:05
     * @desc : 解析页面，获取详情页面，保存 parse detail page,save to database;
     */
    private void saveJobInfo(Page page) {
        Html html = page.getHtml();

        JobInfo jobInfo = new JobInfo();
        //jobInfo details
        String text = html.css("span.css-1pmc6te", "text").toString();
        text = text==null?"0":text;
        jobInfo.setRate(text);
//        System.out.println("***rate***"+jobInfo.getRate());

        jobInfo.setCompanyName(html.css("div.css-16nw49e","text").toString());

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        jobInfo.setTime(format);

        jobInfo.setUrl(page.getUrl().toString());

        jobInfo.setJobTitle(html.css("div.e11nt52q6", "text").toString());

        jobInfo.setJobAddr(html.css("div.e11nt52q2","text").toString());
        String jobDetail = Jsoup.parse(html.css("div.ecgq1xb4").toString()).text();
        if (jobDetail != null && jobDetail.length() > 4000) {
            String substring = jobDetail.substring(0, 4000);
            jobInfo.setJobInfo(substring);
        } else {
            jobInfo.setJobInfo(jobDetail);
        }
        page.putField("jobInfo", jobInfo);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setCharset("utf-8")
                .setTimeOut(10 * 1000)
                .setRetrySleepTime(5000)
                .setRetryTimes(3)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
    }

    @Autowired
    private SpringDataPipeline springDataPipeline;
    @Autowired
    private IpProxyService ipProxyService;

    @Scheduled(cron = "0 0 8 1/3 * ? ")
//    @Scheduled(fixedDelay = 1000*1000*1000)
    public void process() {
//        System.out.println("**********new spider start*************");
        List<Proxy> proxies = ipProxyService.findAllProxy();
        int i = new Random().nextInt(proxies.size() - 1);
        Proxy proxy = proxies.get(i);

        HttpClientDownloader httpClientDownloader = new HttpClientDownloader(){
            @Override
            protected void onError(Request request) {
                setProxyProvider(SimpleProxyProvider.from(proxy));
                int random = new Random().nextInt(proxies.size() - 1);
                //setProxyProvider(SimpleProxyProvider.from(proxies.get(random)));
            }
        };
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxy));

        Spider spider = Spider.create(new JobProcessor())
                .addUrl(url)
//                .setDownloader(httpClientDownloader)// 设置下载器
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(3)
                .addPipeline(springDataPipeline);
        //spider listener
        ArrayList<SpiderListener> listeners = new ArrayList<>();
        listeners.add(new SpiderListener() {
            @Override
            public void onSuccess(Request request) {
            }

            @Override
            public void onError(Request request) {
                Integer cycleTriedTimes = (Integer) request.getExtra(Request.CYCLE_TRIED_TIMES);
                request.putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes == null ? 1 : cycleTriedTimes + 1);
                spider.addRequest(request);
            }
        });
        spider.setSpiderListeners(listeners);
        try {
            spider.run();
        } catch (Exception e) {
            spider.setDownloader(httpClientDownloader);
            spider.run();
        }

    }


    @Autowired
    private JobInfoService jobInfoService;

    @Scheduled(cron = "0 0 2 1/12 * ? ")
    public void cleanOldData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_YEAR, -12);
        Date time = rightNow.getTime();
        String format = simpleDateFormat.format(time);
        jobInfoService.delete(format);
        System.out.println("delete done");
    }


}
