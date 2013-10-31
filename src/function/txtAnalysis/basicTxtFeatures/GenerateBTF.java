package function.txtAnalysis.basicTxtFeatures;

import function.jsoup.WikiHtml2Txt;

/*
 * 大小写问题：
 * 1）源URL name必须要保留大小写，否则从wiki下载页面时各种各样的问题。
 * 2）目标URL name必须保留大小写，在html搜索时，必须要带大小写。
 * 
 * 属性获取
 * 1）基本属性，不依赖其他属性，只依赖html文件
 * 2）附加属性，依赖基本属性，并依赖text文件
 */

public class GenerateBTF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String htmlPath="f:/DM_html";
		GenerateBTF gbtf=new GenerateBTF();
		gbtf.generateBTF(htmlPath);
	}
	
	/**
	 * 生成基本的文本特征
	 * @param htmlDir
	 */
	public void generateBTF(String htmlDir){
		/**********html转换txt************/
		WikiHtml2Txt wht=new WikiHtml2Txt();
		wht.parseDirText(htmlDir);
		/*********** 下面是生成html词集 **********/
		MOREConfig mc = new MOREConfig();
		mc.setHtmlPath(htmlDir);
		mc.createFile();
		String RdWrFile = mc.xlsName;
		BasicFeatures BFss = new BasicFeatures(mc);
		BFss.init(RdWrFile);
		BFss.createTermSet();
		/*********** 下面是抽取超链接工作 **********/
		BFss.go();
		/**********下面是生成txt相关属性************/
		BasicTxtFeatures BTFss=new BasicTxtFeatures(mc);
		BTFss.init(RdWrFile);
		BTFss.go();
		/************* 全部操作完毕 ***********/
		System.out.println("生成超链接基本工作完毕！");
	}

}
