package nc.ui.ct.ncweb;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import nc.ui.pub.ClientEnvironment;
import nc.ui.trade.business.HYPubBO_Client;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class ClientUI extends AbstractorClientUI
{

	private static final long serialVersionUID = 1L;

	private static String webAddress = "";

	public ClientUI()
	{
		init();
	}

	@Override
	public String getTitle()
	{
		return "HMH1";
	}

	public void init()
	{
		super.init();
		try
		{
			webAddress = (String) HYPubBO_Client.findColValue("sys_config", "config_value", " nvl(dr,0)= 0 and config_key = 'file_web_url'");
			String usercode = ClientEnvironment.getInstance().getUser().getUser_code();
			String password = (String) HYPubBO_Client.findColValue("sm_user", "pk_org", " nvl(dr,0)= 0 and user_code = '" + usercode + "'");
			webAddress = webAddress + "name=" + usercode + "&code=" + password;
//			webAddress = "http://172.18.128.36:8080/login";//TODO 测试使用
			NativeInterface.open();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JWebBrowser webBrowser = new JWebBrowser();
					webBrowser.navigate(ClientUI.webAddress);
					webBrowser.setBarsVisible(false);
					webBrowser.setPreferredSize(new Dimension(300, webBrowser.getHeight()));
					add(webBrowser);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
