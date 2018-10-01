package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.BehaviourMngr;
import BLL.Logger;
import BLL.SysConfigMngr;
import BLL.UploadMngr;
import BLL.UserInfoMngr;
import Model.UploadInfoBean;
import Util.Common;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/upload/*")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      http://localhost:8080/mdlmngr/upload/add?usertoken=c&filename
	 *      =testfilename
	 *      &username=testusername&typename=testtypename&length=filesize
	 *      &range=0-100
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		//Cookie[] c =request.getCookies();
		String strToken = request.getParameter("usertoken");
		String strUserName = UserInfoMngr.UserLogin(strToken);
		if (strUserName.startsWith("@-")) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(strUserName.substring(1)).concat("\",\"errorcode\":").concat(strUserName.substring(1)).concat("}"));
			return;
		}
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		String strRet;
		switch (strAction.replace("/", "").toLowerCase()) {
		case "add":
			strRet = addInfo(strUserName, request, response);
			break;
		case "delete":
			strRet = deleteInfo(strUserName, request);
			break;
		case "getid":
			strRet = getFileID(strUserName, request);
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

	private String addInfo(String UserName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strFileID = request.getParameter("fileid");
		String strCrc = request.getParameter("crc");
		if (Common.IsNullOrEmpty(strFileID) || Common.IsNullOrEmpty(strCrc)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strRange = request.getParameter("range");

		UploadInfoBean uploadinfo = new UploadInfoBean();
		uploadinfo.setFileID(strFileID);
		uploadinfo.setUserName(UserName);
		//uploadinfo.setRange(strRange);
		uploadinfo.setClient(request.getHeader("User-Agent"));
		uploadinfo.setAddress(Common.getClientIP(request));

		byte[] arrFileContent = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrFileContent);
		request.getInputStream().close();
		dataInput.close();

		// crc校验错误
		if (!strCrc.equalsIgnoreCase(Common.getCRC32(arrFileContent))) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_INTEGRITY.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_INTEGRITY.code)).concat("}");
		}

		String strRet;
		long lUploadedLength = 0;
		lUploadedLength = UploadMngr.personalUpload(uploadinfo, strRange, arrFileContent);
		if (lUploadedLength > 0) {
			strRet = "{\"result\":true,\"uploadedlength\":\"".concat(String.valueOf(lUploadedLength)).concat("\"}");
		} else {
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(lUploadedLength)).concat("\"}");
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(lUploadedLength)).concat("\",\"errorcode\":").concat(String.valueOf(lUploadedLength)).concat("}");
			
		}
		return strRet;
	}

	private String deleteInfo(String UserName, HttpServletRequest request) {
		String strFileID = request.getParameter("fileid");
		if (Common.IsNullOrEmpty(strFileID)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}
		String strRet;
		if (UploadMngr.deleteFile(strFileID, UserName)) {
			strRet = "{\"result\":true}";
		} else {
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEUPLOADINFO.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DELETEUPLOADINFO.code)).concat("}");
		}
		return strRet;
	}

	private String getFileID(String UserName, HttpServletRequest request) {
		String strFileName = null;
		try {
			strFileName = URLDecoder.decode(request.getParameter("filename"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.WriteException(e);
		}
		String strFileMD5 = request.getParameter("md5");
		String strFileType = request.getParameter("filetype");
		String strFileSize = request.getParameter("length");
		String strAgent = request.getHeader("User-Agent");
		String strClientAddr = Common.getClientIP(request);

		if (Common.IsNullOrEmpty(strFileName) || Common.IsNullOrEmpty(strFileType) || Common.IsNullOrEmpty(strFileSize) || Common.IsNullOrEmpty(strFileMD5)) {
			return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
		}

		
		// if (UploadMngr.isExist(UserName, strFileName)) {
		// return
		// "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILEHASEXIST.code)).concat("\"}");
		// }
		
		UploadInfoBean FileInfo = new UploadInfoBean();
		FileInfo.setFileName(strFileName);
		FileInfo.setTypeName(strFileType);
		FileInfo.setFileLength(Long.parseLong(strFileSize));
		FileInfo.setUserName(UserName);
		FileInfo.setTypeid("cajcloud");
		FileInfo.setFileMd5(strFileMD5);
		FileInfo.setClient(strAgent);
		FileInfo.setAddress(strClientAddr);

		String strFileID = Common.EnCodeMD5(UserName.concat(strFileName).concat(UUID.randomUUID().toString()).toLowerCase());
		FileInfo.setFileID(strFileID);
		Map<String, Object> mapExistFile = UploadMngr.getExistMd5(strFileMD5);
		if (mapExistFile.get("vfileid") == null) {
			FileInfo.setFileTable(UploadMngr.getUploadTable(UserName));
			FileInfo.setDskFileName(strFileID.concat(".").concat(strFileType));
			if (UploadMngr.addUploadInfo(FileInfo)) {
				return "{\"result\":true,\"fileid\":\"".concat(strFileID).concat("\"}");
			} else {
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
			}
		} else {
			FileInfo.setFileTable(String.valueOf(mapExistFile.get("vfiletable")));
			FileInfo.setDskFileName(String.valueOf(mapExistFile.get("vfileid")));
			FileInfo.setIsCompleted(1);
			FileInfo.setRange("0-".concat(String.valueOf(Long.parseLong(strFileSize)-1)));
			if (UploadMngr.addUploadInfo(FileInfo)) {
				if("pdf".equalsIgnoreCase(strFileType)){
					UploadMngr.pdf2Epub("cajcloud", strFileID, UserName,"");
				}
				BehaviourMngr.addUploadInfo(UserName, strFileName, strFileType);
				return "{\"result\":true,\"fileid\":\"".concat(strFileID).concat("\",\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILEHASEXIST.code))).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_FILEHASEXIST.code)).concat("}");
			} else {
				return "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PARAMETERS.code)).concat("}");
			}
		}

	}
}
