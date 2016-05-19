package com.xiaoao.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadStringFilter {
	public static final String filePath = "/config/pattern.txt";
	private List<Pattern> patternList = new ArrayList<Pattern>();
	private static BadStringFilter instance;
	
	public static BadStringFilter getInstance() {
		if(instance == null) {
			instance = new BadStringFilter();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		StringBuffer sb = readTxtFile(filePath);
		String[] pat = sb.toString().split(",");
		for (int i = 0; i < pat.length; i++) {
			patternList.add(Pattern.compile(pat[i]));
		}
		System.out.println("屏蔽字："+patternList.size());
	}

	public String matcher(String s) {
		Matcher matcher;
		for (Pattern p : patternList) {
			matcher = p.matcher(s);
			s = matcher.replaceAll("*").trim();
			matcher.reset();
		}
		return s;
	}

	public StringBuffer readTxtFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStreamReader read = new InputStreamReader(BadStringFilter.class
					.getResourceAsStream(filePath),"utf-8");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				sb.append(lineTxt);
			}
			read.close();
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return sb;
	}
	
	public static void main(String[] args) {
		System.out.println(BadStringFilter.getInstance().matcher("习近平"));
	}
}
