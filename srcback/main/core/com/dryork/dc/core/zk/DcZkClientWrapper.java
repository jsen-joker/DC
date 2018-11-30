package com.dryork.dc.core.zk;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.Assert;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcZkClientWrapper {
    private Logger logger = LoggerFactory.getLogger(DcZkClientWrapper.class);
    private long timeout;
    private ZkClient client;
    private CompletableFuture<ZkClient> completableFuture;
    private volatile boolean started = false;

    public DcZkClientWrapper(final String serverAddr, long timeout) {
        this.timeout = timeout;
        completableFuture = CompletableFuture.supplyAsync(() -> new ZkClient(serverAddr, Integer.MAX_VALUE));
    }


    public void start() {
        if (!started) {
            try {
                client = completableFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Throwable t) {
                logger.error("Timeout! zookeeper server can not be connected in : " + timeout + "ms!", t);
                completableFuture.whenComplete(this::makeClientReady);
            }
            started = true;
        } else {
            logger.warn("Zkclient has already been started!");
        }
    }



    public void addListener(IZkStateListener listener) {
        completableFuture.whenComplete((value, exception) -> {
            this.makeClientReady(value, exception);
            if (exception == null) {
                client.subscribeStateChanges(listener);
            }
        });
    }

    public boolean isConnected() {
//        return client != null && state == KeeperState.SyncConnected;
        return client != null;
    }

    public void createPersistent(String path) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        client.createPersistent(path, true);
    }

    public void createEphemeral(String path) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        client.createEphemeral(path);
    }

    public void delete(String path) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        client.delete(path);
    }

    public List<String> getChildren(String path) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        return client.getChildren(path);
    }

    public boolean exists(String path) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        return client.exists(path);
    }

    public void close() {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        client.close();
    }

    public List<String> subscribeChildChanges(String path, final IZkChildListener listener) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        return client.subscribeChildChanges(path, listener);
    }

    public void unsubscribeChildChanges(String path, IZkChildListener listener) {
        Assert.notNull(client, new IllegalStateException("Zookeeper is not connected yet!"));
        client.unsubscribeChildChanges(path, listener);
    }

    private void makeClientReady(ZkClient client, Throwable e) {
        if (e != null) {
            logger.error("Got an exception when trying to create zkclient instance, can not connect to zookeeper server, please check!", e);
        } else {
            this.client = client;
//            this.client.subscribeStateChanges(IZkStateListener);
        }
    }


}
