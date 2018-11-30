package com.dryork.dc.core.zk;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.dryork.dc.core.zk.AbstractZookeeperClient;
import com.dryork.dc.core.zk.DcZkClientWrapper;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcZookeeperClient extends AbstractZookeeperClient<IZkChildListener> {

    private final DcZkClientWrapper client;
    private volatile Watcher.Event.KeeperState state = Watcher.Event.KeeperState.SyncConnected;

    public DcZookeeperClient(URL url) {
        long timeout = url.getParameter(Constants.TIMEOUT_KEY, 30000L);
        client = new DcZkClientWrapper(url.getBackupAddress(), timeout);
        client.start();
    }

    @Override
    public void createPersistent(String path) {
        try {
            client.createPersistent(path);
        } catch (ZkNodeExistsException e) {}
    }
    @Override
    public void createEphemeral(String path) {
        try {
            client.createEphemeral(path);
        } catch (ZkNodeExistsException e) {
        }
    }

    @Override
    public void delete(String path) {
        try {
            client.delete(path);
        } catch (ZkNoNodeException e) {
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
    }

    @Override
    public boolean checkExists(String path) {
        try {
            return client.exists(path);
        } catch (Throwable t) {
        }
        return false;
    }

    @Override
    protected IZkChildListener createTargetChildListener(String path, ChildListener listener) {
        return new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                listener.childChanged(parentPath, currentChilds);
            }
        };
    }

    @Override
    public boolean isConnected() {
        return state == Watcher.Event.KeeperState.SyncConnected;
    }

    @Override
    public void doClose() {
        client.close();
    }

    @Override
    public List<String> addTargetChildListener(String path, final IZkChildListener listener) {
        return client.subscribeChildChanges(path, listener);
    }

    @Override
    public void removeTargetChildListener(String path, IZkChildListener listener) {
        client.unsubscribeChildChanges(path, listener);
    }
}
