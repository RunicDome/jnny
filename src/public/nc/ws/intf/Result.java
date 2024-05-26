package nc.ws.intf;

import java.util.HashMap;

import net.sf.json.JSONObject;

/**
 * 操作消息提醒
 * 
 * @author zwh
 */
public class Result extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/** 状态码 */
	public static final String CODE_TAG = "code";

	/** 返回内容 */
	public static final String MSG_TAG = "msg";

	/** 数据对象 */
	public static final String DATA_TAG = "data";

	/**
	 * 状态类型
	 */
	public enum Type {
		/** 成功 */
		SUCCESS(0),
		/** 警告 */
		WARN(301),
		/** 错误 */
		ERROR(500);
		private final int value;

		Type(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	/**
	 * 初始化一个新创建的 Result 对象，使其表示一个空消息。
	 */
	public Result() {
	}

	/**
	 * 初始化一个新创建的 Result 对象
	 * 
	 * @param type
	 *            状态类型
	 * @param msg
	 *            返回内容
	 */
	public Result(Type type, String msg) {
		super.put(CODE_TAG, type.value);
		super.put(MSG_TAG, msg);
	}

	/**
	 * 初始化一个新创建的 Result 对象
	 * 
	 * @param type
	 *            状态类型
	 * @param msg
	 *            返回内容
	 * @param data
	 *            数据对象
	 */
	public Result(Type type, String msg, Object data) {
		super.put(CODE_TAG, type.value);
		super.put(MSG_TAG, msg);
		if (null != data) {
			super.put(DATA_TAG, data);
		}
	}

	/**
	 * 返回成功消息
	 * 
	 * @return 成功消息
	 */
	public static String success() {
		return Result.success("操作成功");
	}

	/**
	 * 返回成功数据
	 * 
	 * @return 成功消息
	 */
	public static String success(Object data) {
		return Result.success("操作成功", data);
	}

	/**
	 * 返回成功消息
	 * 
	 * @param msg
	 *            返回内容
	 * @return 成功消息
	 */
	public static String success(String msg) {
		return Result.success(msg, null);
	}

	/**
	 * 返回成功消息
	 * 
	 * @param msg
	 *            返回内容
	 * @param data
	 *            数据对象
	 * @return 成功消息
	 */
	public static String success(String msg, Object data) {
		return new Result(Type.SUCCESS, msg, data).Object2Json();
	}

	/**
	 * 返回警告消息
	 * 
	 * @param msg
	 *            返回内容
	 * @return 警告消息
	 */
	public static Result warn(String msg) {
		return Result.warn(msg, null);
	}

	/**
	 * 返回警告消息
	 * 
	 * @param msg
	 *            返回内容
	 * @param data
	 *            数据对象
	 * @return 警告消息
	 */
	public static Result warn(String msg, Object data) {
		return new Result(Type.WARN, msg, data);
	}

	/**
	 * 返回错误消息
	 * 
	 * @return
	 */
	public static String error() {
		return Result.error("操作失败");
	}

	/**
	 * 返回错误消息
	 * 
	 * @param msg
	 *            返回内容
	 * @return 警告消息
	 */
	public static String error(String msg) {
		return Result.error(msg, null);
	}

	/**
	 * 返回错误消息
	 * 
	 * @param msg
	 *            返回内容
	 * @param data
	 *            数据对象
	 * @return 警告消息
	 */
	public static String error(String msg, Object data) {
		return new Result(Type.ERROR, msg, data).Object2Json();
	}

	public String Object2Json() {
		JSONObject json = JSONObject.fromObject(this);// 将java对象转换为json对象
		String str = json.toString();// 将json对象转换为字符串
		return str;
	}
}
