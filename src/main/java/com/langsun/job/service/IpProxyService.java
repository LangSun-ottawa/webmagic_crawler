package com.langsun.job.service;

import com.langsun.job.pojo.IpProxy;
import us.codecraft.webmagic.proxy.Proxy;

import java.util.List;

public interface IpProxyService {

    public void save(IpProxy ipProxy);

    public void delete(Long id);

    public List<IpProxy> findAll();

    public List<Proxy> findAllProxy();

}
