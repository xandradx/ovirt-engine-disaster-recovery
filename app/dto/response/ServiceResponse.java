package dto.response;

import com.google.gson.Gson;

public class ServiceResponse<T> {
	
	private boolean success;
	private String userMessage;
	private String errorMessage;
	private String sessionId;
	
	private T data;
	
	public static <T> ServiceResponse<T> success(String userMessage) {
		return ServiceResponse.successToken(userMessage, null, null);
	}
	
	public static <T> ServiceResponse<T> success(T data) {
		return ServiceResponse.successToken(null, data, null);
	}
	
	public static <T> ServiceResponse<T> success(String userMessage, T data) {
		return ServiceResponse.successToken(userMessage, data, null);
	}
	
	public static <T> ServiceResponse<T> successToken(String userMessage, T data, String token) {
		ServiceResponse<T> response = new ServiceResponse<T>();
	    response.setSuccess(true);
	    response.setData(data);
	    response.setUserMessage(userMessage);
	    response.setSessionId(token);
	    return response;
	}
	
	public static <T> ServiceResponse<T> error(String errorMessage) {
	    return error(errorMessage, null);
	}
	
	public static <T> ServiceResponse<T> error(String errorMessage, String sessionId) {
		ServiceResponse<T> response = new ServiceResponse<T>();
	    response.setSuccess(false);
	    response.setErrorMessage(errorMessage);
	    response.setSessionId(sessionId);
	    return response;
	}
	
	protected ServiceResponse() {
		this.success = false;
	}
	
	protected ServiceResponse(String sessionId) {
		this.success = true;
		this.sessionId = sessionId;
	}
	
	protected ServiceResponse(String sessionId, T data) {
		this.success = true;
		this.sessionId = sessionId;
		this.data = data;
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
