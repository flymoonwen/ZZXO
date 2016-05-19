package com.xiaoao.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

/**
 * 全局类，负责配置文件的读写、log的写入等
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:23:00
 */
public class Global {

    private static final Global instance = new Global();
    private static final Logger logger = LogManager.getLogger("server");
    private static final Logger commLogger = LogManager.getLogger("comm");
    private static final Logger timeLogger = LogManager.getLogger("time");
    private static final Logger gmLogger = LogManager.getLogger("gm");
    private static final Logger dbLogger = LogManager.getLogger("db");
    private static boolean screenShow = false;

    private static final String SYSLOG = "[0][0][0][";
    public static final String LOGSPACE = String.valueOf((char) 0X09);

    public static Global getInstance() {
        return instance;
    }

    public static void logMessage(String ip, int id, String msgType, String content) {
        String msg = "[" + ip + "][" + id + "][" + msgType + "][" + content + "]";
        logger.info(msg);
        if (screenShow) {
            System.out.println(msg);
        }
    }

    public static void logMessage(ChannelHandlerContext ctx, int id, String msgType, String content) {
        if (ctx == null) {
            logMessage("0", id, msgType, content);
        } else {
            logMessage(ctx.getChannel().getRemoteAddress().toString(), id, msgType, content);
        }
    }

    public static void logErrorMessage(ChannelHandlerContext ctx, Exception ex) {
        String msg;
        if (ctx == null) {
            msg = "[0][0][" + getErrorMessage(ex) + "]";
        } else {
            msg = "[" + ctx.getChannel().getRemoteAddress().toString() + "][0][" + getErrorMessage(ex) + "]";
        }
        logger.error(msg);
        System.out.println(msg);
    }

    public static void logErrorMessage(Exception ex) {
        logErrorMessage(getErrorMessage(ex));
    }

    public static void logErrorMessage(Logger log, Exception ex) {
        String msg = getErrorMessage(ex);
        logger.error(msg);
        System.out.println(msg);
    }

    public static void logErrorMessage(String content) {
        String msg = SYSLOG + content + "]";
        logger.error(msg);
        System.out.println(content);
    }

    private static String getErrorMessage(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder sb = new StringBuilder(100);
        sb.append("----------------------------------").append("\n");
        sb.append(e.getClass()).append(":").append(e.getMessage()).append("\n");
        for (StackTraceElement t : stackTrace) {
            sb.append("    ").append("at ").append(t.getClassName()).append(".").append(t.getMethodName()).append("(").append(t.getFileName()).append(":").append(t.getLineNumber()).append(")").append("\n");
        }
        return sb.toString();
    }

    public static void logSysMessage(String content) {
        String msg = SYSLOG + content + "]";
        logger.entry();
        logger.info(msg);
        System.out.println(content);
    }

    public static void logCommMessage(String content) {
        commLogger.info(content);
        if (screenShow) {
            System.out.println(content);
        }
    }

    public static void logDealTime(String content) {
        timeLogger.info(content);
    }

    public static void logGmMessage(String content) {
        gmLogger.info(content);
        if (screenShow) {
            System.out.println(content);
        }
    }

    /**
     * 得到数据库日志
     */
    public static Logger getDbLogger() {
        return dbLogger;
    }
    private final ChannelGroup sockets = new DefaultChannelGroup("Global");
//    public final ResourceBundle language;
    /**
     * 全局类，负责配置文件的读写、log的写入、系统的初始化
     *
     * @author <a href="mailto:cliff7777@gmail.com">cliff</a>
     */
    private Global() {
//        language = ResourceBundle.getBundle("Language_zh_CN");
    }

    public void setScreenShow(boolean screenShow) {
        Global.screenShow = screenShow;
    }

    public ChannelGroup getSockets() {
        return sockets;
    }

//    /**
//     * 获得language文件里的国际化字符串
//     *
//     * @param key 字符串的key
//     *
//     */
//    public String getInternationalString(String key) {
//        return language.getString(key);
//    }
//
//    /**
//     * 获得language文件里的国际化字符串
//     *
//     * @param key 字符串的key
//     * @param para 替换字符串中的参数
//     *
//     */
//    public String getInternationalString(String key, Object... para) {
//        return MessageFormat.format(language.getString(key), para);
//    }

}
