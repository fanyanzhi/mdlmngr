package RecommendMngr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.CnkiMngr;
import BLL.Logger;
import BLL.RecommendationInfoMngr;
import BLL.UserInfoMngr;
import Model.OrgBookInfoBean;
import Model.OrgJournalInfoBean;
import Model.OrgPicDetailInfoBean;
import Model.OrgPicIndexInfoBean;
import Model.OrgVideoInfoBean;
import Util.Common;

/**
 * Servlet implementation class JournalInfoServlet
 */
@WebServlet("/OrgInfo.do")
public class OrgInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgInfoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String strOrgPY = request.getParameter("opy");
		String strTypeID = request.getParameter("vtypeid");
		String sourceType = request.getParameter("sourceType");
		if (Common.IsNullOrEmpty(strOrgPY) || Common.IsNullOrEmpty(strTypeID)) {
			response.sendRedirect("RecommendSearch.do");
			return;
		}
		if("YNKX_CACM".equals(sourceType)){
			OrgJournalInfoBean orgJournalInfo = getOrgJournalInfo(request);
			if (orgJournalInfo == null) {
				response.sendRedirect("RecommendSearch.do");
				return;
			}
			boolean bRecomand = RecommendationInfoMngr.isHasRecommend(strTypeID, strOrgPY);
			request.setAttribute("ImageURL", getOrgJournalImageURL(request));
			request.setAttribute("IsRecomand", bRecomand);
			request.setAttribute("orgJournalInfo", orgJournalInfo);
			request.setAttribute("orgPY", strOrgPY);
			request.setAttribute("TypeID", strTypeID);
			request.setAttribute("HandlerURL", "JournalInfoHandler.do");
			request.getRequestDispatcher("/RecommendMngr/orgjournalinfo.jsp").forward(request, response);
		}
		if("YNKX_BOOKINFO".equals(sourceType)){
			OrgBookInfoBean orgBookInfo = getOrgBookInfo(request);
			if (orgBookInfo == null) {
				response.sendRedirect("RecommendSearch.do");
				return;
			}
			boolean bRecomand = RecommendationInfoMngr.isHasRecommend(strTypeID, strOrgPY);
			request.setAttribute("ImageURL", getOrgBookImageURL(request));
			request.setAttribute("IsRecomand", bRecomand);
			request.setAttribute("orgBookInfo", orgBookInfo);
			request.setAttribute("orgPY", strOrgPY);
			request.setAttribute("TypeID", strTypeID);
			request.setAttribute("HandlerURL", "JournalInfoHandler.do");
			request.getRequestDispatcher("/RecommendMngr/orgbookinfo.jsp").forward(request, response);
		}
		if("YNKX_CACV".equals(sourceType)){
			OrgVideoInfoBean orgVideoInfoBean = getOrgVideoInfo(request);
			if (orgVideoInfoBean == null) {
				response.sendRedirect("RecommendSearch.do");
				return;
			}
			boolean bRecomand = RecommendationInfoMngr.isHasRecommend(strTypeID, strOrgPY);
			request.setAttribute("ImageURL", getOrgVideoImageURL(request));
			request.setAttribute("IsRecomand", bRecomand);
			request.setAttribute("orgVideoInfo", orgVideoInfoBean);
			request.setAttribute("orgPY", strOrgPY);
			request.setAttribute("TypeID", strTypeID);
			request.setAttribute("HandlerURL", "JournalInfoHandler.do");
			request.getRequestDispatcher("/RecommendMngr/orgvideoinfo.jsp").forward(request, response);
		}
		if("YNKX_PICINDEX".equals(sourceType)){
			OrgPicIndexInfoBean orgPicIndexInfoBean = getOrgPicIndexInfo(request);
			if (orgPicIndexInfoBean == null) {
				response.sendRedirect("RecommendSearch.do");
				return;
			}
		/*	List<OrgPicDetailInfoBean> orgPicDetailInfoBeanList = getOrgPicDetailInfo(orgPicIndexInfoBean.getPicindexid());
			if(orgPicDetailInfoBeanList.size()>0){
				List<Map<String, String>> picList = getOrgPicDetailImagesURL(orgPicDetailInfoBeanList);
				request.setAttribute("picList", picList);
			}*/
			boolean bRecomand = RecommendationInfoMngr.isHasRecommend(strTypeID, strOrgPY);
			request.setAttribute("IsRecomand", bRecomand);
			request.setAttribute("orgPicIndexInfo", orgPicIndexInfoBean);
			request.setAttribute("orgPY", strOrgPY);
			request.setAttribute("TypeID", strTypeID);
			request.setAttribute("HandlerURL", "JournalInfoHandler.do");
			request.getRequestDispatcher("/RecommendMngr/orgpicindexinfo.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		PrintWriter pw = response.getWriter();
		String picindexid = request.getParameter("picindexid");
		List<OrgPicDetailInfoBean> orgPicDetailInfoBeanList = getOrgPicDetailInfo(picindexid);
		List<Map<String, String>> picList=null;
		if(orgPicDetailInfoBeanList.size()>0){
			picList = getOrgPicDetailImagesURL(orgPicDetailInfoBeanList);
		}
		pw.write(JSONArray.fromObject(picList).toString());
		pw.flush();
		pw.close();
	}
	
	protected OrgBookInfoBean getOrgBookInfo(HttpServletRequest request) throws ServletException, IOException {
		OrgBookInfoBean orgBookInfoBean = null;
		String strOPY = request.getParameter("opy");
		if (Common.IsNullOrEmpty(strOPY)) {
			return null;
		}
		String type="YNKX_BOOKINFO";
		String fields="BookTitle,Creator,Publisher,PublicationPlace,PublishDate,ISBN,BookPrice,PageNo,size,ElectronicPrice,Summary";
		JSONObject jsonSeaData = CnkiMngr.getSingleNewOdata(type, strOPY,fields);
		if(jsonSeaData==null||"0".equals(jsonSeaData.get("Count"))){
			return null;
		}
		Map<String, Object> mapBookInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(0)).get("Cells"));
		for (int i = 0; i < instanceArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
			mapBookInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
		}
		if (mapBookInfo != null) {
			orgBookInfoBean = new OrgBookInfoBean(mapBookInfo);
		}
		return orgBookInfoBean;
	}
	protected OrgVideoInfoBean getOrgVideoInfo(HttpServletRequest request) throws ServletException, IOException {
		OrgVideoInfoBean orgVideoInfo = null;
		String strOPY = request.getParameter("opy");
		if (Common.IsNullOrEmpty(strOPY)) {
			return null;
		}
		String type="YNKX_CACV";
		String fields="ColumnName,ShowName,BroadcastDate,Duration,Director,FileSize,ShowInfo";
		JSONObject jsonSeaData = CnkiMngr.getSingleNewOdata(type, strOPY,fields);
		if(jsonSeaData==null||"0".equals(jsonSeaData.get("Count"))){
			return null;
		}
		Map<String, Object> mapVideoInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(0)).get("Cells"));
		for (int i = 0; i < instanceArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
			mapVideoInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
		}
		if (mapVideoInfo != null) {
			orgVideoInfo = new OrgVideoInfoBean(mapVideoInfo);
		}
		return orgVideoInfo;
	}
	
	protected OrgJournalInfoBean getOrgJournalInfo(HttpServletRequest request) throws ServletException, IOException {
		OrgJournalInfoBean orgJournalInfo = null;
		String strOPY = request.getParameter("opy");
		if (Common.IsNullOrEmpty(strOPY)) {
			return null;
		}
		String type="YNKX_CACM";
		String fields="JournalName,JournalName@EN,Creator,Publisher,Language,ISSN,CN,PublishDate,Summary";
		JSONObject jsonSeaData = CnkiMngr.getSingleNewOdata(type, strOPY,fields);
		if(jsonSeaData==null||"0".equals(jsonSeaData.get("Count"))){
			return null;
		}
		Map<String, Object> mapJournalInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(0)).get("Cells"));
		for (int i = 0; i < instanceArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
			if(String.valueOf(recordObj.get("Name")).equals("JournalName@EN")){
				mapJournalInfo.put("journalnameen", String.valueOf(recordObj.get("Value")));
			}
			mapJournalInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
		}
		if (mapJournalInfo != null) {
			orgJournalInfo = new OrgJournalInfoBean(mapJournalInfo);
		}
		return orgJournalInfo;
	}
	
	
	protected OrgPicIndexInfoBean getOrgPicIndexInfo(HttpServletRequest request) throws ServletException, IOException {
		OrgPicIndexInfoBean orgPicIndexInfoBean = null;
		String strOPY = request.getParameter("opy");
		if (Common.IsNullOrEmpty(strOPY)) {
			return null;
		}
		String type="YNKX_PICINDEX";
		String fields="PicIndexID,PicFormat,PicIndexNum,PicIndexTheme,PicSize,Resolution,SubjectName";
		JSONObject jsonSeaData = CnkiMngr.getSingleNewOdata(type, strOPY,fields);
		if(jsonSeaData==null||"0".equals(jsonSeaData.get("Count"))){
			return null;
		}
		Map<String, Object> mapPicIndexInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(jsonArray.get(0)).get("Cells"));
		for (int i = 0; i < instanceArray.size(); i++) {
			JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
			mapPicIndexInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
		}
		if (mapPicIndexInfo != null) {
			orgPicIndexInfoBean = new OrgPicIndexInfoBean(mapPicIndexInfo);
		}
		return orgPicIndexInfoBean;
	}
	
	protected List<OrgPicDetailInfoBean> getOrgPicDetailInfo(String picId) throws ServletException, IOException {
		OrgPicDetailInfoBean orgPicDetailInfoBean = null;
		List<OrgPicDetailInfoBean> list = new ArrayList<OrgPicDetailInfoBean>();
		if (Common.IsNullOrEmpty(picId)) {
			return null;
		}
		String type="YNKX_PIC";
		String fields="BelongsPicIndexID,PicFileName,PicID";
		String query = "BelongsPicIndexID eq '"+picId+"'";
		JSONObject jsonSeaData = CnkiMngr.getNewOdataInfo(type, fields, query, "", "", 0, 100);
		if(jsonSeaData==null||"0".equals(jsonSeaData.get("Count"))){
			return null;
		}
		Map<String, Object> mapPicIndexInfo = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(jsonSeaData.get("Rows"));
		for(Object object :jsonArray){
			mapPicIndexInfo.clear();
			JSONArray instanceArray = JSONArray.fromObject(JSONObject.fromObject(object).get("Cells"));
			for (int i = 0; i < instanceArray.size(); i++) {
				JSONObject recordObj = JSONObject.fromObject(instanceArray.get(i));
				mapPicIndexInfo.put(String.valueOf(recordObj.get("Name")).toLowerCase(), String.valueOf(recordObj.get("Value")));
			}
			if (mapPicIndexInfo != null) {
				orgPicDetailInfoBean = new OrgPicDetailInfoBean(mapPicIndexInfo);
				list.add(orgPicDetailInfoBean);
			}
		}
		return list;
	}
	
	protected String getOrgJournalImageURL(HttpServletRequest request) throws ServletException, IOException {
		String strOPY = request.getParameter("opy");
		String trim = strOPY.replaceAll("\\d", "").trim();
		String orgImageUrl = Common.GetConfig("orgImageUrl");	
		String strUrl =orgImageUrl.concat("/ynkx/CACM/fm/big/").concat(trim).concat("/").concat(strOPY.toLowerCase().substring(0,strOPY.length()-3)).concat(".jpg");
		return getConfirmResult(strUrl);
	}
	protected String getOrgVideoImageURL(HttpServletRequest request) throws ServletException, IOException {
		String strOPY = request.getParameter("opy");
		String orgImageUrl = Common.GetConfig("orgImageUrl");
		String strUrl =orgImageUrl.concat("/ynkx/CACV/jpg/").concat(strOPY).concat(".jpg");
		return getConfirmResult(strUrl);
	}
	protected String getOrgBookImageURL(HttpServletRequest request) throws ServletException, IOException {
		String bookNo = request.getParameter("opy");
		String orgImageUrl = Common.GetConfig("orgImageUrl");
		String strUrl =orgImageUrl.concat("/ynkx/CACB/jpg/").concat(bookNo).concat(".jpg");
		return getConfirmResult(strUrl);
	}
	protected List<Map<String, String>> getOrgPicDetailImagesURL(List<OrgPicDetailInfoBean> list) throws ServletException, IOException {
		//http://192.168.103.234:8091/ynkx/PIC/E083/M_E083001.jpg
		List<Map<String, String>> urlList = new ArrayList<Map<String, String>>();
		for(OrgPicDetailInfoBean info:list){
			Map<String, String> map = new HashMap<String, String>();
			String orgImageUrl = Common.GetConfig("orgImageUrl");
			String maxUrl =orgImageUrl.concat("/ynkx/PIC/").concat(info.getBelongspicindexid()).concat("/M_").concat(info.getPicfilename());
			String smallUrl =orgImageUrl.concat("/ynkx/PIC/").concat(info.getBelongspicindexid()).concat("/S_").concat(info.getPicfilename());
			map.put("maxUrl", maxUrl);
			map.put("smallUrl", smallUrl);
			urlList.add(map);
		}
		return urlList;
	}
	
	private String getConfirmResult(String strUrl){
		// 获取用户Token
				String strSysToken = null;
				int iNum = 10;
				while (strSysToken == null) {
					strSysToken = UserInfoMngr.getUserToken();
					--iNum;
					if (iNum < 0) {
						return "\"images/nopic.png\"";
					}
				}
				try {
					String url = Common.confirmImage(strUrl, strSysToken);
					if(Common.IsNullOrEmpty(url)){
						return "\"images/nopic.png\"";
					}
					return url;
				} catch (Exception e) {
					Logger.WriteException(e);
				}
				return "\"images/nopic.png\"";
	}
}
