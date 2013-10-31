package function.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import function.util.SetUtil;

public class Proxy {

	/**
	 * @param args
	 */
	private String proxyList = getClass()
			.getResource("/resources/proxyList.txt").getPath()
			.replace("%20", " ");

	public String getProxyList() {
		return proxyList;
	}

	public void setProxyList(String proxyList) {
		this.proxyList = proxyList;
	}
	
	/**
	 * 设置随机代理
	 */
	public void setLocalRandomHttpProxy(){
		Vector<String> vProxy=SetUtil.readSetFromFile(getProxyList());
		int i=(int)(997*Math.random());
		String record[]=vProxy.get(i).split(":");
		setLocalHttpProxy(record[0],record[1]);
	}

	/**
	 * 设置http代理
	 */
	public void setLocalHttpProxy(String host, String port) {
		Properties prop = System.getProperties();
		System.out.println("设置HTTP代理——" + host + ":" + port);
		// 设置HTTP访问要使用的代理服务器的地址
		prop.setProperty("http.proxyHost", host);
		// 设置HTTP访问要使用的代理服务器的端口
		prop.setProperty("http.proxyPort", port);
	}

	/**
	 * 设置https代理
	 */
	public void setLocalHttpsProxy(String host, String port) {
		Properties prop = System.getProperties();
		// 设置HTTPS访问要使用的代理服务器的地址
		prop.setProperty("https.proxyHost", host);
		// 设置HTTPS访问要使用的代理服务器的端口
		prop.setProperty("https.proxyPort", port);
	}

	/**
	 * 设置ftp代理
	 */
	public void setLocalFtpProxy(String host, String port) {
		Properties prop = System.getProperties();
		// 设置FTP访问要使用的代理服务器的地址
		prop.setProperty("ftp.proxyHost", host);
		// 设置FTP访问要使用的代理服务器的端口
		prop.setProperty("ftp.proxyPort", port);
	}

	/**
	 * 设置socks代理
	 */
	public void setLocalSocksProxy(String host, String port) {
		Properties prop = System.getProperties();
		// 设置socks访问要使用的代理服务器的地址
		prop.setProperty("socks.proxyHost", host);
		// 设置socks访问要使用的代理服务器的端口
		prop.setProperty("socks.proxyPort", port);
	}

	/**
	 * 清除http代理设置
	 */
	public void removeLocalHttpProxy() {
		Properties prop = System.getProperties();
		// 清除HTTP访问的代理服务器设置
		prop.remove("http.proxyHost");
		prop.remove("http.proxyPort");
	}

	/**
	 * 清除https代理设置
	 */
	public void removeLocalHttpsProxy() {
		Properties prop = System.getProperties();
		// 清除HTTPS访问的代理服务器设置
		prop.remove("https.proxyHost");
		prop.remove("https.proxyPort");
	}

	/**
	 * 清除ftp代理设置
	 */
	public void removeLocalFtpProxy() {
		Properties prop = System.getProperties();
		// 清除ftp访问的代理服务器设置
		prop.remove("ftp.proxyHost");
		prop.remove("ftp.proxyPort");
	}

	/**
	 * 清除socks代理设置
	 */
	public void removeLocalSocksProxy() {
		Properties prop = System.getProperties();
		// 清除socks访问的代理服务器设置
		prop.remove("socks.proxyHost");
		prop.remove("socks.proxyPort");
	}

	// 测试HTTP访问
	public void testHttpProxy() throws MalformedURLException, IOException {
		URL url = new URL("http://en.wikipedia.org/wiki/Data_mining");
		// 直接打开连接，但系统会调用刚设置的HTTP代理服务器
		URLConnection conn = url.openConnection(); // ①
		Scanner scan = new Scanner(conn.getInputStream());
		// 读取远程主机的内容
		while (scan.hasNextLine()) {
			System.out.println(scan.nextLine());
		}
	}
	
