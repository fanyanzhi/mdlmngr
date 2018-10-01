package Request;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ODataHelper;
import BLL.SysConfigMngr;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class ConvertServlet
 */
@WebServlet("/convert/*")
public class ConvertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConvertServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		request.getInputStream().read(arrReq);
		String strReq = new String(arrReq, "utf-8");
		arrReq = null;

		JSONObject jo = JSONObject.fromObject(strReq);
		@SuppressWarnings("unchecked")
		Map<String, String> mapReq = (Map<String, String>) jo;

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "word":
			strRet = convertWord(mapReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
			break;
		}
		if (Common.IsNullOrEmpty(strRet)) {
			return;
		}
		sendResponseData(response, strRet);
	}

	private void sendResponseData(HttpServletResponse response, String Data) throws IOException {
		response.getOutputStream().write(Data.getBytes("utf-8"));
		response.getOutputStream().close();
	}

	private String convertWord(Map<String, String> arg) throws ServletException, IOException {
		
		String word = arg.containsKey("word") ? arg.get("word") : "";
		if (Common.IsNullOrEmpty(word)) {
			return "{\"result\":false,\"message\":\""
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":")
					.concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String kwUrl = Common.GetConfig("chkdword");
		kwUrl = kwUrl + "?sword=" + URLEncoder.encode(word, "utf-8");
		String result = Common.sendGet(kwUrl, null);
		JSONObject rest = new JSONObject();
		if(Common.IsNullOrEmpty(result)){
			rest.put("result", true);
			rest.put("subterm", 0);
			//JSONArray jsonary = new JSONArray();
			//JSONObject jsonword = new JSONObject();
			rest.put("word", word);
			//jsonary.add(jsonword);
			rest.put("count", 0);
			return rest.toString();
		}
		JSONArray corwords=  null;
		try{
			corwords = JSONArray.fromObject(result);
		}catch(Exception e){
			rest.put("result", true);
			rest.put("subterm", 0);
			rest.put("word", word);
			rest.put("count", 0);
			return rest.toString();
		}
				
		JSONObject yjsonword = null;
		JSONArray yjsonary = new JSONArray();
		int count = 0;
		boolean bsubword = false;
		StringBuilder sbCodition = new StringBuilder();// ((M主要主题词="3893280" and (M中文文本词="糖尿病" or M英文文本词="糖尿病")) or M主要主题词="3893280") 
		for(int i=0;i<corwords.size();i++){
			yjsonword = new JSONObject();
			if(corwords.getJSONObject(i).containsKey("Code")){
				count = count+1;
				bsubword = true;
				yjsonword.put("code", corwords.getJSONObject(i).get("Code"));	
				sbCodition.append("((MMAINKEY='").append(corwords.getJSONObject(i).get("Code")).append("' and (MCNTEXT='").append(word).append("' or MENTEXT='").append(word).append("')) or MMAINKEY='").append(corwords.getJSONObject(i).get("Code")).append("') or ");
			}
			yjsonary.add(yjsonword);
		}
		if(!bsubword){//没有主题词
			rest.put("result", true);
			rest.put("subterm", 0);
			rest.put("word", word);
			rest.put("count", 0);
			rest.put("data", "[]");
			return rest.toString();
		}else{
			if(sbCodition.length()>0){
				sbCodition.delete(sbCodition.length()-4, sbCodition.length());
			}
			JSONObject jsonData = ODataHelper.GetObjDataLists("CHKD", "id", sbCodition.toString(), "", "", 0, 20);
			if (jsonData.containsKey("Count")&&jsonData.getInt("Count")>0) {
				rest.put("result", true);
				rest.put("subterm", 1);
				rest.put("word", word);
				rest.put("count", count);
				rest.put("data", yjsonary.toString());
				return rest.toString();
			}else{
				rest.put("result", true);
				rest.put("subterm", 0);
				rest.put("count", 0);
				rest.put("data", "[]");
				return rest.toString();
			}
		}
	}

}
