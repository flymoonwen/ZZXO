package com.xiaoao.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 随机昵称
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2016-1-20下午3:56:16
 */
public class RandomNameManager {
	public static final String filePath = "/config/name.txt";
	private static RandomNameManager instance;
	public static RandomNameManager getInstance() {
		if(instance == null) {
			instance = new RandomNameManager();
			instance.init();
		}
		return instance;
	}
	
	private List<String> fristName  = new ArrayList<String>();
	private List<String> secondName = new ArrayList<String>();
	
	private void init() {
		try {
			InputStreamReader read = new InputStreamReader(BadStringFilter.class
					.getResourceAsStream(filePath),"utf-8");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				String[] names = lineTxt.split("#");
				if(!names[0].trim().isEmpty()) {
					fristName.add(names[0].trim());
				}
				secondName.add(names[1].trim());
			}
			read.close();
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		System.err.println("随机昵称数 姓:"+fristName.size());
		System.err.println("随机昵称数 名:"+secondName.size());
	}
	
	public String randomName() {
		int r1 = new Random().nextInt(fristName.size());
		int r2 = new Random().nextInt(secondName.size());
		return fristName.get(r1)+secondName.get(r2);
	}
}
