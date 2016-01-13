package com.hubbleconnected.bridge;

import java.io.InputStream;
import java.util.Properties;

public class Utils {

	private InputStream inputStream;
	private Properties prop;
	
	public Utils() {
		readProperty();
	}

	public void readProperty() {
		String propFileName = "config.properties";
		try {
			prop = new Properties();
			
			inputStream = getClass().getClassLoader().getResourceAsStream(
					propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			}
		} catch (Exception e) {
			System.out.println("Property File Not Found");
		}
	}
	
	public String getProperty(String key){
		return prop.getProperty(key);
	}
}