	/**
	 * 更新默认路径代理
	 */
	public void updateProxy(){
		updateProxy(getProxyList());
	}

	/**
	 * 更新代理到指定的文件
	 * 
	 * @param fileName
	 */
	public void updateProxy(String fileName) {
		String proxyUrlPrefix = "http://www.cnproxy.com/proxy";
		String proxyUrlSuffix = ".html";
		Vector<String> vProxySite = new Vector<String>();
		for (int i = 1; i <= 10; i++) {// 从10个页面获取
			String proxyUrl = proxyUrlPrefix + i + proxyUrlSuffix;// 代理地址
			System.out.println("代理地址：" + proxyUrl);
			String proxyPageContent = WebCrawler.getPageStringFromWeb(proxyUrl);// 代理页面内容
			if (proxyPageContent.length() != 0) {
				vProxySite.addAll(getProxyList(proxyPageContent));
			}
		}
		SetUtil.writeSetToFile(vProxySite, fileName);
	}

	/**
	 * 获取代理页面中的代理地址
	 * 
	 * @param proxyPageContent
	 * @return
	 */
	public Vector<String> getProxyList(String proxyPageContent) {
		Vector<String> vProxy = new Vector<String>();
		proxyPageContent = proxyPageContent.toLowerCase();
		/****** 找端口号对应字母 *******/
		HashMap<String, String> hm = new HashMap<String, String>();
		String scriptBegin = "<script type=\"text/javascript\">";
		String scriptEnd = "</script>";
		int scriptBeginPos = proxyPageContent.indexOf(scriptBegin)
				+ scriptBegin.length();
		int scriptEndPos = proxyPageContent.indexOf(scriptEnd, scriptBeginPos);
		String keyString = proxyPageContent.substring(scriptBeginPos,
				scriptEndPos);
		keyString = keyString.replace("\n", "");
		keyString = keyString.replace("\r", "");
		System.out.println(keyString);
		String record[] = keyString.split(";");
		for (int i = 0; i < record.length; i++) {
			String key = record[i].substring(0, 1);
			String value = record[i].substring(3, 4);
			hm.put(key, value);
			System.out.println("端口："+key+","+value);
		}
		/********** 添加代理地址 **********/
		String tag = "ip:port";
		String proxyTag1 = "<tr><td>";
		String proxyTag2 = "<script type=text/javascript>";
		String proxyTag3 = "document.write(\":\"+";
		String proxyTag4 = "</td><td>";
		int tablePosBegin = proxyPageContent.indexOf(tag);
		int tablePosEnd = proxyPageContent.indexOf("</table>", tablePosBegin);
		int pos1 = proxyPageContent.indexOf(proxyTag1, tablePosBegin);
		while (pos1 < tablePosEnd && pos1 != -1) {
			pos1 += +proxyTag1.length();
			int pos2 = proxyPageContent.indexOf(proxyTag2, pos1);
			String ip = proxyPageContent.substring(pos1, pos2);
			int pos3 = proxyPageContent.indexOf(proxyTag3, pos1)
					+ proxyTag3.length();
			int pos4 = proxyPageContent.indexOf(")", pos3);
			int pos5 = proxyPageContent.indexOf(proxyTag4, pos1)
					+ proxyTag4.length();
			int pos6 = proxyPageContent.indexOf(proxyTag4, pos5);
			String protocol = proxyPageContent.substring(pos5, pos6);
			if (protocol.equals("http")) {
				String portChar[] = proxyPageContent.substring(pos3, pos4)
						.replace("+", ",").split(",");
				String port = "";
				for (int i = 0; i < portChar.length; i++) {
					port = port + hm.get(portChar[i]);
				}
				vProxy.add(ip + ":" + port);
				System.out.println(ip + ":" + port);
			}
			pos1 = proxyPageContent.indexOf(proxyTag1, pos6);
		}
		return vProxy;
	}

	public static void main(String[] args) throws IOException {
		Proxy proxy = new Proxy();
		proxy.updateProxy();
	}

}
