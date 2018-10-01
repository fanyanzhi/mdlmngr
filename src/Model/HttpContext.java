package Model;

import javax.servlet.http.HttpServletRequest;

public class HttpContext {
	public static ThreadLocal<HttpServletRequest> mRequestHodler = new ThreadLocal<HttpServletRequest>();
	
	public static void SetRequest(HttpServletRequest request)
	{
		mRequestHodler.set(request);
	}
	
	public static HttpServletRequest GetRequest()
	{
		return mRequestHodler.get();
	}
	
}
