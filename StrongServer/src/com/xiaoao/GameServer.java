package com.xiaoao;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.xiaoao.common.Global;
import com.xiaoao.netty.GameServerPipelineFactory;
import com.xiaoao.netty.IMsgProcesser;
import com.xiaoao.pojo.Room;
import com.xiaoao.scheduler.JobEntry;
import com.xiaoao.scheduler.QuartzManager;
import com.xiaoao.scheduler.job.KeyFrameTickJob;
import com.xiaoao.timer.GameTimerManager;
import com.xiaoao.user.UserLogic;
import com.xiaoao.util.PackageUtil;

/**
 * socket 服务器管理类，负责Nio Server Socket的开启与关闭
 *
 * @author <a href="mailto:cliff7777@gmail.com">cliff</a>
 * @since 2014-5-23
 */
public class GameServer {

    private static final Logger logger = LogManager.getLogger(GameServer.class);
    
    
    private static final GameServer instance = new GameServer();
    public static GameServer getInstance() {
        return instance;
    }
    private Channel bind;

    private final NioServerSocketChannelFactory tcpChannelFactory = new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
    
    private GameServer() {
    }

    /**
     * 开启socket
     */
    public void socketStart() {
        ServerBootstrap bootstrap = new ServerBootstrap(tcpChannelFactory);
        bootstrap.setPipelineFactory(new GameServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.TIMEOUT", 120);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.receiveBufferSize", 1024 * 64);
        bootstrap.setOption("child.sendBufferSize", 1024 * 64);
        
        String port = "8666";
        bind = bootstrap.bind(new InetSocketAddress(Integer.parseInt(port)));
        logger.info("Server Start，Port：" + port);
    }

    /**
     * 关闭socket
     */
    public void closeSocket() {
        ChannelFuture future = bind.close();
        future.awaitUninterruptibly(5000);
        tcpChannelFactory.releaseExternalResources();
        logger.info("Server Stop!");
    }
    
    private void loadMessage() {
    	try {
	        List<String> classNames = PackageUtil.getClassName("com.xiaoao");
	        if (classNames != null) {
	            for (String className : classNames) {
	                Class<?> temp = Class.forName(className);
	                if (!temp.equals(IMsgProcesser.class) && IMsgProcesser.class.isAssignableFrom(temp)) {
	                    Method method = temp.getMethod("getInstance");
	                    IMsgProcesser invoke = (IMsgProcesser) method.invoke(null);
	                    invoke.registMsgId();
	                }
	            }
	        }
	        
	        logger.info("loadMessage end");
        } catch (Exception ex) {
        	ex.printStackTrace();
            Global.logErrorMessage(ex);
            System.exit(0);
        }
    }
    
    /**
     * 初始化server
     */
    public void initServer() {
    	try {
    		loadMessage();
    		socketStart();
    		GameTimerManager.getInstance().startTimer();
    		
    		
//			List<JobEntry> list = new ArrayList<JobEntry>();
//			list.add(new JobEntry(new KeyFrameTickJob(), "0/1 * * * * ?", "SimpleJob", "Simple"));
//			QuartzManager.executJobs(list);
			
			
			
//    		test();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务器启动失败");
		} finally{
//			closeSocket();
		}
    }
    
    public static void main(String[] args) {
    	GameServer.getInstance().initServer();
	}
}
