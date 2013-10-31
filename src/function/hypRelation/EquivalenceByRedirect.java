/**
 * 
 */
package function.hypRelation;

import function.linkAnalysis.redirectProcess.WikiWebPage;



/**
 * @author weibifan
 *目的：检测wiki术语是否是重定向。
 *输入：页面内容的字符串
 *输出：是否是等价的重定向
 *基本思路：
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
public class EquivalenceByRedirect {


	public EquivalenceByRedirect() {
		// TODO Auto-generated constructor stub
		
	}

	
	public boolean basicDetect(String term, String[] newTerm){
		StringBuffer sb=WikiWebPage.webPageByTitle(term);
		String lowerSb=sb.toString().toLowerCase();
//		term.
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
		
		//消气页面，分为2类：may mean:及may refer to:
		String mayReferToPattern="</b> may refer to:</p>";
		int mayReferToPos=sb.indexOf(mayReferToPattern);
		if(mayReferToPos>0){
			newTerm[0]=term;						
			return false;
		}
		
		//二级标题
		//>Cycle detection</span></h2>
		String temp=term.replaceAll("_", " ");
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
	
	//必须要先调用baseDetect
	public boolean extDetect(String term, String[] newTerm){
//		String exactPattern=compactTerm.toLowerCase();
//		
//		int exactPos=lowerSb.indexOf(exactPattern, pos2End);
//		int num=0;
//		while(0<exactPos && exactPos<sb.length()){
//			exactPos=lowerSb.indexOf(exactPattern, exactPos+exactPattern.length());
//			num++;
//		}
//		
//		if(num>2){
//			newTerm[0]=term;	
//			//
//			return false;
//		}
//		
//		String stemTerm=Stemmer.stem(compactTerm);
//		String stemTitle=Stemmer.stem(compactTitle);
//		double sim=StringSimilarity.getTitleSim(stemTerm, stemTitle);
//		if(sim>0.8){
//			newTerm[0]=term;	
//			//
//			return true;
//		}
//		
//		System.out.println(compactTitle);
		return true;
	}
	
//	public static String multiWordsStem(String inWords){
//		
//		String[] words=inWords.split(" ");
//		String result="";
//		String temp="";
//		for(String word:words){			
//			result=result+temp+PorterStemmer.stem(word);
//			temp=" ";
//		}
//		return result;
//	}

}
