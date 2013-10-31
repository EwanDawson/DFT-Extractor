package function.txtAnalysis.basicTxtFeatures;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class MOREConfig {

	public String htmlPath ="";// html数据集的目录
	public String getHtmlPath() {
		return htmlPath;
	}


	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	public String txtPath =htmlPath+ "_txt";// txt数据集的目录
	public String xlsPath =htmlPath+"_xls";// Excel文件夹
	public String xlsName = xlsPath + "/relation-BTF.xls";//基本文本特征的excel文件
	public String termSetPath=htmlPath+"_termset.txt";//术语集文件

	
	public void createFile() {
		this.txtPath =htmlPath+ "_txt";// txt数据集的目录
		this.xlsPath =htmlPath+"_xls";// Excel文件夹
		this.xlsName = xlsPath + "/relation-BTF.xls";//基本文本特征的excel文件
		termSetPath=htmlPath+"_termset.txt";//术语集文件
		File txt = new File(txtPath);
		File xls = new File(xlsPath);
		txt.mkdirs();
		xls.mkdirs();
		String[] headName = { "ID", "sourceURLName", "toURLName", "Relation",
				"srcID", "toID", "Note","snTag", "DeltaText", "posInHtml",
				"AnchorText", "linkSameAhchor", "htmlLen", "posInHtml2",
				"LogicPos", "repeatNum", "linkMode", "LinkSequence",
				"PosInTxt", "txtLen", "PosInTxt2", "ParaInTxt", "ParaNum",
				"ParaInTxt2", "posInPara", "ParaLen", "posInPara2"};
		try {
			WritableWorkbook wb = Workbook.createWorkbook(new File(xlsName));
			// 创建数据表格
			WritableSheet wsheet = wb.createSheet("BTF",0);
			for (int i = 0; i < headName.length; i++) {
				Label label = new Label(i, 0, headName[i]);
				wsheet.addCell(label);
			}
			wb.write();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("相关文件创建成功……");
	}
}
