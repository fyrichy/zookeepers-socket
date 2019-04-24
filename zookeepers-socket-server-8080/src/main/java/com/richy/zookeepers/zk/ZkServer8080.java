package com.richy.zookeepers.zk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Desc：创建端口号为8080的服务端
 * @author：Richy
 */
public class ZkServer8080 implements Runnable {
	
	private int port = 8080;

	public static void main(String[] args) throws IOException {
		int port = 8080;	
		ZkServer8080 server = new ZkServer8080(port);
		Thread thread = new Thread(server);
		thread.start();
	}

	public ZkServer8080(int port) {
		this.port = port;
	}

	/**
	 * @Desc：服务端进行处理
	 * @Author：richy
	 * @Year：2019
	 */
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("服务开始，端口号为：" + port);
			Socket socket = null;
			while (true) {
				socket = serverSocket.accept();
				new Thread(new ZkServerHandler(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (Exception e2) {

			}
		}
	}

}
