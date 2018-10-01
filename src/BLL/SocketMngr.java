package BLL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import Util.Common;

public class SocketMngr {
	public static boolean sendSocketDataNoWait(String ServerAddr, String Data) {
		int iTimeOut = 20000;
		String strTimeOut = Common.GetConfig("SocketTimeOut");
		if (!Common.IsNullOrEmpty(strTimeOut)) {
			iTimeOut = Integer.valueOf(strTimeOut);
		}
		return sendSocketDataNoWait(ServerAddr, Data, iTimeOut);
	}

	public static boolean sendSocketDataNoWait(String ServerAddr, String Data, int TimeOut) {
		boolean bolRet = true;
		String[] arrServer = ServerAddr.split(":");
		String strServer = arrServer[0];
		String strPort = arrServer[1];
		Socket client = null;
		PrintWriter out = null;
		try {
			client = new Socket();
			
			client.connect(new InetSocketAddress(strServer, Integer.valueOf(strPort)), TimeOut);
			out = new PrintWriter(client.getOutputStream());
			out.println(Data);
			out.flush();
		} catch (Exception e) {
			Logger.WriteException(e);
			bolRet = false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				client.close();
			} catch (Exception e) {
				Logger.WriteException(e);
				bolRet = false;
			}
		}
		return bolRet;
	}

	public static String sendSocketData(String ServerAddr, String Data) {
		int iTimeOut = 20000;
		String strTimeOut = Common.GetConfig("SocketTimeOut");
		if (!Common.IsNullOrEmpty(strTimeOut)) {
			iTimeOut = Integer.valueOf(strTimeOut);
		}
		return sendSocketData(ServerAddr, Data, iTimeOut);
	}

	public static String sendSocketData(String ServerAddr, String Data, int TimeOut) {
		String[] arrServer = ServerAddr.split(":");
		String strServer = arrServer[0];
		String strPort = arrServer[1];
		String strRet = null;
		Socket client = null;
		BufferedReader in = null;
		PrintWriter out = null;
		StringBuilder sbRet = new StringBuilder();
		try {
			client = new Socket();
			client.connect(new InetSocketAddress(strServer, Integer.valueOf(strPort)), TimeOut);

			// InputStreamReader in1 = new
			// InputStreamReader(client.getInputStream());

			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream());
			out.println(Data);
			out.flush();

			while (!Common.IsNullOrEmpty(strRet = in.readLine())) {
				sbRet.append(strRet).append("\r\n");
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				client.close();
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		return sbRet.toString();
	}
}
