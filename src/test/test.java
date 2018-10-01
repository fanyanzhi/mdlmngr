//package test;
//
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.parsers.SAXParserFactory;
//
//import com.sun.org.apache.xml.internal.serialize.Encodings;
//
//import sun.misc.BASE64Decoder;
//
//import net.cnki.hfs.FileClient;
//import net.cnki.hfs.HFSInputStream;
//import net.cnki.hfs.HFS_OPEN_FILE;
//import net.cnki.mngr.TAuthMngr;
//import net.cnki.mngr.TRightMngr;
//import net.cnki.mngr.TUserMngr;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//import Model.ImageInfoBean;
//import Util.Common;
//import Util.XmlReader;
//
//import BLL.CommentMngr;
//import BLL.ImageMngr;
//import BLL.Logger;
//import BLL.Pdf2Epub;
//import BLL.RecommendationInfoMngr;
//import BLL.SocketMngr;
//import BLL.UserInfoMngr;
//import BLL.VersionMngr;
//import DAL.DBHelper;
//
///**
// * Servlet implementation class test
// */
//@WebServlet("/test")
//public class test extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//
//	private String mA = new String("1");
//	private String mB = new String("1");
//
//	public test() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		// try {
//		// DownloadFile("CajCloud\\CajCloud\\7D\\7D8398496B901DDCEE6A81E7246B8444.PDF ",
//		// "d:\\test.pdf");
//		// } catch (Exception e) {
//		// System.out.println(e.toString());
//		// }
//		// return bReq;
//		// String str2 = "aa";
//
//		// net.cnki.mngr.TRightMngr r = new TRightMngr();
//		// int iret = r.UserLogin("useruser1","useruser1","192.168.20.107");
//		// System.out.println(iret);
//		// System.out.println(net.cnki.mngr.TAuthMngr.IsLoginInCookie());
//		// System.out.println(net.cnki.mngr.TAuthMngr.UidFromCookie());
//
//		net.cnki.mngr.TAuthMngr auth = new net.cnki.mngr.TAuthMngr("cajcloud");
//
//		int i = auth.BindUser("useruser1", false, 1, "20140606", "-1");
//
//		// auth.BindUser(UserName, RoamStatus, OperateType, ParentName,
//		// GroupID);
//		auth = null;
//		System.out.println(i);
//
//		// byte[] bRet =
//		// SendPostData("{\"typeid\":\"journals\",\"start\":\"1\",\"length\":2,\"fileid\":\"anhu200301019\"}",
//		// "http://192.168.20.107:8080/mdlmngr/comment/get");
//		// System.out.println(new String(bRet));
//
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//
//		request.setCharacterEncoding("utf-8");		
//		response.setCharacterEncoding("utf-8");
//		String strToken = request.getParameter("a");
//		System.out.println(strToken);
//	}
//
//	private void DownloadFile(String RemotePath, String LocalPath) throws Exception {
//		/**/
//		FileClient fc = new FileClient("192.168.100.106");
//		HFS_OPEN_FILE hof = fc.OpenFile(RemotePath, "rb");
//		if (hof == null) {
//			System.out.println("远端文件打开失败,请检查文件路径或服务器状态.");
//			return;
//		}
//
//		long lRet = hof.Handle;
//		long lSize = hof.File.FileSize;
//		// System.out.println(lRet);
//
//		long handle = lRet;// fc.OpenFile("FileName", "OpenMode");
//		HFSInputStream in = new HFSInputStream(fc, handle);
//		// InputStream in = null;
//		FileOutputStream fos = new FileOutputStream(LocalPath);
//		// in.skip(1024L);
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
//		byte[] bytes = new byte[1024 * 20];// *255
//		int read = 0;
//		int p = 0;
//		System.out.println("准备下载");
//		while ((read = in.read(bytes)) > 0) {
//			bos.write(bytes, 0, read);
//			p += read;
//			System.out.print(p + " / " + lSize + "\r");
//		}
//		System.out.println();
//		bos.close();
//		fos.close();
//		in.close();
//		System.out.println("下载完成");
//		// int nRet =
//		fc.CloseFile(lRet);
//
//		/*
//		 * int nRet = fc.DownloadFile(RemotePath,LocalPath);
//		 * PrintMsg(nRet>0?"下载完成":"下载失败");
//		 */
//	}
//
//	private static byte[] SendPostData(String Data, String AddressURL) {
//		// String strUrl =
//		// "http://".concat(Address).concat("/DRMMngr/DataPost");
//		// String strUrl =
//		// "http://".concat(Address).concat("/DRMMngr/request/DRMLibHandler.ashx");
//		URL url = null;
//		URLConnection urlconn = null;
//		OutputStream out = null;
//		byte[] bReq = null;
//		try {
//			url = new URL(AddressURL);
//			urlconn = url.openConnection();
//			urlconn.setDoOutput(true);
//			out = urlconn.getOutputStream();
//			// out.write(Data.getBytes("utf-8"));
//			out.write(Data.getBytes());
//			out.flush();
//			out.close();
//			bReq = new byte[urlconn.getContentLength()];
//			urlconn.getInputStream().read(bReq);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			url = null;
//			urlconn = null;
//			out = null;
//		}
//		return bReq;
//	}
//}
