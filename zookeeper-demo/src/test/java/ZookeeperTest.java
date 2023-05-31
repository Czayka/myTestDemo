import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZookeeperTest {
    public CuratorFramework curatorFramework = null;
    private InterProcessMutex lock;
    @BeforeEach
    public void connect(){
        curatorFramework = CuratorFrameworkFactory.newClient(
                "159.75.56.189:2181",
                60000,
                5000,
                new RetryNTimes(5, 5000));
        curatorFramework.start();
    }

    /**
     * 创建节点：create 持久 临时 顺序 数据
     */
    @Test
    public void testCreate() throws Exception {
        // 基本创建
//        String s = curatorFramework.create().forPath("/app1"); // 默认将客户端的ip作为数据存储
//        System.out.println(s);
        // 带有数据
//        String s = curatorFramework.create().forPath("/app2","指定数据app2".getBytes());
//        System.out.println(s);
        // 设置节点类型
//        String s = curatorFramework.create()
//                .withMode(CreateMode.EPHEMERAL)
//                .forPath("/app3"); // 会话关闭后自动销毁
//        System.out.println(s);
        //创建多级节点 /app4/4
        //creatingParentsIfNeeded()如果父节点不存在，则创建父节点
        String s = curatorFramework.create().creatingParentsIfNeeded().forPath("/app4/4");
        System.out.println(s);
    }

    /**
     * 初选节点
     * 1.查询数据：get
     * 2.查询节点：ls
     * 3.查询节点状态信息：ls -s
     */
    @Test
    public void testGet() throws Exception {
        //查询数据
//        byte[] bytes = curatorFramework.getData().forPath("/app1");
//        System.out.println(new String(bytes));
        //查询子节点
//        List<String> list = curatorFramework.getChildren().forPath("/app4");
//        System.out.println(list);
        //查询节点状态信息
        Stat status = new Stat();
        System.out.println(status);
        curatorFramework.getData().storingStatIn(status).forPath("/app1");
        System.out.println(status);
    }


    /**
     * 1.基本修改数据
     * 2.根据版本修改
     */
    @Test
    public void testUpdate() throws Exception {
//        curatorFramework.setData().forPath("/app1", "上上上".getBytes());
        Stat stat = new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath("/app1");
        curatorFramework.setData().withVersion(stat.getVersion())
                .forPath("/app1", "下下下".getBytes());
    }



    /**
     * 1.删除单个节点
     * 2.删除带有子节点的节点
     * 3.必须成功的删除
     * 4.回调
     */
    @Test
    public void testDelete() throws Exception {
        //1.删除单个节点
//        curatorFramework.delete().forPath("/app1");
        //2.删除带有子节点的节点
//        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/app4");
        //3.必须成功的删除
//        curatorFramework.delete().guaranteed().forPath("/app2");
        //4.回调
        System.out.println("开始");
        curatorFramework.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("删除");
                System.out.println(curatorEvent);
            }
        }).forPath("/app1");
    }

    /**
     * NodeCache：给指定一个节点注册监听器
     */
    @Test
    public void testNodeCache() throws Exception {
        // 1.创建NodeCache对象
        final NodeCache nodeCache = new NodeCache(curatorFramework,"/tt/t");
        // 2.注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点改变了");
                //获取修改节点后的数据
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });
        // 3.开启监听，如果设置为true，则开启监听，加载缓冲数据
        nodeCache.start(true);
        while (true){

        }
    }

    /**
     * NodeCache：监听某个节点的所有子节点
     */
    @Test
    public void testPathChildrenCache() throws Exception {
        // 1.创建NodeCache对象
        PathChildrenCache pathChildrenCache =
                new PathChildrenCache(curatorFramework,"/tt",true);
        // 2.注册监听
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("子节点变化了");
                System.out.println(pathChildrenCacheEvent);
                // 获取类型
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                System.out.println(type);
                // 获取数据
                byte[] data = pathChildrenCacheEvent.getData().getData();
                String s = new String(data);
                System.out.println(s);
            }
        });
        // 3.开启监听，如果设置为true，则开启监听，加载缓冲数据
        pathChildrenCache.start();
        while (true){

        }
    }

    /**
     * TreeCache：监听某个节点和自己的所有子节点
     */
    @Test
    public void testTreeCache() throws Exception {
        // 1.创建NodeCache对象
        final TreeCache treeCache = new TreeCache(curatorFramework,"/tt");
        // 2.注册监听
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("节点变化了");
                System.out.println(treeCacheEvent);
            }
        });
        // 3.开启监听，如果设置为true，则开启监听，加载缓冲数据
        treeCache.start();
        while (true){

        }
    }

    /**
     * 实现分布式锁
     */
    @Test
    public void testLock(){
        lock = new InterProcessMutex(curatorFramework,"/lock");
        try {
            lock.acquire(3, TimeUnit.SECONDS);
        }catch (Exception e){

        }finally {
            try {
                lock.release();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @AfterEach
    public void destroyed(){
        curatorFramework.close();
    }
}
