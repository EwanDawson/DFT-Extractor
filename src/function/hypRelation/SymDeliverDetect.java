package function.hypRelation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import function.base.ExcelBase;

/**
 * 
 * @author MJ
 * @description 对称传递检测
 * 
 */
public class SymDeliverDetect extends ExcelBase {

	String fileName = "";
	int sheetId = 1;

	public SymDeliverDetect(String fileName, int sheetId) {
		this.fileName = fileName;
		this.sheetId = sheetId;
	}

	public static void main(String args[]) {
		String fileName = "F:\\DOFT-data\\hyperExtraction\\DS\\DS_html-hypRelation\\relation.xls";
		int sheetId = 1;
		SymDeliverDetect sdd = new SymDeliverDetect(fileName, sheetId);
		sdd.run(sdd);
	}

	@Override
	public void process() throws IOException {
		// TODO Auto-generated method stub
		SymmetryDetect();
		deliverDetect();
	}

	@Override
	public void run(ExcelBase eb) {
		// TODO Auto-generated method stub
		eb.go(fileName);
	}

	//对称性检测
	public void SymmetryDetect() {
		int num1 = 0, num2 = 0;
		for (int i = 1; i < getRows(sheetId); i++) {
			String src = getStringValue(sheetId, "sourceURLName", i);
			String to = getStringValue(sheetId, "toURLName", i);
			String rel = getStringValue(sheetId, "relation", i);
			String preRel = getStringValue(sheetId, "predictRelation", i);
			if (rel.equals("noExist") && preRel.equals("A is a B")) {
				for (int j = 1; j < getRows(sheetId); j++) {
					String srcj = getStringValue(sheetId, "sourceURLName", j);
					String toj = getStringValue(sheetId, "toURLName", j);
					String preRelj = getStringValue(sheetId, "predictRelation",
							j);
					String checkRelation = checkRelation(src, to);
					String checkRelationj = "Other";
					if (src.equals(toj) && to.equals(srcj)) {
						if (!preRelj.equals("B is a A")) {
							if (checkRelation.equals("A is a B"))
								checkRelationj = "B is a A";
							else if (checkRelation.equals("B is a A"))
								checkRelationj = "A is a B";
							System.out.println(src + "->" + to + "(" + preRel
									+ ")	" + to + "->" + src + "(" + preRelj
									+ ")");
							System.out.println("纠正：" + src + "->" + to + "("
									+ checkRelation + ")	" + to + "->" + src
									+ "(" + checkRelationj + ")");
							setStringValue(sheetId, "checkRelation", i,
									checkRelation);
							setStringValue(sheetId, "checkRelation", j,
									checkRelationj);
							num1++;
							break;
						} else
							setStringValue(sheetId, "checkRelation", i, preRel);
					} else
						setStringValue(sheetId, "checkRelation", i, preRel);
				}
			}// end if
			else if (rel.equals("noExist") && preRel.equals("B is a A")) {
				for (int j = 1; j < getRows(sheetId); j++) {
					String srcj = getStringValue(sheetId, "sourceURLName", j);
					String toj = getStringValue(sheetId, "toURLName", j);
					String preRelj = getStringValue(sheetId, "predictRelation",
							j);
					if (src.equals(toj) && to.equals(srcj)) {
						if (!preRelj.equals("A is a B")) {
							String checkRelation = checkRelation(src, to);
							String checkRelationj = "Other";
							if (checkRelation.equals("A is a B"))
								checkRelationj = "B is a A";
							else if (checkRelation.equals("B is a A"))
								checkRelationj = "A is a B";
							System.out.println(src + "->" + to + "(" + preRel
									+ ")	" + to + "->" + src + "(" + preRelj
									+ ")");
							System.out.println("纠正：" + src + "->" + to + "("
									+ checkRelation + ")	" + to + "->" + src
									+ "(" + checkRelationj + ")");
							setStringValue(sheetId, "checkRelation", i,
									checkRelation);
							setStringValue(sheetId, "checkRelation", j,
									checkRelationj);
							num2++;
							break;
						} else
							setStringValue(sheetId, "checkRelation", i, preRel);
					} else
						setStringValue(sheetId, "checkRelation", i, preRel);
				}
			}// end if
			else
				setStringValue(sheetId, "checkRelation", i, preRel);
		}// end for
		System.out.println("共有" + num1 + "个A is a B不对称");
		System.out.println("共有" + num2 + "个B is a A不对称");
	}// SymmetryDetect()

