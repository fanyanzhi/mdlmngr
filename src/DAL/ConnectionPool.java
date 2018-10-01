package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

class ConnectionModel {
	private Connection DBConnection;
	private boolean IsLock;
	private Date CreateTime;

	public boolean getIsLock() {
		return IsLock;
	}

	public void setIsLock(boolean isLock) {
		IsLock = isLock;
	}

	public Connection getDBConnection() {
		return DBConnection;
	}

	public Date getCreateTime() {
		return CreateTime;
	}

	public ConnectionModel(Connection dbConnection, boolean isLock, Date createTime) {
		DBConnection = dbConnection;
		IsLock = isLock;
		CreateTime = createTime;
	}
}

public class ConnectionPool {
	private static Map<String, List<ConnectionModel>> ConnMap;
	private static Map<String, Integer> CurIndexMap;
	//private static int TestIndex = 0;
	static {
		if (ConnMap == null) {
			ConnMap = new HashMap<String, List<ConnectionModel>>();
			CurIndexMap = new HashMap<String, Integer>();
		}
	}

	private static List<ConnectionModel> GetConnPool(String Key) {
		List<ConnectionModel> lstConn;
		if (ConnMap.containsKey(Key)) {
			lstConn = ConnMap.get(Key);
		} else {
			lstConn = new ArrayList<ConnectionModel>();
			ConnMap.put(Key, lstConn);
			CurIndexMap.put(Key, -1);
		}
		return lstConn;
	}

	public static ConnectionModel GetConnection(String Drive, String Url, String UserName, String PassWord, int MaxConnections, boolean IsLock) throws ClassNotFoundException, SQLException {
		String strKey = Drive.concat(Url).toLowerCase();
		List<ConnectionModel> lstConn = GetConnPool(strKey);
		if (lstConn.size() < MaxConnections) {
			return CreateNewConnection(Drive, Url, UserName, PassWord, IsLock);
		}
		int iThreadID = (int) java.lang.Thread.currentThread().getId();
		int iCurConnIndex =0;
		synchronized(CurIndexMap){
		CurIndexMap.put(strKey, CurIndexMap.get(strKey) + 1);
		 iCurConnIndex = CurIndexMap.get(strKey);
		}
		iCurConnIndex = iCurConnIndex % MaxConnections;
		ConnectionModel connModel = lstConn.get(iCurConnIndex);
		Connection conn = connModel.getDBConnection();
		boolean bolIsLock = connModel.getIsLock();

//		if (TestIndex == iCurConnIndex) {
//			System.err.println(TestIndex);
//		}
		
		if (bolIsLock) {
			while (true) {
				iCurConnIndex = (iCurConnIndex + iThreadID) % MaxConnections;
				System.err.println(iCurConnIndex);
				
				connModel = lstConn.get(iCurConnIndex);
				bolIsLock = connModel.getIsLock();
				if (!bolIsLock) {
					conn = connModel.getDBConnection();
					break;
				}
			}
		}
		//TestIndex = iCurConnIndex;
		if (!IsValid(conn)) {
			lstConn.remove(iCurConnIndex);
			return CreateNewConnection(Drive, Url, UserName, PassWord, IsLock);
		}
		// if (conn.isClosed()) {
		// lstConn.remove(iCurConnIndex);
		// return CreateNewConnection(Drive, Url, UserName, PassWord, IsLock);
		// }
		// Statement pingStatement = null;
		// try {
		// pingStatement = (Statement) conn.createStatement();
		// pingStatement.executeQuery("SELECT 1");
		// } catch (Exception e) {
		// lstConn.remove(iCurConnIndex);
		// return CreateNewConnection(Drive, Url, UserName, PassWord, IsLock);
		// } finally {
		// if (pingStatement != null) {
		// try {
		// pingStatement.close();
		// } catch (SQLException ex) {
		// throw ex;
		// }
		// }
		// }

		// CurIndexMap.put(strKey, iCurConnIndex);
		connModel.setIsLock(IsLock);
		return connModel;
	}

	private static boolean IsValid(Connection conn) throws SQLException {
		if (conn.isClosed()) {
			return false;
		}
		Statement pingStatement = null;
		try {
			pingStatement = (Statement) conn.createStatement();
			pingStatement.executeQuery("SELECT 1");
		} catch (Exception e) {
			return false;
		} finally {
			if (pingStatement != null) {
				pingStatement.close();
			}
		}
		return true;
	}

	private static ConnectionModel CreateNewConnection(String Drive, String Url, String UserName, String PassWord, boolean IsLock) throws ClassNotFoundException, SQLException {
		String strKey = Drive.concat(Url).toLowerCase();
		List<ConnectionModel> lstConn = GetConnPool(strKey);
		Class.forName(Drive);
		Connection conn;
		conn = DriverManager.getConnection(Url, UserName, PassWord);
		ConnectionModel connModel = new ConnectionModel(conn, IsLock, new Date());
		lstConn.add(connModel);
		return connModel;

	}
}
