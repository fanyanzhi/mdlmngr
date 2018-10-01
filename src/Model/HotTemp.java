package Model;

public class HotTemp {
	private int id;
	private String typeid;
	private String fileid;
	private int factor;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public int getFactor() {
		return factor;
	}
	public void setFactor(int factor) {
		this.factor = factor;
	}
	@Override
	public String toString() {
		return "HotTemp [id=" + id + ", typeid=" + typeid + ", fileid=" + fileid + ", factor=" + factor + "]";
	}
	
}
