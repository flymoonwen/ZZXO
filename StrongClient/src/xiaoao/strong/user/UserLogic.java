package xiaoao.strong.user;

import xiaoao.strong.netty.message.ServerMsg;

/**
 * 用户相关逻辑处理类
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午2:03:41
 */
public class UserLogic{
	
	private static final UserLogic instance = new UserLogic();
    public static UserLogic getInstance() {
        return instance;
    }
	
	
	public void loginSuc(ServerMsg msg) {
		int id = msg.parseInt();
		String info = msg.parseString();
		System.out.println(info+" 你的id是："+id);
	}
	
	public void sayHello(ServerMsg msg) {
		String info = msg.parseString();
		System.out.println("服务器对我说："+info);
	}
}
