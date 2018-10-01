package SysMngr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.ParseException;

import BLL.AppInfoMngr;
import BLL.JdpushMngr;
import BLL.KuaiBaoMngr;
import BLL.UserInfoMngr;
import Util.Common;
import Util.HuaWeiPush;
import Util.UmengSender;
import Util.XiaoMiPush;

/**
 * Servlet implementation class PushTestServlet
 */
@WebServlet("/PushTest.do")
public class PushTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PushTestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		request.getRequestDispatcher("/SysMngr/pushtest.jsp").forward(request, response);
	}

	/**
	 * 
	 * 
	 * 
	 * 1.type=1 为推送关注的快报信息更新信息，跳转到我的图书馆，其他参数：code为对应的行业分类代码
2.type=2 为推荐的单篇信息，跳转到文献详情页，其他参数：odatatype为文献类别，fileid为文献id
3.type=3 为推送的整刊信息，跳转到整刊详情页，其他参数：titlepy 为拼音刊名
4.type=4 为文本类型，跳转到消息详情页，其他参数：id为消息id
5.type=5 为链接，直接打开链接，其他参数：url为链接地址
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		String username = request.getParameter("txtuser");
		String devicetype = request.getParameter("device");
		String server = request.getParameter("server");
		String content = request.getParameter("txtComment");
		String pushtype = request.getParameter("pushtype");
		
		
		List<Map<String, Object>> lstUserDevice = UserInfoMngr.getUserDevice(username);
		if(lstUserDevice==null||lstUserDevice.size()==0){
			request.setAttribute("errmsg", "不存在设备信息");
			request.getRequestDispatcher("/SysMngr/pushtest.jsp").forward(request, response);
		}
		List<String> iosDev = new ArrayList<String>();
		List<String> huaweiDev = new ArrayList<String>();
		List<String> xiaomiDev = new ArrayList<String>();
		List<String> otherDev = new ArrayList<String>();
		Iterator<Map<String,Object>> it = lstUserDevice.iterator();
		Map<String, Object> map = null;
		while(it.hasNext()){
			map = it.next();
			if("iphone".equals(map.get("manu").toString())){
				iosDev.add(map.get("devicetoken").toString());
			}else if("huawei".equals(map.get("manu").toString())){
				huaweiDev.add(map.get("devicetoken").toString());
			}else if("xiaomi".equals(map.get("manu").toString())){
				xiaomiDev.add(map.get("devicetoken").toString());
			}else if("other".equals(map.get("manu").toString())){
				otherDev.add(map.get("devicetoken").toString());
			}
		}
		Map<String, String> parammap = new HashMap<String, String>();
		parammap.put("fromuser", "admin");
		parammap.put("id", "1");
		parammap.put("type", "text");
		parammap.put("time", Common.GetDateTime());
		if(Common.IsNullOrEmpty(devicetype)){
			JdpushMngr.iosPushKuaiBao(iosDev, content, "6", parammap);
			HuaWeiPush.pushKuaiBaoMsg("全球学术快报", content, huaweiDev, "6", parammap);
			try {
				XiaoMiPush.sendKuaiBaoMessage("全球学术快报", content, "", xiaomiDev, "6", parammap);
				UmengSender.sendKuaiBaoMessage("全球学术快报", "11", content, otherDev, "6", parammap);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("iphone".equals(devicetype)){
			JdpushMngr.iosPushKuaiBao(iosDev, content, "6", parammap);
		}else if("huawei".equals(devicetype)){
			HuaWeiPush.pushKuaiBaoMsg("全球学术快报", content, huaweiDev, "6", parammap);
		}else if("xiaomi".equals(devicetype)){
			try {
				XiaoMiPush.sendKuaiBaoMessage("全球学术快报", content, "", xiaomiDev, "6", parammap);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("other".equals(devicetype)){
			try {
				UmengSender.sendKuaiBaoMessage("全球学术快报", "11", content, otherDev, "6", parammap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		request.setAttribute("errmsg", "发送完成");
		request.getRequestDispatcher("/SysMngr/pushtest.jsp").forward(request, response);
	}

}
