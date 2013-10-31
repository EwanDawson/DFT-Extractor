package function.DTExtraction;

import java.util.Vector;

import function.base.ExcelBase;
import function.util.SetUtil;

/**
 * 去除categoryIsSpecial and term is special，从srcSheet拷贝到desSheet
 * 
 * @author MJ
 * @description
 */
public class RemoveSpecial extends ExcelBase {

	/**
	 * @param args
	 */
	String fileName = "";// 抽取的category统计文件
	int srcSheet = 0;
	int desSheet = 1;
	String termExceptionList = "";
	String personOrganizationList = "";
	String npersonOrganizationList = "";

	public RemoveSpecial(String fileName, int srcSheet, int desSheet,
			String termExceptionList, String personOrganizationList,
			String npersonOrganizationList) {
		super();
		this.fileName = fileName;
		this.srcSheet = srcSheet;
		this.desSheet = desSheet;
		this.termExceptionList = termExceptionList;
		this.personOrganizationList = personOrganizationList;
		this.npersonOrganizationList = npersonOrganizationList;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Data_mining Data_structure Computer_network
	}

	@Override
	public void process() throws Exception {
		// TODO Auto-generated method stub
		Vector<String> vPersonSuffix = SetUtil
				.readSetFromFile(personOrganizationList);
		Vector<String> vnPersonSuffix = SetUtil
				.readSetFromFile(npersonOrganizationList);
		Vector<String> vExceptionTerm = SetUtil
				.readSetFromFile(termExceptionList);
		int recordId = 1;
		for (int i = 1; i < getRows(srcSheet); i++) {
			String category = getStringValue(srcSheet, "category", i);
			String term = getStringValue(srcSheet, "term", i);
			String fs = getStringValue(srcSheet, "FSWikiTerm", i);
			boolean selectTag = false;
			for (int j = 0; j < vExceptionTerm.size(); j++) {
				String exceptionTerm = vExceptionTerm.get(j);
				if (term.contains(exceptionTerm)) {
					selectTag = true;
					break;
				}
			}// termException
			if (selectTag)
				continue;
			for (int j = 0; j < vnPersonSuffix.size(); j++) {
				String suffix = vnPersonSuffix.get(j);
				if (category.toLowerCase().contains(suffix)) {
					setStringValue(desSheet, "term", recordId, term);
					setStringValue(desSheet, "category", recordId, category);
					setStringValue(desSheet, "FSWikiTerm", recordId++, fs);
					selectTag = true;
					break;
				}
			}// vnPersonSuffix
			if (selectTag)
				continue;
			for (int j = 0; j < vPersonSuffix.size(); j++) {
				String suffix = vPersonSuffix.get(j);
				if (category.toLowerCase().contains(suffix)) {
					setStringValue(srcSheet, "catIsSpecial", i, "true");
					setStringValue(srcSheet, "catSpecialSuffix", i, suffix);
					System.out.println(term + "——" + suffix);
					selectTag = true;
					break;
				}
			}//PersonSuffix
			if(!selectTag){
				setStringValue(desSheet, "term", recordId, term);
				setStringValue(desSheet, "category", recordId, category);
				setStringValue(desSheet, "FSWikiTerm", recordId++, fs);
			}
		}
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

}
