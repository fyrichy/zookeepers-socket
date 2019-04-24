package com.richy.zookeepers.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.I0Itec.zkclient.ZkClient;

/**127.0.0.1:2181
 * @Desc：服务端8080
 * @author：Richy
 */
public class Server8080 {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			//创建服务端对象
			serverSocket = new ServerSocket(8080);
			//服务启动，向zk注册服务
			registerServer();
			//获取连接过来的客户端对象
			Socket socket = serverSocket.accept();
			System.out.println("您有新的请求，来自【"+socket.getInetAddress().getHostAddress()+"】");
			
			//用于读取来自客户端的请求数据
			BufferedReader bufferedReader_client = null;
			//用户读取控制台输入的数据
			BufferedReader bufferedReader_SystemIn = null;
			//用于对客户端做出响应
			BufferedWriter bufferedWriter = null;
			
			//通过socket对象获取输入流，也即读取客户端的请求信息
			bufferedReader_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//读取控制台输入的信息
			bufferedReader_SystemIn = new BufferedReader(new InputStreamReader(System.in));
			//使用socket输出流向客户端端响应数据
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//进行读取操作
			String message = "";
			//读取客户端的请求数据
			while((message = bufferedReader_client.readLine()) != null) {
				System.out.println("【客户端】："+message);
				System.out.println("请输入：");
				//键盘录入，进行响应
				String str = bufferedReader_SystemIn.readLine();
				bufferedWriter.write(str);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != serverSocket) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Desc：想注册中心注册服务
	 * @Author：richy
	 * @Year：2019
	 */
	private static void registerServer() {
		ZkClient zkClient = new ZkClient("127.0.0.1:2181", 60000, 1000);
		String path = "/zk_socket/server_8080";
		if(zkClient.exists(path)) {
			zkClient.delete(path);
		}
		//创建临时节点
		zkClient.createEphemeral(path, "127.0.0.1:8080");
	}
}
