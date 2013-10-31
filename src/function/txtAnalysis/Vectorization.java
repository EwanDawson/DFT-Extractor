package function.txtAnalysis;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Vector;

import function.util.FileUtil;
import function.util.SetUtil;

/**
 * html网页向量化
 * @author MJ
 * @description
 */
public class Vectorization {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String txtDir="F:\\Data\\术语抽取数据集\\DMDataSet\\DM_experiment\\removeNoise\\layer3-new-txt";
		Vectorization vz=new Vectorization();
		vz.wordVector(txtDir);
	}
	/**
	 * 对txt文件夹进行向量化
	 * @param txtDir
	 * @return 文本向量化后的文件夹路径
	 */
	public String wordVector(String txtDir) {
		String vectorDir = txtDir + "_wordVector";
		File fSave = new File(vectorDir);
		fSave.mkdirs();
		File f = new File(txtDir);
		File childs[] = f.listFiles();
		int size=childs.length;
		for (int i = 0; i <size; i++) {
			String srcPath = childs[i].getAbsolutePath();
			String fileName = childs[i].getName();
			String desPath = vectorDir +"/"+ fileName;
			Vector<String> vTerm = getWord(srcPath);
			SetUtil.writeSetToFile(vTerm, desPath);
			System.out.println("向量化："+fileName+"--------("+(i+1)+"/"+size+")");
		}
		return vectorDir;
	}
	public Vector<String> getWord(String txtPath) {
		URL stopWordFile=getClass().getResource("/resources/stopWord.txt");
		Vector<String> vStopWord=SetUtil.readSetFromFile(stopWordFile.toString());
		Vector<String> vTxtString =SetUtil.readSetFromFile(txtPath);
		Vector<String> vWord = new Vector<String>();
		for(String txtString:vTxtString){
			int p=0,q=0;
			while(p<txtString.length()&&q<txtString.length()){
				if(isWord(txtString.charAt(p))){
					q=p+1;
					while(q<txtString.length()&&isWord(txtString.charAt(q)))q++;
					String word=txtString.substring(p, q).toLowerCase();
					if(!isStopWord(word,vStopWord)&&!isNumber(word)&&word.length()>1){//不是停用词
						vWord.add(word);//放到vector里
					}
					p=q+1;
				}
				else p++;
			}//end while
		}
		return vWord;
	}
	public boolean isWord(char c){
		if(c>='a'&&c<='z'||c>='A'&&c<='Z'||c>='0'&&c<='9')return true;
		else return false;
	}//判别字符函数
	public boolean isNumber(String s){
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)>='0'&&s.charAt(i)<='9')continue;
			else return false;
		}
		return true;
	}//判别字符函数
	public boolean isStopWord(String s,Vector<String> vStopWord){
		for(int i=0;i<vStopWord.size();i++){
			if(s.equals(vStopWord.get(i)))return true;
		}
		return false;
	}//判别停用词函数
	/**
	 * 对html文件夹进行向量化
	 * @param htmlDir
	 * @return 文本向量化后的文件夹路径
	 */
	public String wikiVector(String htmlDir) {
		String vectorDir = htmlDir + "_wikiVector";
		File fSave = new File(vectorDir);
		fSave.mkdirs();
		File f = new File(htmlDir);
		File childs[] = f.listFiles();
		int size=childs.length;
		for (int i = 0; i <size; i++) {
			String srcPath = childs[i].getAbsolutePath();
			String fileName = childs[i].getName().replace(".html", ".txt");
			String desPath = vectorDir +"/"+ fileName;
			Vector<String> vTerm = getWikiTerm(srcPath);
			SetUtil.writeSetToFile(vTerm, desPath);
			System.out.println("向量化："+fileName+"--------("+(i+1)+"/"+size+")");
		}
		return vectorDir;
	}

	/**
	 * 获取指定路径的wiki页面的词条
	 * @param filePath
	 * @return
	 */
	public Vector<String> getWikiTerm(String filePath) {
		String s = FileUtil.readFile(filePath);
		try {
			s=new String(s.getBytes("gbk"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Vector<String> vRemoveTag = new Vector<String>();
		vRemoveTag.add("History");
		vRemoveTag.add("External links");
		vRemoveTag.add("Further reading");
		vRemoveTag.add("Software");
		vRemoveTag.add("References");
		Vector<int[]> vInterval = getInterval(s, vRemoveTag);
		Vector<String> vTerm = new Vector<String>();
		String wikiTag = "a href=\"/wiki/";
		int posA = s.indexOf(wikiTag);
		int posB = 0;
		while (posA != -1) {
			posB = s.indexOf("\"", posA + wikiTag.length());
			String temp = s.substring(posA + wikiTag.length(), posB);
			if (temp.contains("#"))
				temp = temp.substring(0, temp.indexOf("#"));
			if (existInAmong(posA, vInterval) == false&&!temp.contains(":")&&!temp.equals("Main_Page"))
				vTerm.add(temp);
			posA = s.indexOf(wikiTag, posB);
		}
		return vTerm;
	}

	/**
	 * 
	 * @param s
	 * @param vRemoveTag
	 * @return 获取网页字符串removeTag的区间位置，比如History
	 */
	public Vector<int[]> getInterval(String s, Vector<String> vRemoveTag) {
		Vector<int[]> v = new Vector<int[]>();
		for (String tag : vRemoveTag) {
			String t2Tag = tag + "</span></h2>";
			if (s.contains(t2Tag)) {
				int interval[] = new int[2];
				interval[0] = s.indexOf(t2Tag);
				int pos = s.indexOf("</h2>", interval[0] + t2Tag.length());
				if (pos != -1)
					interval[1] = pos;
				else
					interval[1] = s.length();
				v.add(interval);
			}
		}
		return v;
	}

	/**
	 * 
	 * @param pos
	 * @param vInterval
	 * @return 判断给定的pos是否在给定的区间向量里面
	 */
	public boolean existInAmong(int pos, Vector<int[]> vInterval) {
		for (int i = 0; i < vInterval.size(); i++) {
			int interval[] = vInterval.get(i);
			if (pos > interval[0] && pos < interval[1]) {
				return true;
			} else
				continue;
		}
		return false;
	}

}
