package function.DTExtraction;

import function.base.ExcelBase;

/**
 * 根据领域词来筛选术语
 * 
 * @author MJ
 * @description
 */
public class SelectAccordingDT extends ExcelBase {

	String fileName = "";
	String domainName = "";

	public SelectAccordingDT(String fileName, String domainName) {
		this.fileName = fileName;
		this.domainName = domainName;
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		int termFeatureSheet = 0;
		int selectSheet = 1;
		int selectRecord = 1;
		for (int i = 1; i < getRows(termFeatureSheet); i++) {
			String term = getStringValue(termFeatureSheet, "term", i);
			String category = getStringValue(termFeatureSheet, "category", i);
			String FSWikiTerm = getStringValue(termFeatureSheet, "FSWikiTerm",
					i);
			String editor = getStringValue(termFeatureSheet, "editor", i);
			if (category.toLowerCase().contains(domainName.toLowerCase())
					|| FSWikiTerm.toLowerCase().contains(
							domainName.toLowerCase())) {
				setStringValue(selectSheet, "term", selectRecord, term);
				setStringValue(selectSheet, "category", selectRecord, category);
				setStringValue(selectSheet, "FSWikiTerm", selectRecord,
						FSWikiTerm);
				setStringValue(selectSheet, "editor", selectRecord++, editor);
			}
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="F:\\extractTest\\Data_mining\\process\\layer1.xls";
		String domainName="Data_mining";
		SelectAccordingDT sadt=new SelectAccordingDT(fileName, domainName);
		sadt.run(sadt);
	}

}
