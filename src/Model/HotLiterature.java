package Model;

public class HotLiterature {

	private String fileid;
	private String typeid;
	private String filename;
	private String author;
	private String date;
	private String year;
	private String issue;
	private String code;
	private String source;
	private String sourcech;
	private float factor;
	private String time;
	
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourcech() {
		return sourcech;
	}
	public void setSourcech(String sourcech) {
		this.sourcech = sourcech;
	}
	public float getFactor() {
		return factor;
	}
	public void setFactor(float factor) {
		this.factor = factor;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "HotLiterature [fileid=" + fileid + ", typeid=" + typeid + ", filename=" + filename + ", author="
				+ author + ", date=" + date + ", year=" + year + ", issue=" + issue + ", code=" + code + ", source="
				+ source + ", sourcech=" + sourcech + ", factor=" + factor + ", time=" + time + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileid == null) ? 0 : fileid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HotLiterature other = (HotLiterature) obj;
		if (fileid == null) {
			if (other.fileid != null)
				return false;
		} else if (!fileid.equals(other.fileid))
			return false;
		return true;
	}
	
}
