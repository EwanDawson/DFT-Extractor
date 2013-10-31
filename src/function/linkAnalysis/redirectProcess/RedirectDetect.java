/**
 * 
 */
package function.linkAnalysis.redirectProcess;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import function.util.FileUtil;



/**
 * @author weibifan
 *目的：检测wiki术语是否是重定向。
 *细节：
 *基本思路：
 *P10：得到待处理的术语，默认是Set
 *P20：循环处理每一个术语。
 *P30：从网上下载页面。
 *P40：分析页面头部，确认是否有重定向。
 *P41：如果没有，该术语结束。
 *P50：如果有，运用一系列规则进行判断。
 *P51：搜索内容，查找是否有有以该术语为标题的内容。
 *P52：
 *
 *测试用例：
 *1. 
 *
 *异常分析：
 *1. 组成部分，比如leaf_node
 *2. 操作，比如deque操作。
 */
public class RedirectDetect {

	Set<String> terms=new HashSet<String>();
	/**
	 * 
	 */
	public RedirectDetect() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 根据term从网上获取页面检测重定向
	 * @param term
	 * @param newTerm
	 * @return
	 */
	public boolean DetectFromWeb(String term, String[] newTerm){
		StringBuffer sbWeb=WikiWebPage.webPageByTitle(term);
		String sb= sbWeb.toString();
		try {
			sb=new String(sb.getBytes("gbk"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String lowerSb=sb.toString().toLowerCase();
		
		if(sb.length() < 10){
			System.err.println("error: "+sb);

		}
		
		String pattern="Redirected from <a href=";
		int pos=sb.indexOf(pattern);
		if(pos <0){
			System.out.println("not redirect!");
			//1）存在页面，2）错误页面。
			newTerm[0]=term;						
			return false;
		}
		
		//消气页面
		String mayReferToPattern="</b> may refer to:</p>";
		int mayReferToPos=sb.indexOf(mayReferToPattern);
		if(mayReferToPos>0){
			newTerm[0]=term;						
			return false;
		}
		
		//二级标题
		//>Cycle detection</span></h2>
		String temp=removeBrace(term);		
		temp=temp.replaceAll("_", " ");
		String h2TitlePattern=">"+temp.toLowerCase()+"</span></h2>";
		int h2TitlePos=lowerSb.indexOf(h2TitlePattern);
		if(h2TitlePos>0){
			newTerm[0]=term;						
			return false;
		}
		
		//判断标题
		String pattern2="<h1 id=\"firstHeading\" class=\"firstHeading\">";
		int pos2=sb.indexOf(pattern2);
		String pattern2End="</span></h1>";
		int newBeginPos=pos2 + pattern2.length()+17;
		int pos2End = sb.indexOf(pattern2End, newBeginPos);
		
		
		if(pos2 <0 || pos2End <0){
			//标题不存在
			System.out.println("title not exist!");
			newTerm[0]=term;						
			return false;
		}

		String fullTitle=null;
		fullTitle = sb.substring(newBeginPos, pos2End);
		
		fullTitle=fullTitle.replaceAll(" ", "_");
		newTerm[0]=fullTitle;
		return true;
	}

	/**
	 * 根据term从dir获取页面检测重定向
	 * @param dir
	 * @param term
	 * @param newTerm
	 * @return
	 */
	public boolean DetectFromDir(String dir,String term, String[] newTerm){
		String sb=FileUtil.readFile(dir+"\\"+term+".html");
		try {
			sb=new String(sb.getBytes("gbk"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lowerSb=sb.toString().toLowerCase();
		
		if(sb.length() < 10){
			System.err.println("error: "+sb);

		}
		
		String pattern="Redirected from <a href=";
		int pos=sb.indexOf(pattern);
		if(pos <0){
			System.out.println("not redirect!");
			//1）存在页面，2）错误页面。
			newTerm[0]=term;						
			return false;
		}
		
		//消气页面
		String mayReferToPattern="</b> may refer to:</p>";
		int mayReferToPos=sb.indexOf(mayReferToPattern);
		if(mayReferToPos>0){
			newTerm[0]=term;						
			return false;
		}
		
		//二级标题
		//>Cycle detection</span></h2>
		String temp=removeBrace(term);		
		temp=temp.replaceAll("_", " ");
		String h2TitlePattern=">"+temp.toLowerCase()+"</span></h2>";
		int h2TitlePos=lowerSb.indexOf(h2TitlePattern);
		if(h2TitlePos>0){
			newTerm[0]=term;						
			return false;
		}
		
		//判断标题
		String pattern2="<h1 id=\"firstHeading\" class=\"firstHeading\">";
		String h1Span="<span dir=\"auto\">";
		int pos2=sb.indexOf(pattern2);
		pos2=sb.indexOf(h1Span, pos2);
		String pattern2End="</span>";
		int newBeginPos=pos2+h1Span.length();
		int pos2End = sb.indexOf(pattern2End, newBeginPos);
		
		
		if(pos2 <0 || pos2End <0){
			//标题不存在
			System.out.println("title not exist!");
			newTerm[0]=term;						
			return false;
		}

		String fullTitle=null;
		fullTitle = sb.substring(newBeginPos, pos2End);		
		fullTitle=fullTitle.replaceAll(" ", "_");
		fullTitle=fullTitle.replaceAll("&amp;", "&"); //替换掉HTML转义字符，如遇到其它转义字符，需同样处理
		newTerm[0]=fullTitle;
		return true;
	}
	
	//必须要先调用baseDetect
	public boolean extDetect(String term, String[] newTerm){
		return true;
	}

	private static String removeBrace(String input){
		String newStr=input;
		String pattern="_(";
		String pattern2=" (";
		int end=input.indexOf(pattern);
		int end2=input.indexOf(pattern2);
		
		if(end>0){
		newStr=input.substring(0, end);
		}
		if(end2>0){
		newStr=input.substring(0, end2);
		}
		return newStr;
	}
}
