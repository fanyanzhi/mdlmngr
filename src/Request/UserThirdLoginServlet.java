package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import BLL.SysConfigMngr;
import BLL.UserInfoMngr;
import Model.UserInfoBean;
import Util.Common;


import Util.SHA1;

//import com.qq.connect.QQConnectException;
//import com.qq.connect.oauth.Oauth;
//import java.security.InvalidKeyException;
//import java.security.Key;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.spec.InvalidKeySpecException;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.DESKeySpec;
//
//import org.apache.commons.codec.binary.Base64;

/**
 * Servlet implementation class UserThirdLoginServlet
 */
@WebServlet("/thirduser/*")
public class UserThirdLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String KEY_ALGORITHM = "DES";
	// 算法名称/加密模式/填充方式
	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
	public static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserThirdLoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");

		// String strTo = request.getParameter("to");
		// switch (strTo) {
		// case "qq":
		// qqUserLogin(request,response);
		// break;
		// case "sina":
		// sinaUserLogin(request,response);
		// break;
		// default:
		//
		// }

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}
		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;
		JSONObject jo = JSONObject.fromObject(strReq);
		Map<String, String> mapReq = (Map<String, String>) jo;
		String ip = Common.getClientIP(request);
		//ip = "121.17.160.181";
		mapReq.put("ip", ip);

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "qqlogin":
			strRet = qqUserLogin(mapReq);
			break;
		case "sinalogin":
			strRet = sinaUserLogin(mapReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}

		sendResponseData(response, strRet);

	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	// private void qqUserLogin(HttpServletRequest request, HttpServletResponse
	// response) throws ServletException, IOException {
	// try {
	// response.sendRedirect(new Oauth().getAuthorizeURL(request));
	// } catch (QQConnectException e) {
	// e.printStackTrace();
	// }
	// }
	//

	private String qqUserLogin(Map<String, String> LoginInfo) throws ServletException, IOException {
		int secKey = 8856;
		String sign = LoginInfo.get("sign");
		String openID = LoginInfo.get("openid");
		String timeStamp = LoginInfo.get("timestamp");
		int key = secKey + Integer.parseInt(timeStamp);
		openID = Common.DecryptData(openID, String.valueOf(key));
		String txt = "timestamp=" + timeStamp + "&openid=" + openID + "&action=qqlogin";
		SHA1 sha1 = new SHA1();
		String chkSign = sha1.Digest(txt, "UTF-8");
		if (!sign.equals(chkSign)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String userName = UserInfoMngr.checkQQOpenID(openID);
		String password = "";
		boolean exists = true;
		if (Common.IsNullOrEmpty(userName)) {
			exists = false;
			userName = "qq_" + key;
			password = String.valueOf(System.currentTimeMillis() / 1000).substring(0, 6);
			String checkUserRet = "有";
			while (checkUserRet.equals("有")) {
				//checkUserRet = net.cnki.mngr.TUserMngr.IsExistUserName(userName);
				if (!checkUserRet.equals("有"))
					break;
				userName = "qq_" + (key - 100);
			}
			String regUserRet = "";
			//String regUserRet = net.cnki.mngr.TUserMngr.CreatPersonLib(userName, password, userName, "云阅读系统");
			if (!Common.IsNullOrEmpty(regUserRet)) {
				return "{\"result\":false,\"message\":\"".concat(regUserRet).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_REGISTER.code)).concat("}");
			}
		}
		LoginInfo.put("qqopenid", openID);
		LoginInfo.put("username", userName);
		LoginInfo.put("password", password);
		String strToken = UserInfoMngr.qqUserLogin(new UserInfoBean(LoginInfo), exists);
		if (strToken.startsWith("@-")) {
			return "{\"result\":false,\"message\":\"".concat(strToken.substring(1)).concat("\",\"errorcode\":").concat(strToken.substring(1)).concat("}");
		} else {
			return "{\"result\":true,\"usertoken\":\"".concat(strToken).concat("\",\"username\":\"").concat(userName).concat("\"}");
		}
	}

	private String sinaUserLogin(Map<String, String> LoginInfo) throws ServletException, IOException {
		int secKey = 8856;
		String sign = LoginInfo.get("sign");
		String openID = LoginInfo.get("openid");
		String timeStamp = LoginInfo.get("timestamp");
		int key = secKey + Integer.parseInt(timeStamp);
		openID = Common.DecryptData(openID, String.valueOf(key));
		String txt = "timestamp=" + timeStamp + "&openid=" + openID + "&action=sinalogin";
		SHA1 sha1 = new SHA1();
		String chkSign = sha1.Digest(txt, "UTF-8");
		if (!sign.equals(chkSign)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String userName = UserInfoMngr.checkQQOpenID(openID);
		String password = "";
		boolean exists = true;
		if (Common.IsNullOrEmpty(userName)) {
			exists = false;
			userName = "qq_" + key;
			password = String.valueOf(System.currentTimeMillis() / 1000).substring(0, 6);
			String checkUserRet = "有";
			while (checkUserRet.equals("有")) {
				//checkUserRet = net.cnki.mngr.TUserMngr.IsExistUserName(userName);
				if (!checkUserRet.equals("有"))
					break;
				userName = "sina_" + (key - 100);
			}
			String regUserRet = "";
			//String regUserRet = net.cnki.mngr.TUserMngr.CreatPersonLib(userName, password, userName, "云阅读系统");
			if (!Common.IsNullOrEmpty(regUserRet)) {
				return "{\"result\":false,\"message\":\"".concat(regUserRet).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_REGISTER.code)).concat("}");
			}
		}
		LoginInfo.put("qqopenid", openID);
		LoginInfo.put("username", userName);
		LoginInfo.put("password", password);
		String strToken = UserInfoMngr.qqUserLogin(new UserInfoBean(LoginInfo), exists);
		if (strToken.startsWith("@-")) {
			return "{\"result\":false,\"message\":\"".concat(strToken.substring(1)).concat("\",\"errorcode\":").concat(strToken.substring(1)).concat("}");
		} else {
			return "{\"result\":true,\"usertoken\":\"".concat(strToken).concat("\",\"username\":\"").concat(userName).concat("\"}");
		}
	}

}
