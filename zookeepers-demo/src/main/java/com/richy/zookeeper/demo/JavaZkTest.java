package com.richy.zookeeper.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @Desc：使用java操作zookeeper，创建节点有如下四种方式：
 * 	  PERSISTENT (持久节点)
　　　　PERSISTENT_SEQUENTIAL(持久顺序节点)
　　　　EPHEMRAL(临时节点)
　　　　EPHEMRAL_SEQUENTIAL(临时顺序节点)
	  
	  顺序节点和非顺序节点，在其后加：
	   /zk_demo/child_01
	  /zk_demo/child_010000000003
 * @author：Richy
 */
public class JavaZkTest {

	//连接地址
	private static final String ADDRESS = "127.0.0.1:2181";
	//session会话超时时间
	private static final Integer SESSION_OUTTIME = 2000;
	//信号量，阻塞程序执行，用户等待zookeeper连接成功，发送信号
	private static final CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
		Watcher watcher = new Watcher() {
			@SuppressWarnings("static-access")
			@Override
			public void process(WatchedEvent event) {
				//获取事件状态
				KeeperState keeperState = event.getState();
				//获取事件类型
				EventType eventType = event.getType();
				if(keeperState == keeperState.SyncConnected) {
					if(eventType == EventType.None) {
						countDownLatch.countDown();
						System.out.println("zk 启动连接...");
					}
				}
			}
		};
		ZooKeeper zookeeper = new ZooKeeper(ADDRESS, SESSION_OUTTIME, watcher);
		//进行阻塞
		countDownLatch.await();
		//创建一个持久的父节点
		//String result = zookeeper.create("/zk_demo", "zk_demo".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//创建一个临时子节点
		String result1 = zookeeper.create("/zk_demo/child_01", "child_01".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		String result2 = zookeeper.create("/zk_demo/child_02", "child_02".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(result1);
		System.out.println(result2);
		zookeeper.close();
	}
	
}
