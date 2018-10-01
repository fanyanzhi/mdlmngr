package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

public class NoticeInfoBean {
	private int id;
	private String noticeid;
	private String title;
	private String content;
	private int isPublic;// 公开级别
	private String time;
	private int type;// 通知类型

	


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNoticeid() {
		return noticeid;
	}

	public void setNoticeid(String noticeid) {
		this.noticeid = noticeid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public NoticeInfoBean() {

	}

	public NoticeInfoBean(Map<String, Object> mapData) {
		Field[] fileds = this.getClass().getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			try {
				Object objData = mapData.get(fileds[i].getName().toLowerCase());
				if (objData != null && String.valueOf(fileds).length() > 0) {
					fileds[i].set(this,
							Common.CovertToObject(mapData.get(fileds[i].getName().toLowerCase()), fileds[i].getType()));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
	}

}
