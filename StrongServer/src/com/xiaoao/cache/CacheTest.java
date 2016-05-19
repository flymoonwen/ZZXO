package com.xiaoao.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xiaoao.pojo.User;

/**
 * 此类是缓存类 
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:34:44
 */
public class CacheTest {
	/**
	 * 此项配置 是 缓存中数据的过期时间，
	 * expireAfterAccess=5m 表示最后一次访问某数据A 后 5分钟每人访问，就把他从缓存中删除
	 * 更多信息 请去google官网 查看
	 */
	public static final String CACHESPEC = "expireAfterAccess=5m";
	private final LoadingCache<String, User> cache;
	
	private static final CacheTest instance = new CacheTest();
    public static CacheTest getInstance() {
        return instance;
    }
    
    public final User NULLUSER;
	
	private  CacheTest() {
		NULLUSER = new User();
		NULLUSER.setNickName("null");
		cache = CacheBuilder.from(CACHESPEC).build(new CacheLoader<String, User>() {
            @Override
            public User load(String userName) {
            	//从mysql中根据userId去查询 到user 存储到缓存中
            	User user = getUserFromDB(userName);
            	return user == null ? NULLUSER:user;
            }
        });
	}
	
	/**
	 * 从缓存中取User
	 * @param userName
	 * @return
	 */
	public User getUser(String userName) {
		return cache.getUnchecked(userName);
	}
	
	/**
	 * 这里只是测试
	 * @param userName
	 * @return
	 */
	public User getUserFromDB(String userName) {
		User user = new User();
		user.setUsername(userName);
		return user;
	}
	
}
