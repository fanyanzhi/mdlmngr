package Model;

import BLL.SysConfigMngr;

public class MobileRightStatus {
	private static boolean mRight = false; // 0表示关闭机构移动权限验证，1表示开始机构移动权限验证

	static {
		mRight = SysConfigMngr.getMobileRight() == 1;
	}

	/*
	 * 获取机构移动权限开关
	 */
	public static boolean getMobileRight() {
		return mRight;
	}

	/*
	 * 更新机构移动权限开关
	 */
	public static void updateMobileRight(String rightStatus) {
		if (rightStatus == null || rightStatus.length() == 0) {
			mRight = false;
		} else {
			try {
				mRight = Integer.parseInt(rightStatus) == 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
