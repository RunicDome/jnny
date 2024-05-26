package nc.ui.ct.ncweb;

import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.ToftPanel;
import nc.ui.trade.business.HYPubBO_Client;

public class ClientUINew extends ToftPanel
{

	private static final long serialVersionUID = 1L;

	public ClientUINew()
	{
//		init();
	}
	
	@SuppressWarnings({ "restriction", "deprecation" })
	public void init()
	{
		if (java.awt.Desktop.isDesktopSupported()) {
			try {
				String webAddress = (String) HYPubBO_Client.findColValue("sys_config", "config_value", " nvl(dr,0)= 0 and config_key = 'file_web_url'");
				String usercode = ClientEnvironment.getInstance().getUser().getUser_code();
				String password = (String) HYPubBO_Client.findColValue("sm_user", "pk_org", " nvl(dr,0)= 0 and user_code = '" + usercode + "'");
				webAddress = webAddress + "name=" + usercode + "&code=" + password;
				// 创建一个URI实例
				java.net.URI uri = java.net.URI.create(webAddress);
				// 获取当前系统桌面扩展
				java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				// 判断系统桌面是否支持要执行的功能
				if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
					// 获取系统默认浏览器打开链接
					dp.browse(uri);
				}
				this.setVisible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onButtonClicked(ButtonObject arg0) {
		System.out.println("test");
	}

}
