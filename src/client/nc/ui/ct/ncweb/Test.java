package nc.ui.ct.ncweb;

public class Test {
	public static void main(String args[]) {
		if (java.awt.Desktop.isDesktopSupported()) {
			try {
				// 创建一个URI实例
				java.net.URI uri = java.net.URI.create("http://www.cnblogs.com/lsgwr/");
				// 获取当前系统桌面扩展
				java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				// 判断系统桌面是否支持要执行的功能
				if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
					// 获取系统默认浏览器打开链接
					dp.browse(uri);
				}
 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
