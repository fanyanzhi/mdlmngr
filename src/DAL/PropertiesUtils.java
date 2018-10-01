package DAL;

import java.io.IOException;
import java.util.Properties;

class PropertiesUtils {
	static Properties prop = new Properties();

	protected static boolean loadFile(String fileName) {
		try {
			prop.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected static String getPropertyValue(String key) {
		return prop.getProperty(key);
	}
}
