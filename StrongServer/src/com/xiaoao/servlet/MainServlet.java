package com.xiaoao.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.xiaoao.GameServer;

public class MainServlet extends HttpServlet{

	@Override
	public void init() throws ServletException {
		GameServer.getInstance().initServer();
		System.out.println("服务启动成功");
	}

	@Override
	public void destroy() {
		System.err.println("关闭服务器！!!!!!!");
//		GameServer.getInstance().closeSocket();
	}
	
	
}
