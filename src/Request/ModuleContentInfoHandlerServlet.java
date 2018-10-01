package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Logger;
import BLL.ModuleMngr;
import Model.ModuleContentInfoBean;
import Util.Common;

/**
 * Servlet implementation class ModuleContentInfoHandlerServlet
 */
@WebServlet("/ModuleContentInfoHandler.do")
public class ModuleContentInfoHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModuleContentInfoHandlerServlet() {
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
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();

		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getModuleContentCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getModuleContentInfo(request);
		} else if ("save".equals(request.getParameter("do"))) {
			strResult = saveModuleTypeList(request);
		}
		out.write(strResult);
		out.flush();
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";

		PrintWriter out = response.getWriter();
		strResult = saveModuleTypeList(request);

		out.write(strResult);
		out.flush();
		out.close();
	}

	private String getModuleContentCount(HttpServletRequest request) throws ServletException, IOException {
		String strTableID = request.getParameter("hidtabid");
		return String.valueOf(ModuleMngr.getModuleContentInfoCountByTabID(strTableID));
	}

	private String getModuleContentInfo(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<table id=\"fieldtab\" name=\"fieldtab\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"120\">名称</th>");
		sbHtml.append("<th width=\"80\">类型</th>");
		sbHtml.append("<th width=\"50\">长度</th>");
		sbHtml.append("<th width=\"12%\">英文名称</th>");
		sbHtml.append("<th>中文名称</th>");
		sbHtml.append("<th width=\"7%\">是否显示</th>");
		sbHtml.append("<th width=\"7%\">是否索引</th>");
		sbHtml.append("<th width=\"7%\">是否为空</th>");
		sbHtml.append("<th width=\"7%\">是否主键</th>");
		sbHtml.append("<th width=\"40\">操作</th>");
		sbHtml.append("</tr>");

		String strTableID = request.getParameter("hidtabid");
		List<ModuleContentInfoBean> lstModuleType = ModuleMngr.getModuleContentInfoByTabID(strTableID);
		if (lstModuleType == null) {
			// sbHtml.append("<tr>");
			// sbHtml.append("<td class=\"num\">1</td>");
			// sbHtml.append("<td>id</td>");
			// sbHtml.append("<td class=\"tabcent\">&nbsp;</td>");
			// sbHtml.append("<td class=\"tabcent\">&nbsp;</td>");
			// sbHtml.append("<td><input type=\"text\" name=\"fileden1\" id=\"fileden1\" value=\"").append("iden").append("\" class=\"addvalue\" /></td>");
			// sbHtml.append("<td class=\"tabcent\"><input type=\"text\" name=\"filedch1\" id=\"filedch1\" value=\"").append("标示").append("\" class=\"addvalue\" /></td>");
			// sbHtml.append("<td class=\"tabcent\">否</td>");
			// sbHtml.append("<td class=\"tabcent\">是</td>");
			// sbHtml.append("<td class=\"tabcent\">否</td>");
			// sbHtml.append("<td class=\"tabcent\">否</td>");
			// sbHtml.append("<td class=\"tabopt tableft\"><a href=\"#\" class=\"del\" title=\"删除\"></a></td>");
			// sbHtml.append("</tr>");
			sbHtml.append("<tr class=\"tabtotopt\">");
			sbHtml.append("<td class=\"num\"> + </td>");
			sbHtml.append("<td colspan=\"10\">  <a href=\"javascript:void(0);\" onclick=\"addRow();\" class=\"addnewtd\">新增</a>");
			sbHtml.append("</td>");
			sbHtml.append("</tr>");
			sbHtml.append("</table>");
			sbHtml.append("<div class=\"addsure\">");
			sbHtml.append("<span style=\"float:left\" id=\"tipspan\"></span>");
			sbHtml.append("<input name=\"\" type=\"button\" value=\"完成\" class=\"addsurebtn\"  onclick=\"saveFields()\"/>");
			sbHtml.append("</div>");
			return sbHtml.toString();
		}

		Iterator<ModuleContentInfoBean> iMap = lstModuleType.iterator();
		ModuleContentInfoBean moduleContentInfo = null;

		int iNum = 0;
		// StringBuilder sbIDHtml = new StringBuilder();
		StringBuilder sbFieldHtml = new StringBuilder();
		while (iMap.hasNext()) {

			moduleContentInfo = iMap.next();
			if ("time".equals(moduleContentInfo.getFieldName().toLowerCase())) {
				continue;
			}
			iNum++;
			// if ("id".equals(moduleContentInfo.getFieldName().toLowerCase()))
			// {
			// sbIDHtml.append("<tr>");
			// sbIDHtml.append("<td class=\"num\">1</td>");
			// sbIDHtml.append("<td>id</td>");
			// sbIDHtml.append("<td class=\"tabcent\">&nbsp;</td>");
			// sbIDHtml.append("<td class=\"tabcent\">&nbsp;</td>");
			// sbIDHtml.append("<td><input type=\"text\" name=\"fileden1\" id=\"fileden1\" value=\"").append(moduleContentInfo.getFieldName_EN()).append("\" class=\"addvalue\" /></td>");
			// sbIDHtml.append("<td class=\"tabcent\"><input type=\"text\" name=\"filedch1\" id=\"filedch1\" value=\"").append(moduleContentInfo.getFieldName_CH()).append("\" class=\"addvalue\" /></td>");
			// sbIDHtml.append("<td class=\"tabcent\">否</td>");
			// sbIDHtml.append("<td class=\"tabcent\">是</td>");
			// sbIDHtml.append("<td class=\"tabcent\">否</td>");
			// sbIDHtml.append("<td class=\"tabopt tableft\">&nbsp;</td>");
			// sbHtml.append("</tr>");
			// continue;
			// }
			sbFieldHtml.append("<tr>");
			sbFieldHtml.append("<td class=\"num\">").append(iNum).append("</td>");
			sbFieldHtml.append("<td><input type=\"text\" name=\"filedname").append(iNum).append("\" id=\"filedname").append(iNum).append("\" value=\"").append(moduleContentInfo.getFieldName()).append("\" class=\"addvalue\" /></td>");
			String strFieldType = moduleContentInfo.getFieldType();
			boolean bText = "text".equals(strFieldType);
			boolean bTime = "datetime".equals(strFieldType);
			sbFieldHtml.append("<td class=\"tabcent\">").append(setFieldType(iNum, strFieldType)).append("</td>");
			if(bText||bTime){
				sbFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" name=\"filedlen").append(iNum).append("\" id=\"filedlen").append(iNum).append("\" value=\"\" check=\"no\" class=\"addvalue\" ").append((bText || bTime) ? "disabled='true'" : "").append(" /></td>");
			}else{
				sbFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" name=\"filedlen").append(iNum).append("\" id=\"filedlen").append(iNum).append("\" value=\"").append(String.valueOf(moduleContentInfo.getFieldLength())).append("\" class=\"addvalue\" ").append((bText || bTime) ? "disabled='true'" : "").append(" /></td>");
			}
			sbFieldHtml.append("<td><input type=\"text\" name=\"fileden").append(iNum).append("\" id=\"fileden").append(iNum).append("\" value=\"").append(moduleContentInfo.getFieldName_EN()).append("\" class=\"addvalue\" /></td>");
			sbFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" name=\"filedch").append(iNum).append("\" id=\"filedch").append(iNum).append("\" value=\"").append(moduleContentInfo.getFieldName_CH()).append("\" class=\"addvalue\" /></td>");
			sbFieldHtml.append("<td class=\"tabcent\"><select name=\"isdisplay").append(iNum).append("\" id=\"isdisplay").append(iNum).append("\"><option value=\"true\" ").append(moduleContentInfo.isDisplay() ? "selected" : "").append(">是</option><option value=\"false\" ").append(moduleContentInfo.isDisplay() ? "" : "selected").append(">否</option></select></td>");
			sbFieldHtml.append("<td class=\"tabcent\"><select name=\"isindex").append(iNum).append("\" id=\"isindex").append(iNum).append("\" ").append(bText ? "disabled='true'" : "").append(" ><option value=\"true\" ").append(moduleContentInfo.isIndex() ? "selected" : "").append(">是</option><option value=\"false\" ").append(moduleContentInfo.isIndex() ? "" : "selected").append(">否</option></select></td>");
			sbFieldHtml.append("<td class=\"tabcent\"><select name=\"isnull").append(iNum).append("\" id=\"isnull").append(iNum).append("\"><option value=\"true\" ").append(moduleContentInfo.isNull() ? "selected" : "").append(">是</option><option value=\"false\" ").append(moduleContentInfo.isNull() ? "" : "selected").append(">否</option></select></td>");
			sbFieldHtml.append("<td class=\"tabcent\"><select name=\"isprimkey").append(iNum).append("\" id=\"isprimkey").append(iNum).append("\" ").append(bText ? "disabled='true'" : "").append("><option value=\"true\" ").append(moduleContentInfo.isPrimKey() ? "selected" : "").append(">是</option><option value=\"false\" ").append(moduleContentInfo.isPrimKey() ? "" : "selected").append(">否</option></select></td>");
			sbFieldHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"del\" title=\"删除\"  onclick='deleteRow(").append(iNum).append(")'></a></td>");
			sbFieldHtml.append("</tr>");
		}
		// sbHtml.append(sbIDHtml.toString());
		sbHtml.append(sbFieldHtml.toString());
		// sbIDHtml = null;
		sbFieldHtml = null;
		sbHtml.append("<tr class=\"tabtotopt\">");
		sbHtml.append("<td class=\"num\"> + </td>");
		sbHtml.append("<td colspan=\"10\">  <a href=\"javascript:void(0);\" onclick=\"addRow();\" class=\"addnewtd\">新增</a>");
		sbHtml.append("</td>");
		sbHtml.append("</tr>");
		sbHtml.append("</table>");
		sbHtml.append("<div class=\"addsure\">");
		sbHtml.append("<span style=\"float:left\" id=\"tipspan\"></span>");
		sbHtml.append("<input name=\"\" type=\"button\" value=\"完成\" class=\"addsurebtn\"  onclick=\"saveFields()\"/>");
		sbHtml.append("</div>");
		return sbHtml.toString();
	}

	private String saveModuleTypeList(HttpServletRequest request) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String strResult = "";
		String strFlag = request.getParameter("hidCount");
		String strFlagCount = request.getParameter("hidFlagCount");
		String strTabID = request.getParameter("hidtabid");
		int iFlag = 0;
		int iFlagCount = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
			iFlagCount = Integer.parseInt(strFlagCount);
		} catch (Exception e) {
			Logger.WriteException(e);
			return "保存失败"; //
		}
		Map<String, ModuleContentInfoBean> mapModuleContentInfo = new LinkedHashMap<String, ModuleContentInfoBean>();
		ModuleContentInfoBean moduleContentInfo = null;
		for (int i = 1; i <= iFlag; i++) {
			moduleContentInfo = new ModuleContentInfoBean();
			String strFiledname = request.getParameter("filedname".concat(String.valueOf(i)));
			if (strFiledname == null||"id".equals(strFiledname)||"time".equals(strFiledname)) {
				continue;
			}
			strFiledname = strFiledname.toLowerCase().replace(" ", "");
			moduleContentInfo.setFieldName(request.getParameter("filedname".concat(String.valueOf(i))).replace(" ", ""));
			moduleContentInfo.setTableID(Integer.parseInt(strTabID));
			String strFieldType = request.getParameter("filedtype".concat(String.valueOf(i)));
			int iFieldLen = 0;
			if ("datetime".equals(strFieldType) || "text".equals(strFieldType)) {
				iFieldLen = 0;
			} else {
				iFieldLen = Integer.parseInt(request.getParameter("filedlen".concat(String.valueOf(i))).replace(" ", ""));
			}
			if ("int".equals(strFieldType)) {
				if (iFieldLen == 0) {
					iFieldLen = 11;
				}
			}
			moduleContentInfo.setFieldType(strFieldType);
			moduleContentInfo.setFieldLength(iFieldLen);
			moduleContentInfo.setFieldName_EN(request.getParameter("fileden".concat(String.valueOf(i))).replace(" ", ""));
			moduleContentInfo.setFieldName_CH(request.getParameter("filedch".concat(String.valueOf(i))).replace(" ", ""));
			moduleContentInfo.setDisplay(Boolean.parseBoolean(request.getParameter("isdisplay".concat(String.valueOf(i)))));
			if ("text".equals(strFieldType)) {
				moduleContentInfo.setIndex(false);
			} else {
				moduleContentInfo.setIndex(Boolean.parseBoolean(request.getParameter("isindex".concat(String.valueOf(i)))));
			}
			moduleContentInfo.setNull(Boolean.parseBoolean(request.getParameter("isnull".concat(String.valueOf(i)))));
			if ("text".equals(strFieldType)) {
				moduleContentInfo.setPrimKey(false);
			} else {
				moduleContentInfo.setPrimKey(Boolean.parseBoolean(request.getParameter("isprimkey".concat(String.valueOf(i)))));
			}
			moduleContentInfo.setTime(Common.GetDateTime());
			mapModuleContentInfo.put(strFiledname, moduleContentInfo);
		}
		if (iFlagCount == 0) {
			if (mapModuleContentInfo.size() == 0) {
				return "请添加数据后保存";
			}
		}
		// mapModuleContentInfo.put("id", initModuleContentID(request));
		mapModuleContentInfo.put("time", initModuleContentTime(request));
		strResult = ModuleMngr.saveModuleContentInfo(strTabID, mapModuleContentInfo);
		return getPrompts(strResult);
	}

	// private ModuleContentInfoBean initModuleContentID(HttpServletRequest
	// request) throws ServletException, IOException {
	// ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean();
	// moduleContentInfo.setTableID(Integer.parseInt(request.getParameter("hidtabid")));
	//
	// moduleContentInfo.setFieldName("id");
	// moduleContentInfo.setFieldName_CH(request.getParameter("filedch1"));
	// moduleContentInfo.setFieldName_EN(request.getParameter("fileden1"));
	// moduleContentInfo.setFieldType("int");
	// moduleContentInfo.setFieldLength(500);
	// moduleContentInfo.setAutoIncrement(true);
	// moduleContentInfo.setDisplay(false);
	// moduleContentInfo.setIndex(false);
	// moduleContentInfo.setNull(false);
	// moduleContentInfo.setPrimKey(true);
	// moduleContentInfo.setTime(Common.GetDateTime());
	// return moduleContentInfo;
	// }

	private ModuleContentInfoBean initModuleContentTime(HttpServletRequest request) throws ServletException, IOException {
		ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean();
		moduleContentInfo.setTableID(Integer.parseInt(request.getParameter("hidtabid")));
		moduleContentInfo.setFieldName("time");
		moduleContentInfo.setFieldName_CH("更新时间");
		moduleContentInfo.setFieldName_EN("time");
		moduleContentInfo.setFieldType("datetime");
		moduleContentInfo.setFieldLength(0);
		moduleContentInfo.setAutoIncrement(false);
		moduleContentInfo.setDisplay(true);
		moduleContentInfo.setIndex(true);
		moduleContentInfo.setNull(false);
		moduleContentInfo.setPrimKey(false);
		moduleContentInfo.setTime(Common.GetDateTime());
		return moduleContentInfo;
	}

	private String getPrompts(String PromptNum) {
		switch (PromptNum) {
		case "1":
			return "保存成功";
		case "2":
			return "插入数据失败,原因：建表失败";
		case "3":
			return "删除表失败";
		case "4":
			return "建表失败";
		case "5":
			return "有数据不允许删除字段";
		case "6":
			return "有数据，不允许插入不为空的字段";
		case "7":
			return "有数据，更改表结构失败";
		case "8":
			return "更改表结构失败";
		case "9":
			return "增加字段失败";
		case "10":
			return "为表结构增加字段失败";
		case "11":
			return "更新表字段数据失败";
		case "12":
			return "更新表字段数据失败";
		case "13":
			return "删除表字段值失败";
		case "14":
			return "删除表结构是失败";
		case "15":
			return "表中有数据不能删除表";
		default:
			return "保存成功";
		}
	}

	private String setFieldType(int iNum, String FieldType) {
		StringBuilder sbFieldType = new StringBuilder();
		sbFieldType.append("<select name=\"filedtype").append(iNum).append("\" id=\"filedtype").append(iNum).append("\" onchange=\"changeinput(this)\">");
		sbFieldType.append("<option value=\"varchar\" ").append(FieldType.equals("varchar") ? "selected" : "").append(">string</option>");
		sbFieldType.append("<option value=\"int\" ").append(FieldType.equals("int") ? "selected" : "").append(">int</option>");
		sbFieldType.append("<option value=\"datetime\" ").append(FieldType.equals("datetime") ? "selected" : "").append(">datetime</option>");
		sbFieldType.append("<option value=\"text\" ").append(FieldType.equals("text") ? "selected" : "").append(">text</option>");
		sbFieldType.append("</select>");
		return sbFieldType.toString();
	}

}
