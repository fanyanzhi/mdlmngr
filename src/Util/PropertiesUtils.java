package Util;

import java.io.IOException;
import java.util.Properties;

import BLL.Logger;

public class PropertiesUtils {
	static Properties prop = new Properties();

	public static boolean loadFile(String fileName) {
		try {
			prop.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			Logger.WriteException(e);
			return false;
		}
		return true;
	}

	public static String getPropertyValue(String key) {
		return prop.getProperty(key);
	}
}
