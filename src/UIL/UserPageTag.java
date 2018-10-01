package UIL;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import BLL.Logger;

public class UserPageTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	private int curPage = 1; // 当前页
	private int totalPage = 0; // 总页数
	private int totalCount = 0; // 总条数
	private int pageSize = 20; // 每页的总条数
	private String onClick = "";
	private String pageType = "";

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public int doStartTag() throws JspException {
		StringBuilder sbOut = new StringBuilder();
		if (totalCount > 0 && pageSize > 0) {
			if (totalCount % pageSize == 0) {
				totalPage = totalCount / pageSize;
			} else {
				totalPage = totalCount / pageSize + 1;
			}
		}
		if ("simple".equals(this.pageType)) {
			sbOut.append("<div id=\"").append(id).append("\" class=\"simpages\" style=\"display:none;\">");
			sbOut.append("<em id=\"").append(id).append("page\" name=\"").append(id).append("page\" class=\"red\">").append(curPage).append("</em>/<em id=\"").append(id).append("totalpage\" >").append(totalPage).append("</em>\r\n");
			if (curPage == 1) {
				sbOut.append("<a id=\"").append(id).append("pre\" href=\"javascript:void();\"  class=\"nosimpre\"></a>");
			}else{
				sbOut.append("<a id=\"").append(id).append("pre\" href=\"javascript:void();\"  class=\"simpre\"></a>");
			}
			if (curPage == totalPage) {
				sbOut.append("<a id=\"").append(id).append("next\" href=\"javascript:void();\"  class=\"nosimnext\"></a>");
			}else{
				sbOut.append("<a id=\"").append(id).append("next\" href=\"javascript:void();\"  class=\"simnext\"></a>");
			}
			sbOut.append("</div>");
		} else {
			sbOut.append("<div id=\"").append(id).append("\" class=\"page\" style=\"display:none;\">");
			sbOut.append("<span class=\"pagetotal\">共 <em id=\"").append(id).append("totalcount\" >").append(totalPage).append("</em> 条</span>");
			sbOut.append("<span class=\"pagenumber\">");
			if (curPage == 1) {
				sbOut.append("<a id=\"").append(id).append("first\" href=\"javascript:void();\" class=\"hui\">首页</a>");
				sbOut.append("<a id=\"").append(id).append("pre\" href=\"javascript:void();\" class=\"hui\">上一页</a>");
			} else {
				sbOut.append("<a id=\"").append(id).append("first\" href=\"javascript:void();\">首页</a>");
				sbOut.append("<a id=\"").append(id).append("pre\" href=\"javascript:void();\">上一页</a>");
			}
			if (curPage == totalPage) {
				sbOut.append("<a id=\"").append(id).append("next\" href=\"javascript:void();\" class=\"hui\" >下一页</a>");
				sbOut.append("<a id=\"").append(id).append("last\" href=\"javascript:void();\" class=\"hui\" >末页</a>");
			} else {
				sbOut.append("<a id=\"").append(id).append("next\" href=\"javascript:void();\" >下一页</a>\r\n");
				sbOut.append("<a id=\"").append(id).append("last\" href=\"javascript:void();\" >末页</a>\r\n");
			}
			sbOut.append("<input  id=\"").append(id).append("page\" name=\"").append(id).append("page\" type=\"text\" value=\"1\" class=\"inputnumer\">");
			sbOut.append("/<em id=\"").append(id).append("totalpage\" >").append(totalPage).append("</em>\r\n");
			sbOut.append("<input value=\"确定\" type=\"button\" id=\"").append(id).append("turn\" class=\"btn4\">");
			sbOut.append("</span>");
			sbOut.append("</div>");
		}
		JspWriter out = pageContext.getOut();
		try {
			out.print(sbOut.toString());
		} catch (IOException e) {
			Logger.WriteException(e);
		} finally {
			sbOut = null;
		}
		return SKIP_BODY;
	}

}
