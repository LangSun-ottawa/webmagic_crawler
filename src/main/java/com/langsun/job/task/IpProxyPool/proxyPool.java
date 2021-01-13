package com.langsun.job.task.IpProxyPool;

import com.langsun.job.pojo.IpProxy;
import com.langsun.job.service.IpProxyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:crawler.properties")
public class proxyPool implements PageProcessor {
    @Value("${proxy.validation.url}")
    private String validationUrl;

    @Value("${proxy.freeIpProxy.url}")
    private String getIpProxyUrl;

    @Value("${proxy.freeIpProxy.url.prefix}")
    private String prefix;

    @Autowired
    private IpProxyService ipProxyService;

    @Autowired
    private ProxyPoolSpringDataPipeline proxyPoolSpringDataPipeline;

    @Scheduled(cron = "0 0 0 1/3 * ? ")
    public void process() {
        //检验代理ip可用性
//        checkValidation();
        List<Proxy> proxies = ipProxyService.findAllProxy();
        // 创建下载器 Downloader
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        // 给下载器设置代理服务器信息
//        httpClientDownloader.setProxyProvider(new SimpleProxyProvider(proxies));
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("60.246.7.4",8080)));

        Spider.create(new proxyPool())
                .addUrl(getIpProxyUrl)
                .setDownloader(httpClientDownloader)// 设置下载器
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(5)
                .addPipeline(proxyPoolSpringDataPipeline)
                .run();
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        List<Selectable> oddNodes = html.xpath("//*[@class='table_block']/table/tbody/tr").nodes();
//        List<Selectable> evenNodes = html.xpath("//*[@id='list']/table/tbody/tr").nodes();
        List<IpProxy> ipProxies = new ArrayList<>();
        for (Selectable oddNode : oddNodes) {
            IpProxy ipProxy = new IpProxy();
            String host = oddNode.css("td:nth-child(1)", "text").toString();
            ipProxy.setHost(host);

            String stringPort = oddNode.css("td:nth-child(2)", "text").toString();
            if (!StringUtils.isBlank(stringPort)) {
                ipProxy.setPort(Integer.parseInt(stringPort));
            }

            String addr = oddNode.css("td:nth-child(3) span", "text").toString();
            ipProxy.setAddr(addr);

            String scheme = oddNode.css("td:nth-child(5)", "text").toString();
            ipProxy.setScheme(scheme);

            ipProxies.add(ipProxy);

            page.putField("proxies", ipProxies);
        }

        //next page
        String nextPage = html.xpath("/html/body/div[1]/div[4]/div/div[5]/ul/li[5]/a").links().toString();
        page.addTargetRequest(nextPage);

    }

    @Override
    public Site getSite() {
        return Site.me().setCharset("utf-8")
                .setTimeOut(5 * 1000)
                .setRetrySleepTime(3000)
                .setSleepTime(3000)
                .setRetryTimes(3)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                ;

    }


    @Scheduled(cron = "0 0 0 2/3 * ? ")//需要修改为cron
    public void checkValidation() {
        List<IpProxy> unCheckedProxies = ipProxyService.findAll();
        System.out.println(unCheckedProxies.size()+"*****个");
        for (IpProxy unCheckedProxy : unCheckedProxies) {
            System.out.println("********current ip proxy*******" + unCheckedProxy);
            int statusCode = checkValidation(unCheckedProxy);
            if (statusCode != 200) {
                System.out.println("***delete :" + unCheckedProxy);
                ipProxyService.delete(unCheckedProxy.getId());
            }
        }
    }

    /**
     * @param  : HttpHost
     * @return : int statusCode
     * @author : LangSun
     * @date : 2020/12/11 1:15
     */
    public int checkValidation(IpProxy proxy){
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(validationUrl);

        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
        HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort(),proxy.getScheme());

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(12*1000)//设置连接超时时间
                .setSocketTimeout(12*1000)//设置读取超时时间
                .setProxy(httpHost)//设置代理
                .build();

        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;

        try {
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
//        String s = EntityUtils.toString(entity, "utf-8");
            System.out.println("check validation...");
            System.out.println(proxy.getHost()+":"+proxy.getPort()+"  "+statusCode);
//        System.out.println("****content***");
//        System.out.println(s);
            return statusCode;
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }
    }
}
