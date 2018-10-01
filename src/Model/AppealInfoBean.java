package Model;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import BLL.Logger;
import Util.Common;
import net.sf.json.JSONObject;

public class AppealInfoBean {
	private int id;
	private String username;
	private String expcode;
	private String realname;
	private String workunit;
	private String phone;
	private String email;
	private String cause;
	private Date updatetime;
	private Date time;
	private int status;
	private String cardnum;

	private String front;
	private String back;
	private String remark;
	private String process;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getExpcode() {
		return expcode;
	}

	public void setExpcode(String expcode) {
		this.expcode = expcode;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getWorkunit() {
		return workunit;
	}

	public void setWorkunit(String workunit) {
		this.workunit = workunit;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public static AppealInfoBean getAppealInfoBean(String json) {
		JSONObject object = JSONObject.fromObject(json);
		return (AppealInfoBean) JSONObject.toBean(object, AppealInfoBean.class);
	}

	public AppealInfoBean() {

	}

	public AppealInfoBean(Map<String, Object> mapData) {
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
