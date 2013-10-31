package function.txtAnalysis.util;

import java.net.URL;
import java.util.Vector;

import function.util.SetUtil;

public class OddComplexReplace {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String srcCatFile = "F:\\Data\\wei\\CN\\CN_Wiki-Category.csv";
		String targetCatFile = "F:\\Data\\wei\\CN\\CN_Wiki-Category-replace.csv";
		new OddComplexReplace().oddComplexReplace(srcCatFile, targetCatFile);
	}

	public void oddComplexReplace(String srcCatFile, String targetCatFile) {
		URL dicFile=getClass().getResource("/resources/standardspellings.txt");
		Vector<String> vDic = SetUtil.readSetFromFile(dicFile.toString());
		Vector<String> vSrc = SetUtil.readSetFromFile(srcCatFile);
		Vector<String> vTarget = new Vector<String>();
		for (int i = 0; i < vSrc.size(); i++) {
			String temp[] = vSrc.get(i).split(",");
			String a0="",a1="",b0=temp[0],b1=temp[1];
			if(temp[0].contains("_")){
				a0=temp[0].substring(0, temp[0].lastIndexOf("_")+1);
				b0=temp[0].substring(temp[0].lastIndexOf("_")+1,temp[0].length());
			}
			if(temp[1].contains("_")){
				a1=temp[1].substring(0, temp[1].lastIndexOf("_")+1);
				b1=temp[1].substring(temp[1].lastIndexOf("_")+1,temp[1].length());
			}
			String s1 = a0+replace(b0, vDic);
			String s2 = a1+replace(b1, vDic);
			vTarget.add(s1 + "," + s2);
		}
		SetUtil.writeSetToFile(vTarget, targetCatFile);
		System.out.println("OK");
	}

	/**
	 * 
	 * @param complexTerm
	 *            ¸´Êý´Ê
	 * @param vDic
	 *            ×Öµä
	 * @return
	 */
	public String replace(String complexTerm, Vector<String> vDic) {
		String temp = "";
		String result = "";
		if (complexTerm.endsWith("ies")) {
			temp = complexTerm.substring(0, complexTerm.length() - 3) + "y";
			if (vDic.contains(temp))
				result = temp;
			else
				result = complexTerm;
		} else if (complexTerm.endsWith("ses") || complexTerm.endsWith("xes")
				|| complexTerm.endsWith("ches") || complexTerm.endsWith("shes")
				|| complexTerm.endsWith("toes") || complexTerm.endsWith("roes")) {
			temp = complexTerm.substring(0, complexTerm.length() - 2);
			if (vDic.contains(temp))
				result = temp;
			else
				result = complexTerm;
		} else if (complexTerm.endsWith("s")&&!complexTerm.endsWith("cs")) {
			temp = complexTerm.substring(0, complexTerm.length() - 1);
			if (vDic.contains(temp))
				result = temp;
			else
				result = complexTerm;
		} else
			result = complexTerm;
		if (!result.endsWith(complexTerm))
			System.out.println(complexTerm + "±»Ìæ»»Îª£º" + result);
		return result;
	}

}
