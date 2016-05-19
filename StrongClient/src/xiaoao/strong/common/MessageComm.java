package xiaoao.strong.common;


public class MessageComm {
	//客户端发送登录消息
	public static final int C2S_LOGIN_MSG = 1000;
	//服务器返回登录消息
	public static final int S2C_LOGIN_MSG_RET = 2;
	//测试 
	public static final int C2S_SAY_HELLO = 3;
	
	
	//测试 匹配
	
	
	public static final int C2S_ROOMMATCH_MATCH = 3001;
	public static final int C2S_ROOMMATCH_ADDMATCHING = 3003;
	public static final int C2S_ROOMMATCH_REMOVEMATCHING = 3004;
}
