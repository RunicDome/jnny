package nc.ui.ct.ncweb;

import java.util.HashMap;
import java.util.Properties;

import nc.bs.framework.common.NCLocator;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.ToftPanel;
import nc.vo.pub.BusinessException;

/**
 * 界面基类
 * 
 * @author jieely
 * @date 2016-11-10
 * 
 */
public class AbstractorClientUI extends ToftPanel
{

	private static final long serialVersionUID = 1L;

	public static HashMap<String, String> mapAddress = null;// 节点对照关系

	public StringBuffer suffix = null;

	public AbstractorClientUI()
	{
		init();
	}

	public void init()
	{
		/**
		 * 获取配置文件信息
		 */
		try
		{
			if (mapAddress == null)
				mapAddress = this.getPropInfo();
		}
		catch (BusinessException e)
		{
			e.printStackTrace();
		}

		String usercode = ClientEnvironment.getInstance().getUser().getUser_code();
		suffix = new StringBuffer("");
		suffix.append("/login.aspx?action=LoginSubmit");// 固定格式
		suffix.append("&user=" + usercode);// 用户编码
	}

	/**
	 * 获取配置文件信息
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private HashMap<String, String> getPropInfo() throws BusinessException
	{

		mapAddress = new HashMap<String, String>();
		mapAddress.put("HMH1", "test");

		return mapAddress;
	}

	@Override
	public String getTitle()
	{
		return null;
	}

	@Override
	public void onButtonClicked(ButtonObject arg0)
	{

	}

	/**
	 * 根据节点号获取配置文件的地址
	 * 
	 * @return
	 */
	protected String getWebAddress()
	{
		String nodeCode = this.getTitle();
		String webAddress = mapAddress.get(nodeCode);
		return webAddress;
	}
}
