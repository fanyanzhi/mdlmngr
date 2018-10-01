package UIL;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import BLL.Logger;
import Model.HttpContext;

public class DropDownListTag  extends TagSupport{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = "";
	private int width = 0;
	private String selectedText = "";
	private Map<String, Object> value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	public Map<String, Object> getValue() {
		return value;
	}

	public void setValue(Map<String, Object> value) {
		this.value = value;
	}

	public int doStartTag() throws JspException {
		if (value == null) {
			value = new LinkedHashMap<String, Object>();
		}
		StringBuilder sbOut = new StringBuilder();
		Iterator<Entry<String, Object>> iEntry = value.entrySet().iterator();
		String strText;
		Object objValue;
		String strSelectedText = null;
		Object objSelectedValue = null;
		sbOut.append("<p id=\"").append(id).append("\"  class=\"selectsimi rigselect\">\r\n");
		sbOut.append("<a id=\"").append(id).append("_ddl\" class=\"select\" style=\"width:").append(width).append("px;\"></a>\r\n");
		sbOut.append("<span id='").append(id).append("_ddlul' tabindex='0' hidefocus='true' class='hideoption' style='width:").append(width + 15).append("px;outline:0;'>\r\n");
		while (iEntry.hasNext()) {
			Entry<String, Object> entryData = iEntry.next();
			strText = entryData.getKey();
			objValue = entryData.getValue();
			if (strSelectedText == null || strText.equals(selectedText)) {
				strSelectedText = strText;
				objSelectedValue = objValue;
			}
			sbOut.append("<em title=\"").append(strText).append("\"  onclick=\"drpItemSelected('").append(id).append("','").append(strText).append("','").append(objValue).append("');\">").append(strText).append("</em>\r\n");
		}
		sbOut.append("</span>\r\n");
		sbOut.append("<input type=\"hidden\" id=\"").append(id).append("_SelectText\" name=\"").append(id).append("_SelectText\" />\r\n");
		sbOut.append("<input type=\"hidden\" id=\"").append(id).append("_SelectValue\" name=\"").append(id).append("_SelectValue\" />\r\n");
		sbOut.append("<script type=\"text/javascript\" language=\"javascript\" src=\"").append(HttpContext.GetRequest().getContextPath()).append("/js/dropdonwlisttag.js\" >").append("</script>\r\n");
		sbOut.append("<script type=\"text/javascript\" language=\"javascript\">\r\n");
		sbOut.append("DrpTagInit(\"").append(id).append("\",\"").append(strSelectedText).append("\",\"").append(objSelectedValue).append("\");\r\n");
		sbOut.append("</script>\r\n");
		sbOut.append("</p>\r\n");
		JspWriter out = pageContext.getOut();
		try {
			out.print(sbOut.toString());
		} catch (IOException e) {
			Logger.WriteException(e);
		}
		return SKIP_BODY;
	}
}
