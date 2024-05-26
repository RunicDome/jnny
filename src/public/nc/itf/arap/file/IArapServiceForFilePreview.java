package nc.itf.arap.file;

import nc.vo.pub.BusinessException;

import com.alibaba.fastjson.JSONObject;

public interface IArapServiceForFilePreview {
	public JSONObject getYZViewUrl(JSONObject json) throws BusinessException;
}
