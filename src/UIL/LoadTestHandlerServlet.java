package UIL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;

import BLL.CnkiMngr;

/**
 * Servlet implementation class LoadTestHandlerServlet
 */
@WebServlet("/LoadTestHandler")
public class LoadTestHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String[] users = {"zhu_zhu1","zhu_zhu18","cajtest","test1234567","test1234567891","test999","test888","test777","test666","test555","test444","test222","test111","ceshigeren5","ceshigeren2","ceshigeren3","testcnki1","testcnki2","testcnki3","testcnki4","testcnki5","testcnki6","testcnki7","testcnki8","testcnki9","testcnki10","testcnki11","testcnki12","testcnki13","testcnki14","testcnki15","testcnki16","testcnki17","testcnki18","testcnki19","testcnki20","testcnki21","testcnki22","testcnki23","testcnki24","testcnki25","testcnki26","testcnki27","testcnki28","testcnki29","testcnki30","testcnki31","testcnki32","testcnki33","testcnki34","testcnki35","testcnki36","testcnki37","testcnki38","testcnki39","testcnki40","testcnki41","testcnki42","testcnki43","testcnki44","testcnki45","testcnki46","testcnki47"}; 
	private static String[] password = {"zhu_zhu1","zhu_zhu18","cnki","test1234567","test1234567891","test999","test888","test777","test666","test555","test444","test222","test111","ceshigeren5","ceshigeren2","ceshigeren3","testcnki1","testcnki2","testcnki3","testcnki4","testcnki5","testcnki6","testcnki7","testcnki8","testcnki9","testcnki10","testcnki11","testcnki12","testcnki13","testcnki14","testcnki15","testcnki16","testcnki17","testcnki18","testcnki19","testcnki20","testcnki21","testcnki22","testcnki23","testcnki24","testcnki25","testcnki26","testcnki27","testcnki28","testcnki29","testcnki30","testcnki31","testcnki32","testcnki33","testcnki34","testcnki35","testcnki36","testcnki37","testcnki38","testcnki39","testcnki40","testcnki41","testcnki42","testcnki43","testcnki44","testcnki45","testcnki46","testcnki47"};
	private  static String typeid ="cjfd";
	private static  String[] files = {"XIXX201507048","XIXX201507050","XIXX201507051","XIXX201507052","XIXX201507053","XIXX201507054","XIXX201507056","XIXX201507070","XIXX201507069","XIXX201507068","XIXY201507032","JMSA201509027","JMSA201509046","JMSA201509052","SHNG201530151","SHNG201530176","DLXS201505015","DLXS201505017","HBHS201518033","","","","XIXX201507019","XIXX201507010","XIXX201507001","XIXX201507026","GLZJ201524242","GLZJ201524241","JMSA201509167","XIXX201507002","HBHS201518035","","","","XIXX201507020","LDWC201519032","KXXG201510001","LLYX201510015","KXXG201510018","HNSH201508017","CJJX201518277","CJJX201518267","QYDB201519066","CJJX201518152","CKYK201525001","CJJX201518201","CJJX201518301","NCCZ201510002","BZLD201510046","JJCK201536029","FZFZ201505014","NONG201509012","JRJZ201510017","JRJZ201510012","JRJZ201510004","QYDB201519107","CJMY201510054","CJMY201510046","CJMY201510042","CJMY201510043","CJMY201510034","CJJI201509025","LLYX201510030","ZZYZ201521003","ZZYZ201521006","ZZYZ201521007","ZZYZ201521009","ZZYZ201521011","ZZYZ201521013","ZZYZ201521014","ZZYZ201521016","ZZYZ201521018","ZZYZ201521020","ZZYZ201521024","ZZYZ201521025","ZZYZ201521033","ZZYZ201521034","FJSC201505014","GLZJ201527374","GYYX201507029","JNYZ201520082","KDYZ201510008","KDYZ201510004","QDHY201510008","SCDX201505034","SDYD201505017","SHSW201510005","SHSW201510011","SMKY201505005","SWCX201528004","SWCX201525013","SWCX201528015","SWCX201528052","SXXY201505029","SXXY201505031","SYYD201510003","SXYX201510004","SYYD201510015","SYYD201510010","WOOD201504011","XDZY201505038","XDZY201505038","XDZY201505049","XDZY201505050","XDZY201505055","XDZY201505059","XDZY201505060","XDZY201505061","XDZY201505062","XDZY201505065","XDZY201505066","XDZY201505068","XDZY201505069","YAOL201508004","YAOL201508011","YAOL201508025","YAOL201508031","YAOL201508034","YTCT201520016","YTCT201520073","ZGDX201510015","ZGSD201505014","ZGTR201510002","ZGTR201510003","ZGTR201510011","ZGYH201505014","DXTQ201502011","DZCM201502014","BGYW201508030","CBGJ201504033","CLSJ201503012","GBDJ201504001","DSZM201504001","DSZM201504005","GDXK201505027","GDXK201505014","GDXK201505009","DXTQ201502011","DZSQ201505033","DZKK201505042","GWCL201505027","GBXX201505024","DZCL201505021","DZCL201505024","CGQJ201506006","CYYT201506024","GWCL201505014","CGSJ201502018","CGSJ201502019","DZRU201508145","GPRJ201501013","GLZJ201512343","GBDJ201504030","GBDJ201504031","GBDJ201504063","CMEI201508005","CMEI201508015","GDXK201505008"};
	

	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoadTestHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("come here");
		CnkiMngr mngr = new CnkiMngr();
		String IP = "59.64.113.205";
		Random rd = new Random();
		int iUser=rd.nextInt(62);
		String userName = users[iUser];
		String pwd = password[iUser];
		System.out.println("dd");
		Holder<Integer> errorCode = new Holder<Integer>();
		
		
		
		if (!mngr.chkiUserLogin(userName, pwd, IP)) {
			writeLog(userName + " false, errorcode=" + errorCode.value);
			return;
		} else {
			writeLog(userName + " login success. ");
		}
		int iFile = rd.nextInt(157);
		String file= files[iFile];
		String[] errResult = null;
		if(!mngr.setFileInfo("journals", "CJFD", file, errResult)){
			writeLog(file + " set file false ");
			return;
		}
		String[] arrAuthRet = new String[6];
		int iAuthRet = mngr.getUserAuthority("CJFD", file, arrAuthRet);
		if (iAuthRet == -1) {
			if ("-5".equals(arrAuthRet[1])) {
				writeLog("{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}");
			} else {
				writeLog("{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + "}");
			}
		} else if (iAuthRet == 0) {	
				writeLog("{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}");
		
		}
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		if (mngr.getUserBalance(userName, showBalance, balance, ticket, errorCode)) {
			writeLog("{\"result\":true,\"userbalance\":\"" + balance.value + "\",\"userticket\":" + ticket.value + "}");
		} else {
			writeLog("{\"result\":false,\"message\":\"获取余额失败\",\"errorcode\":\"" + errorCode.value + "\"}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	/*public  static void main(String[] args){
		CnkiMngr mngr = new CnkiMngr();
		String IP = "59.64.113.205";
		Random rd = new Random();
		int iUser=rd.nextInt(62);
		String userName = users[iUser];
		String pwd = password[iUser];
		
		Holder<Integer> errorCode = new Holder<Integer>();
		Holder<String> identId = new Holder<String>();
		
		
		
		if (!mngr.chkiUserLogin(userName, pwd, IP)) {
			writeLog(userName + " false, errorcode=" + errorCode.value);
			return;
		} else {
			writeLog(userName + " login success. ");
		}
		int iFile = rd.nextInt(157);
		String file= files[iFile];
		String[] errResult = null;
		if(!mngr.setFileInfo("journals", "CJFD", file, errResult)){
			writeLog(file + " set file false ");
			return;
		}
		String[] arrAuthRet = new String[6];
		System.out.println("7");
		int iAuthRet = mngr.getUserAuthority("CJFD", file, arrAuthRet);
		if (iAuthRet == -1) {
			if ("-5".equals(arrAuthRet[1])) {
				writeLog("{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}");
			} else {
				writeLog("{\"result\":true,\"passed\":false,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + "}");
			}
		} else if (iAuthRet == 0) {	
				writeLog("{\"result\":true,\"passed\":true,\"message\":\"" + arrAuthRet[0] + "\",\"msgcode\":" + arrAuthRet[1] + ",\"userbalance\":" + arrAuthRet[2] + ",\"userticket\":" + arrAuthRet[3] + ",\"price\":" + arrAuthRet[4] + ",\"pagecount\":" + arrAuthRet[5] + "}");
		
		}
		Holder<Integer> showBalance = new Holder<Integer>();
		Holder<Double> balance = new Holder<Double>();
		Holder<Double> ticket = new Holder<Double>();
		if (mngr.getUserBalance(showBalance, balance, ticket, errorCode)) {
			writeLog("{\"result\":true,\"userbalance\":\"" + balance.value + "\",\"userticket\":" + ticket.value + "}");
		} else {
			writeLog("{\"result\":false,\"message\":\"获取余额失败\",\"errorcode\":\"" + errorCode.value + "\"}");
		}
	}*/
	
	private static void writeLog(String data){
		File file = new File("C:\\rightmngr.txt");
		try {
			if (!file.exists()) {
	            file.createNewFile();
	        }
			FileWriter sucsessFile = new FileWriter(file,true);	
			sucsessFile.write(data+"\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
	}

}
