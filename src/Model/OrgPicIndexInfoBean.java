package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;
public class OrgPicIndexInfoBean {
	//PicIndexID,PicFormat,PicIndexNum,PicIndexTheme,PicSize,Resolution,SubjectName
	private String picindexid;
	private String picformat;
	private String picindexnum ;
	private String picindextheme;
	private String picsize;
	private String resolution;
	private String subjectname;
	public OrgPicIndexInfoBean(Map<String, Object> mapData) {
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
	public String getPicindexid() {
		return picindexid;
	}
	public void setPicindexid(String picindexid) {
		this.picindexid = picindexid;
	}
	public String getPicformat() {
		return picformat;
	}
	public void setPicformat(String picformat) {
		this.picformat = picformat;
	}
	public String getPicindexnum() {
		return picindexnum;
	}
	public void setPicindexnum(String picindexnum) {
		this.picindexnum = picindexnum;
	}
	public String getPicindextheme() {
		return picindextheme;
	}
	public void setPicindextheme(String picindextheme) {
		this.picindextheme = picindextheme;
	}
	public String getPicsize() {
		return picsize;
	}
	public void setPicsize(String picsize) {
		this.picsize = picsize;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}
}
