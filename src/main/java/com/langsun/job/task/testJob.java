package com.langsun.job.task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

@Component
public class testJob implements PageProcessor {

//    @Scheduled(fixedDelay = 100000)
    public void process() {
        // 创建下载器 Downloader
        System.out.println("***testjob***");
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        // 给下载器设置代理服务器信息
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("41.190.92.84", 48515)));
        Spider.create(new testJob())
                .addUrl("https://api.myip.com/")
                .setDownloader(httpClientDownloader)// 设置下载器
                .run();
    }

    @Override
    public void process(Page page) {
        System.out.println(page.getStatusCode());
        System.out.println(page.getHtml().toString());
    }

    @Override
    public Site getSite() {
        return Site.me();
    }

    /**
     * @param : HttpHost
     * @return : int statusCode
     * @author : LangSun
     * @date : 2020/12/11 1:15
     */
    public static void main(String[] args) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("https://api.myip.com/");
        HttpGet httpGet = new HttpGet("https://www.glassdoor.ca/Job/java-software-engineer-jobs-SRCH_KO0,22.htm");

        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
        HttpHost httpHost = new HttpHost("81.12.119.189", 8080);

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000)//设置连接超时时间
                .setSocketTimeout(10000)//设置读取超时时间
                .setProxy(httpHost)//设置代理
                .build();

        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;


        response = client.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity, "utf-8");
        System.out.println(statusCode);
//        System.out.println("****content***");
//        System.out.println(s);

    }

//    public static void main(String[] args) {
//        PriorityQueue<Integer> integers = new PriorityQueue<>(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        integers.offer(1);
//        integers.offer(3);
//        integers.offer(7);
//        integers.offer(2);
//        System.out.println(integers);
//        System.out.println(integers.poll());
//        integers.offer(6);
//        System.out.println(integers);
//
//    }
}
