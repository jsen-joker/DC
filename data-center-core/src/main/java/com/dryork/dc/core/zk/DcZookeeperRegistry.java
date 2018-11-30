package com.dryork.dc.core.zk;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.support.FailbackRegistry;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.rpc.RpcException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 *     数据中心zookeeper注册
 *     这个是client和datacenter共同的注册中心
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcZookeeperRegistry extends FailbackRegistry {

    private final static Logger logger = LoggerFactory.getLogger(DcZookeeperRegistry.class);

    private final static String DEFAULT_ROOT = "DC";

    private final String root;
    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> zkListeners = new ConcurrentHashMap<URL, ConcurrentMap<NotifyListener, ChildListener>>();


    private final ZookeeperClient zkClient;

    public DcZookeeperRegistry(URL url) {
        super(url);
        if (url.isAnyHost()) {
            throw new IllegalStateException("registry address == null");
        }
        String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
        if (!group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        this.root = group;
        zkClient = new DcZookeeperClient(url);
        zkClient.addStateListener(state -> {
            if (state == StateListener.RECONNECTED) {
                try {
                    recover();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public boolean isAvailable() {
        return zkClient.isConnected();
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            zkClient.close();
        } catch (Exception e) {
            logger.warn("Failed to close zookeeper client " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doRegister(URL url) {
        try {
            zkClient.create(toUrlPath(url), url.getParameter(Constants.DYNAMIC_KEY, true));
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doUnregister(URL url) {
        try {
            zkClient.delete(toUrlPath(url));
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doSubscribe(final URL url, final NotifyListener listener) {
        String path = toCategoryPath(url);
        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url);
        if (listeners == null) {
            zkListeners.putIfAbsent(url, new ConcurrentHashMap<NotifyListener, ChildListener>());
            listeners = zkListeners.get(url);
        }
        ChildListener zkListener = listeners.get(listener);
        if (zkListener == null) {
            listeners.putIfAbsent(listener, new ChildListener() {
                @Override
                public void childChanged(String parentPath, List<String> currentChilds) {
                    List<URL> urlList = Lists.newArrayList();
                    for (String u : currentChilds) {
                        urlList.add(URL.valueOf(URL.decode(u)));
                    }
                    listener.notify(urlList);
                }
            });
            zkListener = listeners.get(listener);
        }
        zkClient.addChildListener(path, zkListener);
    }

    @Override
    protected void doUnsubscribe(URL url, NotifyListener listener) {
        logger.warn("not impl method");
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            String path = toCategoryPath(url);
            List<String> children = zkClient.getChildren(path);
            return toUrlsWithoutEmpty(url, children);
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    private String toRootPath() {
        return root;
    }

    private String toCategoryPath(URL url) {
        return toRootPath() + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
    }

    private String toUrlPath(URL url) {
        return toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toFullString());
    }
    private List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<>();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                provider = URL.decode(provider);
                if (provider.contains("://")) {
                    URL url = URL.valueOf(provider);
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }
}
