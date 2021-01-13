package com.langsun.job.service.impl;

import com.langsun.job.dao.IpProxyDao;
import com.langsun.job.pojo.IpProxy;
import com.langsun.job.service.IpProxyService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.proxy.Proxy;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class IpProxyImpl implements IpProxyService {

    @Autowired
    private IpProxyDao ipProxyDao;

    @Override
    public void save(IpProxy ipProxy) {
        List<IpProxy> ipProxies = ipProxyDao.findAll(Example.of(ipProxy));
        if (ipProxies.size() == 0) {
            ipProxyDao.save(ipProxy);
        }
    }



    @Override
    public void delete(Long id) {
        ipProxyDao.deleteById(id);
    }

    @Override
    public List<IpProxy> findAll() {
        List<IpProxy> proxies = ipProxyDao.findAll();
        return proxies;
    }

    @Override
    public List<Proxy> findAllProxy() {
        List<Proxy> result = new ArrayList<>();
        List<IpProxy> proxies = ipProxyDao.findAll();
        for (IpProxy proxy : proxies) {
            Proxy newProxy = new Proxy(proxy.getHost(), proxy.getPort());
            result.add(newProxy);
        }
        return result;
    }
}
