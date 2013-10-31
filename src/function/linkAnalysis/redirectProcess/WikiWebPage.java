package function.linkAnalysis.redirectProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
/*
 * 1)term必须是下划线，不能是空格。
 */
public class WikiWebPage {

	public WikiWebPage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static StringBuffer webPageByTitle(String title){
		String fullUrl=buildNormalPageUrl(title);
		return getPageBufferFromWeb(fullUrl);
	}
	// 构建一般页面的URL
		public static String buildNormalPageUrl(String term) {
			return "http://en.wikipedia.org/wiki/" + term;
		}
		
		// 根据ULR从网上获取HTML文件
		public static StringBuffer getPageBufferFromWeb(String url) {
			return getPageBufferFromWeb2(url,null);
		}	
		
	// 构建分类页面的URL
	public Vector<String> buildCategoryUrls(Vector<String> urls) {
		Vector<String> fullUrls = new Vector<String>();
		for (String url : urls) {
			fullUrls.add(buildCategoryUrl(url));
		}
		return fullUrls;
	}

	public static String buildCategoryUrl(String term) {
		return "http://en.wikipedia.org/wiki/Category:" + term;
	}

		//得到页面的内容，返回值删掉了回车，传出参数未删除回车
		public static StringBuffer getPageBufferFromWeb2(String url,StringBuffer outBuffer) {
			StringBuffer sb = new StringBuffer();
			try {
				URL my_url = new URL(url);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						my_url.openStream()));
				
				String strTemp = "";
				while (null != (strTemp = br.readLine())) {
					sb.append(strTemp);
					if(null != outBuffer) outBuffer.append(strTemp+"\r\n");
				}
				System.out.println("get page successful form " + url);
				if(null != outBuffer) outBuffer.append(br.toString());
			} catch (Exception ex) {
				//ex.printStackTrace();
	                        System.err.println("page not found: " + url);
			}

			return sb;
		}
}
