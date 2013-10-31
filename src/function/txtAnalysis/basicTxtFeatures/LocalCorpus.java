package function.txtAnalysis.basicTxtFeatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
/*
 * 1、对本地的HTML及Text进行操作，主要是读入。
 * 2、对领域术语集进行操作。获取，写入，读出。
 */
public class LocalCorpus {
	MOREConfig mc=null;
	public Vector<String> TermSet=new Vector<String>();
	Set<String> lowercaseTermSet = new HashSet<String>();
	
	public LocalCorpus(MOREConfig mc){
		this.mc=mc;
	}
	
	public boolean isTerm(String term){
		
		return lowercaseTermSet.contains(term.toLowerCase());
	}
	
	public String getTrueTerm(String term){
		for(String temp:TermSet){
			if(temp.equalsIgnoreCase(term.toLowerCase())){
				return temp;
			}
		}
		return term;
	}
	
	//从文本文件得到领域术语集，文件是word list，每行一个。
	public Vector<String> getTermSetFromFile(String filePath){
		FileReader fr;		
		try {
			fr = new FileReader(filePath);
			System.out.println("读取文件……  "+ filePath);
			BufferedReader br = new BufferedReader(fr);
				
			String s;
			TermSet.removeAllElements();
			while ((s = br.readLine()) != null) {
				TermSet.add(s);
				lowercaseTermSet.add(s.toLowerCase());
			}
			System.out.println("构建TermSet成功，总数="+ TermSet.size());			
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return TermSet;
	}
	//读取整个目录的html文件名字作为领域术语集。
	public Vector<String> getFileNamesFromDirectory(String dir){
		File f = new File(dir);
		File[] childs = f.listFiles();
		System.out.println("读取目录……  "+ dir);
		TermSet.removeAllElements();
		for(File file:childs){
			String fileName = file.getName();
			String realName = fileName.substring(0, fileName.length() - 5);
			TermSet.add(realName);
			lowercaseTermSet.add(realName.toLowerCase());
		}
		System.out.println("构建TermSet成功，总数="+ TermSet.size());	
		return TermSet;
	}
	//将术语集存成text文件，word list方式
	public void storeTermSet(String filePath){
		FileWriter fw=null;
		try {
			fw = new FileWriter(filePath);
			System.out.println("TermSet保存到："+filePath);
			for(String term:TermSet)
				//不能是小写，从wiki爬取时部分页面会出错
				fw.write(term+"\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TermSet保存完毕……共："+TermSet.size());
	}
	

	
	//根据文件名（无目录，无扩展名）从本地读取文件内容到内存缓冲区
	public StringBuffer getHtmlFileBuffer(String term){
		String fileName=mc.htmlPath +"/"+term+".html";		
		return getFileBuffer(fileName,null);
	}
	public StringBuffer getTextFileBuffer(String term){
		String fileName=mc.txtPath +"/"+term+".txt";		
		return getFileBuffer(fileName,null);
	}
	//ParaRange用于指明每个段（行）的结束位置，严格来说是下个段的开始位置。
	//元素0永远存的是文件大小。
	public StringBuffer getTextFileBuffer(String term,Vector<Integer> ParaRange){
		String fileName=mc.txtPath +"/"+term+".txt";
		return getFileBuffer(fileName,ParaRange);
	}
	public StringBuffer getFileBuffer(String fileName,Vector<Integer> ParaRange){
		FileReader fr;
		StringBuffer sb=null;
		try {
			fr = new FileReader(fileName);
			System.out.println("读取文件……  "+ fileName);
			BufferedReader br = new BufferedReader(fr);
			sb = new StringBuffer();	
			if(ParaRange!=null)ParaRange.add(sb.length());
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
				if(ParaRange!=null)ParaRange.add(sb.length());
			}
			System.out.println("读取文件到缓冲区成功…… 文件大小="+ sb.length());			
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return sb;
	}

}
