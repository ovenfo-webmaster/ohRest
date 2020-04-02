package ohSolutions.ohRest.util.bean;

public class FileBean {
	
	private String url;
	private String name;
	private String source;
	private boolean isRewritable;
	
	public boolean isRewritable() {
		return isRewritable;
	}
	public void setRewritable(boolean isRewritable) {
		this.isRewritable = isRewritable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}