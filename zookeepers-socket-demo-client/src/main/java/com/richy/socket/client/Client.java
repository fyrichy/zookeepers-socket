package com.richy.socket.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @Desc：客户端
 * @author：Richy
 */
public class Client {

	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader bufferedReader_SystemIn = null;
		BufferedReader bufferedReader_Server = null;
		BufferedWriter bufferedWriter = null;
		try {
			//创建socket连接
			socket = new Socket("127.0.0.1", 8080);
			//获取键盘录入数据，用于向服务端发送请求
			bufferedReader_SystemIn = new BufferedReader(new InputStreamReader(System.in));
			
			//创建字符输入流，用于获取服务端响应的数据
			bufferedReader_Server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//使用socket输出流，想客户端发送请求
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//获取服务端的响应数据
			System.out.println("请输入:");
			String message = "";
			while((message = bufferedReader_SystemIn.readLine()) != null) {
				//客户端是先键盘录入数据，然后请求到服务端
				bufferedWriter.write(message);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				String getMessage = bufferedReader_Server.readLine();
				System.out.println("【服务端】："+getMessage);
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
}
