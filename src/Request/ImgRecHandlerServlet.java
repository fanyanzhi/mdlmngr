package Request;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import BLL.ImageMngr;
import BLL.Logger;
import Model.ImageInfoBean;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class ImgRecHandlerServlet
 */
@WebServlet("/ImgRecHandler.do")
public class ImgRecHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	final String[] AllowType = new String[] { "jpg", "jpeg", "gif", "png" };

	public ImgRecHandlerServlet() {
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
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("del".equals(request.getParameter("do"))) {
			strResult = delImage(request);
		} else {
			strResult = getImagePageHtml(request);
		}

		out.write(strResult);
		out.flush();
		out.close();
	}

	private String getImagePageHtml(HttpServletRequest request) {
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strModuleId = request.getParameter("mid");
		StringBuilder sbImageHtml = new StringBuilder();
		List<ImageInfoBean> lstInfo = ImageMngr.getimageList(appid, Integer.parseInt(strModuleId));
		if (lstInfo != null) {
			for (ImageInfoBean imageInfo : lstInfo) {
				sbImageHtml.append("<li id=\"").append(imageInfo.getId()).append("\">");
				sbImageHtml.append("<img src=\"ImgSrcHandler?").append(imageInfo.getId()).append("\" />");
				sbImageHtml.append("<p>");
				sbImageHtml.append("<span class=\"pictit\">").append(imageInfo.getName()).append(" <em>").append(imageInfo.getWidth()).append("*").append(imageInfo.getHeight()).append("</em></span><span class=\"picupdel\" onclick=\"delPic(").append(imageInfo.getId()).append(");\">删除</span>");
				sbImageHtml.append("</p></li>");
			}
		}
		return sbImageHtml.toString();
	}

	private String delImage(HttpServletRequest request) {
		String strImageId = request.getParameter("pid");
		if (ImageMngr.deleteImageInfo(Integer.parseInt(strImageId))) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("utf-8");
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String strModuleID = request.getParameter("moduleid");
		String strForeignID = request.getParameter("fid");
		int iCacheSize = 4096;
		int iMaxSize = 1048576;
		String strTempPath = Common.GetConfig("ImageUploadTempDir");
		if (Common.GetConfig("ImageUploadCacheSize") != null) {
			iCacheSize = Integer.valueOf(Common.GetConfig("ImageUploadCacheSize"));
		}
		if (Common.GetConfig("ImageUpLoadMaxSize") != null) {
			iMaxSize = Integer.valueOf(Common.GetConfig("ImageUpLoadMaxSize"));
		}
		File fTemp = new File(strTempPath);
		if (!fTemp.exists()) {
			fTemp.mkdirs();
		}
		DiskFileItemFactory dfif = new DiskFileItemFactory();
		dfif.setSizeThreshold(iCacheSize);
		dfif.setRepository(fTemp);

		ServletFileUpload sfu = new ServletFileUpload(dfif);
		sfu.setSizeMax(iMaxSize);

		PrintWriter out = response.getWriter();
		List<FileItem> lstFile = null;
		try {
			lstFile = sfu.parseRequest(request);
		} catch (Exception e) {
			if (e instanceof SizeLimitExceededException) {
				out.println("{message:'文件尺寸超过规定大小:" + 1024 * 1024 + "字节'}");
				return;
			}
			Logger.WriteException(e);
		} finally {
			dfif = null;
			sfu = null;
		}

		if (lstFile == null || lstFile.size() == 0) {
			out.println("{message:'请�?择上传文�?}");
			return;
		}

		Iterator<FileItem> itrFile = lstFile.iterator();
		FileItem fileItem = null;
		String strName = null;
		String strExtName = null;
		int iWidth = 0;
		int iHeight = 0;
		int iRet = -1;
		long lSize = 0;
		while (itrFile.hasNext()) {
			fileItem = (FileItem) itrFile.next();
			if (fileItem == null || fileItem.isFormField()) {
				continue;
			}

			strName = fileItem.getName();
			lSize = fileItem.getSize();
			if ("".equals(strName) || lSize == 0) {
				out.println("{message:'请�?择上传文�?}");
				return;
			}

			strName = strName.substring(strName.lastIndexOf("\\") + 1);
			strExtName = strName.substring(strName.lastIndexOf(".") + 1);

			BufferedImage img = javax.imageio.ImageIO.read(fileItem.getInputStream());
			iWidth = img.getWidth();
			iHeight = img.getHeight();

			int iAllowIndex = 0;
			int iAllowedExtCount = AllowType.length;
			for (; iAllowIndex < iAllowedExtCount; iAllowIndex++) {
				if (AllowType[iAllowIndex].equals(strExtName))
					break;
			}
			if (iAllowIndex == iAllowedExtCount) {
				String message = "";
				for (iAllowIndex = 0; iAllowIndex < iAllowedExtCount; iAllowIndex++) {
					message += "*." + AllowType[iAllowIndex] + " ";
				}
				out.println("{message:'请上传以下类型的文件" + message + "'}");
				return;
			}
			try {
				ImageInfoBean info = new ImageInfoBean();
				info.setName(strName);
				info.setExtName(strExtName);
				info.setModule(Integer.parseInt(strModuleID));
				info.setForeignID(strForeignID);
				info.setSize(lSize);
				info.setWidth(iWidth);
				info.setHeight(iHeight);
				info.setAppid(appid);
				byte[] arrinfo = new byte[(int) fileItem.getSize()];
				fileItem.getInputStream().read(arrinfo);
				info.setContent(arrinfo);
				iRet = ImageMngr.addImageInfo(info);
				response.setStatus(200);
				arrinfo = null;
				if ((1 == info.getModule())|| (3 == info.getModule())||(4 == info.getModule())) {
					out.println("{message:\"" + iRet + "\" }");
				} else {
					if (iRet > 0) {
						out.println("{message:\"文件上传成功.\" }");
					} else {
						out.println("{message:\"文件上传失败.\" }");
					}
				}

			} catch (Exception e) {
				Logger.WriteException(e);
			}finally{
				if(out !=null){
					out.flush();
					out.close();
				}
			}
		}
	}

}
