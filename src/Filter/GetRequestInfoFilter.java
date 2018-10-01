package Filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.log4j.Logger;

import BLL.AppInfoMngr;
import BLL.SysConfigMngr;
import Model.CharResponseWrapper;
import Model.HttpContext;
import Util.Common;
import Util.HuaWeiPush;
import Util.LoggerFile;
import net.sf.json.JSONObject;

/**
 * Servlet Filter implementation class GetRequestInfoFilter
 */
@WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, urlPatterns = { "/*" })
public class GetRequestInfoFilter implements Filter {
	protected static Logger log = Logger.getLogger(GetRequestInfoFilter.class);

	/**
	 * Default constructor.
	 */
	public GetRequestInfoFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String accToken = httpRequest.getHeader("auth");
		if(!Common.IsNullOrEmpty(accToken)){
			int auth = AppInfoMngr.validAccToken(accToken);
			if(auth != 1){
				response.getOutputStream().write("{\"result\":false,\"message\":\"".concat(String.valueOf(auth)).concat("\",\"errorcode\":").concat(String.valueOf(auth)).concat("}").getBytes("utf-8"));
			}
		}
		String appId = httpRequest.getHeader("app_id");
		String sign = httpRequest.getHeader("sign");
		String timeStamp = httpRequest.getHeader("timestamp");
		if (!Common.IsNullOrEmpty(appId)) {
			if (!chkAppId(appId, timeStamp, sign)) {
				//centos系统下
				LoggerFile.appendMethod("/root/appid",
						 "appId " + appId + ";timeStamp:" + timeStamp +"-->"+sign+
						 ";strIP:" + request.getRemoteAddr());
				//window系统下
				/*LoggerFile.appendMethod("d:\\appid",
						 "appId " + appId + ";timeStamp:" + timeStamp +"-->"+sign+
						 ";strIP:" + request.getRemoteAddr());*/
				response.getOutputStream().write("{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_APPID.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_APPID.code)).concat("}").getBytes("utf-8"));
				response.getOutputStream().close();
				return;
			}
		}
		request.setAttribute("app_id", Common.IsNullOrEmpty(appId) ? "cnki_cajcloud" : appId);
		HttpContext.SetRequest((HttpServletRequest) request);
		chain.doFilter(request, response);
	}
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	private boolean chkAppId(String appId, String timeStamp, String sign) {
		String appKey = AppInfoMngr.getValidAppKey(appId);
		if (Common.IsNullOrEmpty(appKey))
			return false;
		if (!Common.SHA1(timeStamp.concat(appKey)).equals(sign))
			return false;
		return true;
	}

	private boolean checkValidReq(ServletRequest req) {
		HttpServletRequest request = (HttpServletRequest) req;
		String ip = Common.getClientIP(request);
		System.out.println("getRequestURL: " + request.getRequestURL());
		System.out.println("getRequestURI: " + request.getRequestURI());
		System.out.println("getQueryString: " + request.getQueryString());
		System.out.println("getRemoteAddr: " + request.getRemoteAddr());
		System.out.println("getRemoteHost: " + request.getRemoteHost());
		System.out.println("getRemotePort: " + request.getRemotePort());
		System.out.println("getRemoteUser: " + request.getRemoteUser());
		System.out.println("getLocalAddr: " + request.getLocalAddr());
		System.out.println("getLocalName: " + request.getLocalName());
		System.out.println("getLocalPort: " + request.getLocalPort());
		System.out.println("getMethod: " + request.getMethod());
		return true;

	}

}
