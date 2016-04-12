package com.pingan.jinke.infra.padis.common;

public class Result<T> {
	/**
	 * 0 success 1 failure
	 */
	private boolean success;
	private String messages;
	private T result;

	public Result(boolean success) {
		this.success = success;
	}

	public Result() {
		this.success = Boolean.FALSE;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

}
