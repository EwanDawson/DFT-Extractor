package function.DTExtraction;

import java.io.File;

import function.util.FileUtil;

public class DTExtractor {
	/**
	 * @param args
	 */
	private String basePath="f:/FacetedTaxonomy/";//术语抽取的初始位置
	private String DTPath="";//领域术语存放路径
	private String DomainTerm="";//领域名称
	private double processId = 0;// 流程控制标签
	private String processIdFile = "";//流程控制文件路径
	private String catPath="";//领域目录文件夹
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public DTExtractor(String DomainTerm){
		this.DomainTerm=DomainTerm;
		this.DTPath=basePath+DomainTerm+"/html";
		this.catPath=basePath+DomainTerm+"/category";
		File f=new File(DTPath);
		f.mkdirs();
		File fCat=new File(catPath);
		fCat.mkdirs();
		this.processIdFile = DTPath + "/processId.txt";
		File fProcess = new File(processIdFile);
		if (!fProcess.exists())
			FileUtil.writeStringFile("0", processIdFile);
	}
	
	public double getProcessId() {
		double id = Double.valueOf(FileUtil.readFile(processIdFile));
		processId = id;
		return processId;
	}

	public void setProcessId(double processId) {
		this.processId = processId;
		String id = String.valueOf(processId);
		FileUtil.writeStringFile(id, processIdFile);
	}
	
	public void extract(){
		//layer1
		Layer1Extractor extractor1=new Layer1Extractor(basePath,DomainTerm);
		extractor1.extract();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DTExtractor dte=new DTExtractor("Computer_network");
		dte.extract();
	}

}
