package Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.ImageMngr;
import BLL.SysConfigMngr;
import Model.ImageInfoBean;
import Util.Common;

/**
 * Servlet implementation class ImageServlet
 */
@WebServlet("/image/*")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageServlet() {
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
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strAction = request.getPathInfo();
		if (strAction == null) {
			sendResponseData(response, "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}"));
			return;
		}

		byte[] arrReq = new byte[request.getContentLength()];
		DataInputStream dataInput = new DataInputStream(request.getInputStream());
		dataInput.readFully(arrReq);
		request.getInputStream().close();
		dataInput.close();

		String strReq = new String(arrReq, "utf-8");
		arrReq = null;
		String strRet;

		switch (strAction.replace("/", "").toLowerCase()) {
		case "getnotice":
			strRet = getInfo(request, strReq);
			break;
		default:
			strRet = "{\"result\":false,\"message\":\"".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACTION.code)).concat("}");
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

	@SuppressWarnings("unchecked")
	private String getInfo(HttpServletRequest request, String arg) {
		String appid = String.valueOf(request.getAttribute("app_id"));
		int iWidth;
		int iHeight;
		int iImgID;
		Map<String, String> mapTemp;
		JSONArray ja = JSONArray.fromObject(arg);

		JSONArray jsonArray = new JSONArray();
		if (ja.size() != 0) {
			for (Object object : ja.toArray()) {
				mapTemp = (Map<String, String>) object;
				iWidth = Integer.valueOf(String.valueOf(mapTemp.get("width")));
				iHeight = Integer.valueOf(String.valueOf(mapTemp.get("height")));
				iImgID = getImage(appid, iWidth, iHeight);

				if (iImgID > 0) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("imgsrc", Common.GetConfig("ImageSrcServer").concat("/ImgSrcHandler?").concat(String.valueOf(iImgID)));
					jsonObj.put("backcolor", "1888d0");
					jsonArray.add(jsonObj);
				}
			}
		}
		return "{\"result\":true,\"data\":" + jsonArray.toString() + "}";
	}

	private int getImage(String appid, int Width, int Height) {
		int iRet = -1;
		List<ImageInfoBean> lstImage = ImageMngr.getimageList(appid, 2, Width, Height);
		if (lstImage == null) {
			return iRet;
		}

		List<int[]> lstSize = new ArrayList<int[]>();
		int iWidth;
		int iHeight;
		int iImgID;
		int iCurSize;
		boolean bolHor = Width > Height ? true : false;
		boolean bolHorTemp = false;
		for (ImageInfoBean imageInfoBean : lstImage) {
			iImgID = imageInfoBean.getId();
			iWidth = imageInfoBean.getWidth();
			iHeight = imageInfoBean.getHeight();
			iCurSize = lstSize.size();
			bolHorTemp = iWidth > iHeight ? true : false;
			if (bolHor != bolHorTemp) {
				continue;
			}
			int[] arrSize = new int[] { iImgID, iWidth, iHeight };
			int[] arrTemp;
			for (int i = 0; i < iCurSize; i++) {
				arrTemp = lstSize.get(i);
				if (iWidth <= arrTemp[1] && iHeight <= arrTemp[2]) {
					lstSize.add(i, arrSize);
					break;
				}
			}
			if (!lstSize.contains(arrSize)) {
				lstSize.add(arrSize);
			}
		}
		lstImage = null;

		if (lstSize.isEmpty()) {
			return iRet;
		}

		int[] arrMinSize = lstSize.get(0);
		iRet = arrMinSize[0];
		if (Width == arrMinSize[1] && Height == arrMinSize[2]) {
			return iRet;
		}

		for (int[] arrTemp : lstSize) {
			if (((float) Width / Height) == ((float) arrTemp[1] / arrTemp[2])) {
				iRet = arrTemp[0];
				break;
			}
		}

		return iRet;
	}
}
