package Request;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import BLL.Logger;
import BLL.OrgImageMngr;
import Model.OrgImageBean;
import Model.UserLoginBean;
import Util.Common;
@WebServlet("/OrgImageHandler.do")
public class OrgImageHandlerServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	final String[] AllowType = new String[] { "jpg", "jpeg", "gif", "png" };


	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("utf-8");
		String appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		String id=request.getParameter("id");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String type=request.getParameter("type");
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
			out.println("{message:'请选择上传文件'}");
			return;
		}

		Iterator<FileItem> itrFile = lstFile.iterator();
		FileItem fileItem = null;
		String strName = null;
		String strExtName = null;
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
				out.println("{message:'请选择上传文件'}");
				return;
			}

			strName = strName.substring(strName.lastIndexOf("\\") + 1);
			strExtName = strName.substring(strName.lastIndexOf(".") + 1);

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
				OrgImageBean info = new OrgImageBean();
				info.setAppid(appid);
				info.setContent(content);
				info.setTitle(title);
				info.setTime(new Date());
				byte[] arrinfo = new byte[(int) fileItem.getSize()];
				fileItem.getInputStream().read(arrinfo);
				info.setActive(arrinfo);
				info.setType(Integer.parseInt(type));
				if(id!=null&&!"".equals(id)){
					info.setId(Integer.parseInt(id));
					iRet =OrgImageMngr.updateImageInfo(info);
				}else{
					iRet = OrgImageMngr.addImageInfo(info);
				}
				response.setStatus(200);
				arrinfo = null;
				if (iRet > 0) {
					out.println("{message:\"文件上传成功.\",id:"+iRet+" }");
				} else {
					out.println("{message:\"文件上传失败.\" }");
				}

			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}

	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("utf-8");
		String id = (String)request.getParameter("id");
		if(id!=null&&!"".equals(id)){
			List<Map<String, Object>> list =OrgImageMngr.getActiveById(Integer.parseInt(id));
			String appid = (String) list.get(0).get("appid");
			PrintWriter out = response.getWriter();
			out.write(appid);
			out.flush();
			out.close();
		}
	}
}
