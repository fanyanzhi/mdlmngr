package Util;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;


public class FileUtils {

	public static String encodeDownloadFilename(String filename, String agent)
			throws IOException {
		if (agent.contains("Firefox")) { // 火狐浏览器
			filename = "=?UTF-8?B?"+Base64.encodeBase64(filename.getBytes("utf-8"))+ "?=";
			filename = filename.replaceAll("\r\n", "");
		} else { // IE及其他浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+"," ");
		}
		return filename;
	}
}