	//传递性检测
	public void deliverDetect() {
		HashMap<String, String> hmReal = new HashMap<String, String>();
		HashMap<String, Integer> hmCheck = new HashMap<String, Integer>();
		for (int j = 1; j < getRows(sheetId); j++) {
			String srcj = getStringValue(sheetId, "sourceURLName", j);
			String toj = getStringValue(sheetId, "toURLName", j);
			String relj = getStringValue(sheetId, "relation", j);
			if (!relj.equals("noExist") && !relj.equals("Other"))
				hmReal.put(srcj + "->" + toj, relj);
			else
				hmCheck.put(srcj + "->" + toj, j);
		}
		HashSet<String> hsCheck = new HashSet<String>();
		Iterator<String> it = hmReal.keySet().iterator();
		while (it.hasNext()) {
			String srcto = it.next();
			String node[] = srcto.split("->");
			String rel = hmReal.get(srcto);
			Iterator<String> itj = hmReal.keySet().iterator();
			while (itj.hasNext()) {
				String srctoj = itj.next();
				String relj = hmReal.get(srctoj);
				String nodej[] = srctoj.split("->");
				if (node[1].equals(nodej[0]) && rel.equals(relj)) {
					String src_toj = node[0] + "->" + nodej[1];
					if (hmCheck.containsKey(src_toj)&&!hsCheck.contains(src_toj)) {
						String relCheck = getStringValue(sheetId,
								"checkRelation", hmCheck.get(src_toj));
						if (!relCheck.equals(rel)) {
							System.out.println("传递错："+node[0] + "->" + node[1] + ":"
									+ rel + "----" + nodej[0] + "->" + nodej[1]
									+ ":" + relj + "---(deliver)" + node[0]
									+ "->" + nodej[1] + ":" + relCheck+"("+hmCheck.get(src_toj)+"行)");
							setStringValue(sheetId, "checkRelation",
									hmCheck.get(src_toj), rel);
							hsCheck.add(src_toj);
						}
					}
					String toj_src = nodej[1] + "->" + node[0];
					if (hmCheck.containsKey(toj_src)&&!hsCheck.contains(toj_src)) {
						String relCheck = getStringValue(sheetId,
								"checkRelation", hmCheck.get(toj_src));
						if (relCheck.equals(rel) || relCheck.equals("Other")) {
							System.out.println("传递错：(symDeliver)" + nodej[1] + "->"
									+ node[0] + ":" + relCheck+"("+hmCheck.get(toj_src)+"行)");
							if (rel.equals("A is a B"))
								setStringValue(sheetId, "checkRelation",
										hmCheck.get(toj_src), "B is a A");
							else
								setStringValue(sheetId, "checkRelation",
										hmCheck.get(toj_src), "A is a B");
							hsCheck.add(toj_src);
						}
					}
				}
			}// end while
		}//end while
		System.out.println("共修改传递错误：" + hsCheck.size());
	}// deliverDetect()

	/**
	 * 根据给定的src和to确定关系
	 * 
	 * @param src
	 * @param to
	 * @return
	 */
	public String checkRelation(String src, String to) {
		String relation = "";
		src = src.toLowerCase();
		to = to.toLowerCase();
		String srcSuffix = src, toSuffix = to;
		if (src.contains("_"))
			srcSuffix = src.substring(src.lastIndexOf("_") + 1, src.length());
		if (to.contains("_"))
			toSuffix = to.substring(to.lastIndexOf("_") + 1, to.length());

		if (to.startsWith(srcSuffix))
			relation = "A is a B";
		else if (src.startsWith(toSuffix))
			relation = "B is a A";
		else if (srcSuffix.equals(toSuffix))
			relation = "Other";
		else
			relation = "Other";
		return relation;
	}
}
