package function.linkAnalysis.redirectProcess;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import function.base.ExcelBase;
import function.util.FileUtil;
import function.util.SetUtil;

/**
 * 
 * @author MJ
 * @description 重定向处理
 */
public class RedirectProcess extends ExcelBase {

	String xlsFileName = "";
	String fileName="";
	String newFileName="";
	int sheetID=0;
	boolean dirTag;
	
	public RedirectProcess(String fileName,String xlsFileName,String newFileName,boolean dirTag){
		this.fileName=fileName;
		this.xlsFileName=xlsFileName;
		this.newFileName=newFileName;
		this.dirTag=dirTag;
		File newF=new File(newFileName);
		newF.mkdirs();
	}
	
	public RedirectProcess(String fileName,boolean dirTag){
		this.fileName=fileName;
		this.dirTag=dirTag;
		this.newFileName=fileName+"-redirect";
		File newF=new File(newFileName);
		newF.mkdirs();
		if(dirTag==false)
			this.xlsFileName=fileName.replace(".txt", "-redirect.xls");
		else
			this.xlsFileName=fileName+"-redirect.xls";
	}
	
	/**
	 * 将抽取的重定向放到指定的xls里面
	 * @param fileName
	 * @param dirTag
	 * @param xlsFileName
	 * @param sheetID
	 */
	public RedirectProcess(String fileName,boolean dirTag,String xlsFileName,int sheetID){
		this.fileName=fileName;
		this.dirTag=dirTag;
		this.xlsFileName=xlsFileName;
		this.sheetID=sheetID;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		if(!dirTag)
			extractRedirectFromWeb();
		else
			extractRedirectFromDir();
			
	}

	/**
	 * 从网上抽取某个集合的重定向对应关系
	 * 
	 * @throws Exception
	 */
	public void extractRedirectFromWeb() {
		Vector<String> v=SetUtil.readSetFromFile(fileName);
		RedirectDetectMain rdm=new RedirectDetectMain("",false);
		for(int i=0;i<v.size();i++){
			String term=v.get(i);
			String title=rdm.getRedirectTerm(term);
			setStringValue(sheetID, "term", i+1, term);
			setStringValue(sheetID, "title", i+1, title);
			if (term.equals(title))
				setStringValue(sheetID, "redirectFlag", i+1, "false");
			else
				setStringValue(sheetID, "redirectFlag", i+1, "true");
		}
	}
	
	/**
	 * 从本地目录抽取某个集合的重定向对应关系
	 * 
	 * @throws Exception
	 */
	public void extractRedirectFromDir() {
		Vector<String> v=new Vector<String>();
		File f=new File(fileName);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String name=childs[i].getName();
			String term=name.substring(0, name.lastIndexOf("."));
			v.add(term);
		}
		RedirectDetectMain rdm=new RedirectDetectMain(fileName,true);
		for(int i=0;i<v.size();i++){
			String term=v.get(i);
			String title=rdm.getRedirectTerm(term);
			setStringValue(sheetID, "term", i+1, term);
			setStringValue(sheetID, "title", i+1, title);
			if(newFileName.length()>0)
			try {
				FileUtil.copyFile(new File(fileName+"/"+term+".html"), new File(newFileName+"/"+title+".html"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (term.equals(title))
				setStringValue(sheetID, "redirectFlag", i+1, "false");
			else
				setStringValue(sheetID, "redirectFlag", i+1, "true");
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(xlsFileName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Data_mining Data_structure Computer_network
		String fileName="F:\\FacetedTaxonomy\\Computer_network\\html\\layer2";
		boolean dirTag=true;
		RedirectProcess rp=new RedirectProcess(fileName, dirTag);
		rp.run(rp);
	}

}
