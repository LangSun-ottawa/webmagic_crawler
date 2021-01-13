package com.langsun.job.task.IpProxyPool;

import com.langsun.job.pojo.IpProxy;
import com.langsun.job.service.IpProxyService;
import com.langsun.job.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
public class ProxyPoolSpringDataPipeline implements Pipeline {

    @Autowired
    private IpProxyService ipProxyService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<IpProxy> proxies = resultItems.get("proxies");
        if (proxies.size() != 0) {
            for (IpProxy proxy : proxies) {
                ipProxyService.save(proxy);
            }
        }
    }
}
