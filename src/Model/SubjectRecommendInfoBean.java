package Model;

import java.util.Date;

public class SubjectRecommendInfoBean {
	private String title;
	private String keyword;
	private String type;
	private int istop;
	private int linktype;
	private String summary;
	private int simageid;
	private int bimageid;
	private int isrecomd;
	private int isadv;
	public int getIsadv() {
		return isadv;
	}

	public void setIsadv(int isadv) {
		this.isadv = isadv;
	}


	private String subjectid;
	private int openclass;
	
	public int getOpenclass() {
		return openclass;
	}

	public void setOpenclass(int openclass) {
		this.openclass = openclass;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public int getIsrecomd() {
		return isrecomd;
	}


	public void setIsrecomd(int isrecomd) {
		this.isrecomd = isrecomd;
	}


	private Date time;
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getIstop() {
		return istop;
	}


	public void setIstop(int istop) {
		this.istop = istop;
	}

	public int getLinktype(){
		return linktype;
	}
	
	public void setLinktype(int linktype){
		this.linktype = linktype;
	}

	public String getSummary() {
		return summary;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public int getSimageid() {
		return simageid;
	}


	public void setSimageid(int simageid) {
		this.simageid = simageid;
	}


	public int getBimageid() {
		return bimageid;
	}


	public void setBimageid(int bimageid) {
		this.bimageid = bimageid;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}



}
