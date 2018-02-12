package com.komodo.InterviewBot.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static String get(String key){
		try{
			Properties properties = new Properties();
			properties.load(new FileInputStream("application.properties"));
			return properties.getProperty(key);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
				
	}
}
