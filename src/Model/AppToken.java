package Model;

import java.util.Date;

import net.sf.json.JSONObject;
import BLL.Logger;
import BLL.UserInfoMngr;

public class AppToken {
	private static String appToken;
	private static long expires;
	static {
		setAppToken();
	}

	public static String getAppToken() {
		if (new Date().getTime() > expires) {
			setAppToken();
		}
		return appToken;
	}

	private static void setAppToken() {
		JSONObject jsonAppToken = null;
		int i = 10;
		while (jsonAppToken == null && i > 0) {
			jsonAppToken = UserInfoMngr.getAppToken();
			i--;
		}
		if (jsonAppToken != null) {
			appToken = String.valueOf(jsonAppToken.get("access_token"));
			expires = new Date().getTime() + (1000 * (Long.parseLong(String.valueOf(jsonAppToken.get("expires_in"))) - 600));
		} else {
			Logger.WriteException(new Exception("app start 获取apptoken失败"));
		}
	}
}
