package com.richy.zookeepers.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Desc: 使用socket创建客户端
 * @author Richy
 */
public class ZkClient {
	public static List<String> listServer = new ArrayList<String>();

	public static void main(String[] args) {
		initServer();
		ZkClient client= new ZkClient();
		//把控制台信息放入到bufferdReader中
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String name;
			try {
				//读取控制台信息
				name = console.readLine();
				if ("exit".equals(name)) {
					System.exit(0);
				}
				//客户端发送请求
				client.request(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 注册所有server
	/**
	 * @Desc：注册所有server
	 * @Author：richy
	 * @Year：2019
	 */
	public static void initServer() {
		listServer.clear();
		listServer.add("127.0.0.1:8080");
	}

	// 获取当前server信息
	public static String getServer() {
		return listServer.get(0);
	}
	
	/**
	 * @Desc：客户端发送请求
	 * @Author：richy
	 * @Year：2019
	 */
	public void request(String name) {
		//把服务的地址和端口号放入到一个集合中
		String server = ZkClient.getServer();
		String[] cfg = server.split(":");
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			//创建一个socket连接到服务端
			socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(name);
			while (true) {
				String response = in.readLine();
				if (response == null) {
					break;
				}else if (response.length() > 0) {
					System.out.println("客户端打印 : " + response);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
