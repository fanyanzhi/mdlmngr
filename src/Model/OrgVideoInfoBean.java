package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;
public class OrgVideoInfoBean {
	private String columnname;
	private String showname;
	private String broadcastdate ;
	private String duration;
	private String director;
	private String filesize;
	private String showinfo;
	public OrgVideoInfoBean(Map<String, Object> mapData) {
		Field[] fileds = this.getClass().getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			try {
				Object objData=mapData.get(fileds[i].getName());
				if(objData!=null&&String.valueOf(fileds).length()>0){
					fileds[i].set(this, Common.CovertToObject(mapData.get(fileds[i].getName()), fileds[i].getType()));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		
	}
	public String getColumnname() {
		return columnname;
	}
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public String getBroadcastdate() {
		return broadcastdate;
	}
	public void setBroadcastdate(String broadcastdate) {
		this.broadcastdate = broadcastdate;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}
	public String getShowinfo() {
		return showinfo;
	}
	public void setShowinfo(String showinfo) {
		this.showinfo = showinfo;
	}
}
