package ohSolutions.ohRest.util.bean;

public class Response {
	
	private boolean isCorrect;
	private Object result;
	private String message;
	
	public boolean isCorrect() {
		return isCorrect;
	}
	public void isCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}