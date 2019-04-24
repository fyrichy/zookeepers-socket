package com.richy.zookeepers.zk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ZkServerHandler implements Runnable {
	private Socket socket;

	public ZkServerHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @Desc：用于接收客户端的请求数据
	 * @Author：richy
	 * @Year：2019
	 */
	public void run() {
		BufferedReader bf = null;
		PrintWriter pw = null;
		try {
			bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			pw = new PrintWriter(this.socket.getOutputStream(), true);
			String body = null;
			while (true) {
				body = bf.readLine();
				if (body == null) {
					break;
				}
				System.out.println("服务端接收到 : " + body);
				pw.println("Hello, " + body);
			}

		} catch (Exception e) {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.socket = null;
			}
		}
	}
}
