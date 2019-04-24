package com.richy.zookeepers.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * @Desc：客户端
 * @author：Richy
 */
public class Client {
	
	private final static String PARENT = "/zk_socket";
	
	// 请求次数
	private static int reqestCount = 1;
	// 服务数量
	private static int serverCount = 0;
	
	//本地缓存，用户缓存订阅的服务
	public static List<String> listServer = new ArrayList<String>();

	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader bufferedReader_SystemIn = null;
		BufferedReader bufferedReader_Server = null;
		BufferedWriter bufferedWriter = null;
		try {
			//订阅自己所需的所有服务
			subServer();
			//获取键盘录入数据，用于向服务端发送请求
			bufferedReader_SystemIn = new BufferedReader(new InputStreamReader(System.in));
			
			//获取服务端的响应数据
			System.out.println("请输入:");
			String message = "";
			while((message = bufferedReader_SystemIn.readLine()) != null) {
				//创建socket连接
				socket = getSocket();
				//创建字符输入流，用于获取服务端响应的数据
				bufferedReader_Server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//使用socket输出流，想客户端发送请求
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				//客户端是先键盘录入数据，然后请求到服务端
				bufferedWriter.write(message);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				String getMessage = bufferedReader_Server.readLine();
				System.out.println("【服务端"+socket.getInetAddress().getHostAddress()+":"+socket.getPort()+"】："+getMessage);
				System.out.println("请输入：");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 * @Desc：获取服务
	 * @Author：richy
	 * @Year：2019
	 */
	private static Socket getSocket() throws NumberFormatException, UnknownHostException, IOException {
		String str = getServer();
		String[] servers = str.split(":");
		Socket socket = new Socket(servers[0], Integer.parseInt(servers[1]));
		return socket;
	}

	/**
	 * @Desc：订阅所有服务
	 * @Author：richy
	 * @Year：2019
	 */
	private static void subServer() {
		final ZkClient client = new ZkClient("127.0.0.1:2181", 6000,1000);
		List<String> childrens = client.getChildren(PARENT);
		getChilds(client,childrens);
		//监听事件，如果有服务挂了，那么断开连接
		client.subscribeChildChanges(PARENT, new IZkChildListener() {
			/**
			 * @Desc：监听子节点改变
			 * @Author：richy
			 * @Year：2019
			 */
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				getChilds(client,childrens);
			}
		});
	}

	/**
	 * @Desc：获取服务，存放到本地缓存
	 * @Author：richy
	 * @Year：2019
	 */
	private static void getChilds(ZkClient client, List<String> childrens) {
		listServer.clear();
		for(String ch:childrens) {
			String pathValue = (String)client.readData(PARENT+"/"+ch);
			listServer.add(pathValue);
		}
		serverCount = listServer.size();
		System.out.println("从zk读取到服务信息:" + listServer.toString());
	}
	
	/**
	 * @Desc：获取当前server，并实现负载均衡
	 * @Author：richy
	 * @Year：2019
	 */
	public static String getServer() {
		// 实现负载均衡
		String serverName = listServer.get(reqestCount % serverCount);
		++reqestCount;
		return serverName;
	}
}
