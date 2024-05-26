package com.yonyou.iuap.system.utils;

import java.io.Serializable;

/**
 * 消息返回
 * 
 * @author xuwjl@yonyou.com
 * 
 *         2018-6-24
 */
public class MessageResult implements Serializable {

	private static final long serialVersionUID = -8268805442839696330L;

	public static final int STATUS_SUCCESS = 200; // 成功状态码

	public static final int STATUS_ERROR = 300; // 错误状态码

	private Object data; // 放返回数据

	private String message; // 放错误信息

	private int statusCode; // 放状态码

	public MessageResult(int statusCode, String message, Object data) {
		this.setStatusCode(statusCode);
		this.setMessage(message);
		this.setData(data);
	}

	public MessageResult() {
		this(STATUS_SUCCESS, "操作成功");
	}

	public MessageResult(int statusCode, String message) {
		this(statusCode, message, null);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "MessageResult [data=" + data + ", message=" + message + ", statusCode=" + statusCode + "]";
	}
}
