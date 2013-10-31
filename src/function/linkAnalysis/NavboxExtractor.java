package function.linkAnalysis;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import function.crawler.WebCrawler;
import function.util.FileUtil;
import function.util.SetUtil;

public class NavboxExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String htmlDir="F:\\test";
		String desPath="F:\\testNavbox";
		extractNavBox(htmlDir,desPath);
		System.out.println("全部完毕！");
	}
	
	/**
	 * 从指定html目录中抽取navbox放到desPath下
	 * @param htmlPath
	 * @param desPath
	 */
	public static void extractNavBox(String htmlPath,String desPath){
		String dataPath=desPath+"/data";
		File fData=new File(dataPath);
		fData.mkdirs();
		NavboxExtractor ne = new NavboxExtractor();
		String matchRelationFile=desPath+"/matchRelation.csv";
		File fHtml=new File(htmlPath);
		File childs[]=fHtml.listFiles();
		for(int i=0;i<childs.length;i++){
			String htmlFilePath=childs[i].getAbsolutePath();
			String htmlFileName=childs[i].getName();
			String wikiTerm=htmlFileName.substring(0, htmlFileName.indexOf(".html"));
			String s=FileUtil.readFile(htmlFilePath);
			System.out.println("正在处理："+wikiTerm);
			/*//不完整的页面先爬取完整
			while(!s.endsWith("</html>")){
				System.out.println(wikiTerm+":不完整");
				WebCrawler wc=new WebCrawler();
				String url="http://en.wikipedia.org/wiki/"+wikiTerm;
				wc.crawlPageByUrl(url,htmlFilePath);
				s=FileUtil.readFile(htmlFilePath);
			}*/
			//抽取
			HashMap<String, Vector<String>> hm=ne.extractNavboxFromString(s);
			Iterator<String> it=hm.keySet().iterator();
			Vector<String> vMatch=new Vector<String>();
			while(it.hasNext()){
				System.out.println("--------------------------------------------------------------------------------");
				String tableName=it.next();
				String fileName=tableName+".csv";
				String filePath=dataPath+"/"+fileName;
				vMatch.add(wikiTerm+","+tableName);
				if(ne.existInDir(fileName,dataPath))
					System.out.println(wikiTerm+"页面中的"+tableName+"表已经存在……");
				else
				{
					System.out.println(wikiTerm+"页面中的"+tableName+"表内容如下：");
					Vector<String> v=hm.get(tableName);
					SetUtil.writeSetToFile(v, filePath);
					for(String temp:v){
						System.out.println(temp);
					}
				}
			}
			SetUtil.appendSetToFile(vMatch, matchRelationFile);
		}
	}
	
	/**
	 * 
	 * @param vWikiTerm
	 * @param desPath
	 * 从网上抽取指定wiki术语集合的navbox，放到指定目录下
	 */
	public static void extractNavBox(Vector<String> vWikiTerm,String desPath){
		String dataPath=desPath+"/data";
		File fData=new File(dataPath);
		fData.mkdirs();
		NavboxExtractor ne = new NavboxExtractor();
		String matchRelationFile=desPath+"/matchRelation.csv";
		for(int i=0;i<vWikiTerm.size();i++){
			String wikiTerm=vWikiTerm.get(i);
			HashMap<String, Vector<String>> hm=extractNavBox(wikiTerm);
			Iterator<String> it=hm.keySet().iterator();
			Vector<String> vMatch=new Vector<String>();
			while(it.hasNext()){
				System.out.println("--------------------------------------------------------------------------------");
				String tableName=it.next();
				String fileName=tableName+".csv";
				String filePath=dataPath+"/"+fileName;
				vMatch.add(wikiTerm+","+tableName);
				if(ne.existInDir(fileName,dataPath))
					System.out.println(wikiTerm+"页面中的"+tableName+"表已经存在……");
				else
				{
					System.out.println(wikiTerm+"页面中的"+tableName+"表内容如下：");
					Vector<String> v=hm.get(tableName);
					SetUtil.writeSetToFile(v, filePath);
					for(String temp:v){
						System.out.println(temp);
					}
				}
			}
			SetUtil.appendSetToFile(vMatch, matchRelationFile);
		}
	}
	
	/**
	 * 
	 * @param wikiTerm
	 * @return 从网络上抽取指定wiki术语的navbox
	 */
	public static HashMap<String, Vector<String>> extractNavBox(String wikiTerm){
		NavboxExtractor ne = new NavboxExtractor();
		String url="http://en.wikipedia.org/wiki/"+wikiTerm;
		String s=WebCrawler.getPageStringFromWeb(url);
		try {
			s=new String(s.getBytes("gbk"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		HashMap<String, Vector<String>> hm=ne.extractNavboxFromString(s);
		return hm;
	}

	/**
	 * 
	 * @param s
	 *            要传入的html网页内容，下面函数一样
	 * @return 要抽取网页的表格标题和对应的关系Vector<String>,里面是四元组
	 */
	public HashMap<String, Vector<String>> extractNavboxFromString(String s) {
		//
		HashMap<String, Vector<String>> hmResult = new HashMap<String, Vector<String>>();
		Vector<Integer> vNavboxPos = findNavbox(s);
		int boxBegin = 0, boxEnd = 0;
		for (int i = 0; i < vNavboxPos.size(); i++) {// navbox
			boxBegin = vNavboxPos.get(i);
			boxEnd = getBoxEnd(s, boxBegin);// 取得navbox边界
			String boxTitle[] = getBoxTitle(s, boxBegin, boxEnd);
			Vector<String> vBoxResult = generateBoxRecord(s, boxBegin, boxEnd);
			hmResult.put(boxTitle[0], vBoxResult);
		}// end for navbox
		return hmResult;
	}

	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 抽取s中从begin到end的表格关系
	 */
	public Vector<String> generateBoxRecord(String s, int begin, int end) {
		Vector<String> vResult = new Vector<String>();
		String boxTitle[] = getBoxTitle(s, begin, end);
		Vector<String[]> vGroupTerm = getGroupTerm(s, begin, end);
		// 产生第一层记录
		for (int j = 0; j < vGroupTerm.size(); j++) {
			String groupUnit[] = vGroupTerm.get(j);
			String record = boxTitle[0] + "," + groupUnit[0] + ","
					+ boxTitle[1] + "," + groupUnit[1];
			vResult.add(record);
		}
		// 产生groupTerm对应的记录
		for (int j = 0; j < vGroupTerm.size(); j++) {
			String groupUnit[] = vGroupTerm.get(j);
			vResult.addAll(generateGroupRecord(s, groupUnit));
		}
		return vResult;
	}

	/**
	 * 
	 * @param s
	 * @param groupUnit
	 *            四元组：groupName,groupWikiTag,groupBegin,groupEnd
	 * @return 抽取指定group里面的关系
	 */
	public Vector<String> generateGroupRecord(String s, String groupUnit[]) {
		Vector<String> vResult = new Vector<String>();
		String groupTerm = groupUnit[0];
		String groupTermWikiTag = groupUnit[1];
		int groupBegin = Integer.valueOf(groupUnit[2]);
		int groupEnd = Integer.valueOf(groupUnit[3]);
		Vector<int[]> vsubBox = findSubBox(s, groupBegin, groupEnd);
		Vector<int[]> vsubGroup = findSubGroup(s, groupBegin, groupEnd);
		Vector<int[]> vunderSubBox = findUnderSubBox(s, groupBegin, groupEnd);
		if (vsubBox.size() != 0) {
			for (int i = 0; i < vsubBox.size(); i++) {
				int subBoxBegin = vsubBox.get(i)[0];
				int subBoxEnd = vsubBox.get(i)[1];
				String[] vsubBoxTitle = getBoxTitle(s, subBoxBegin, subBoxEnd);
				String record = groupTerm + "," + vsubBoxTitle[0] + ","
						+ groupTermWikiTag + "," + vsubBoxTitle[1];
				vResult.add(record);
			}
			for (int i = 0; i < vsubBox.size(); i++) {
				int subBoxBegin = vsubBox.get(i)[0] + 5;
				int subBoxEnd = vsubBox.get(i)[1];
				vResult.addAll(generateBoxRecord(s, subBoxBegin, subBoxEnd));
			}
		}// group包含subbox
		else if (vunderSubBox.size() != 0) {
			for (int i = 0; i < vunderSubBox.size(); i++) {
				int undersubBoxBegin = vunderSubBox.get(i)[0] + 5;
				int undersubBoxEnd = vunderSubBox.get(i)[1];
				vResult.addAll(generateBoxRecord(s, undersubBoxBegin,
						undersubBoxEnd));
			}
		}// group包含undersubbox
		else if (vsubGroup.size() != 0) {
			for (int i = 0; i < vsubGroup.size(); i++) {
				int subGroupBegin = vsubGroup.get(i)[0] + 5;
				int subGroupEnd = vsubGroup.get(i)[1];
				Vector<String[]> vsubGroupTerm = getGroupTerm(s, subGroupBegin,
						subGroupEnd);
				for (int j = 0; j < vsubGroupTerm.size(); j++) {
					String subgroupUnit[] = vsubGroupTerm.get(j);
					String record = groupTerm + "," + subgroupUnit[0] + ","
							+ groupTermWikiTag + "," + subgroupUnit[1];
					vResult.add(record);
				}
			}
			for (int i = 0; i < vsubGroup.size(); i++) {
				int subGroupBegin = vsubGroup.get(i)[0] + 5;
				int subGroupEnd = vsubGroup.get(i)[1];
				Vector<String[]> vsubGroupTerm = getGroupTerm(s, subGroupBegin,
						subGroupEnd);
				for (int j = 0; j < vsubGroupTerm.size(); j++) {
					vResult.addAll(generateGroupRecord(s, vsubGroupTerm.get(j)));
				}
			}
		}// group包含subgroup
		else {
			Vector<String> vTerm = extractTermFromGroup(s, groupBegin, groupEnd);
			for (int i = 0; i < vTerm.size(); i++) {
				String record = groupTerm + "," + vTerm.get(i) + ","
						+ groupTermWikiTag + ",true";
				vResult.add(record);
			}
		}
		return vResult;
	}

	/**
	 * 
	 * @param s
	 * @return 查找所有的navbox位置，并返回所有的位置值
	 */
	public Vector<Integer> findNavbox(String s) {
		Vector<Integer> v = new Vector<Integer>();
		String navboxTag = "<table cellspacing=\"0\" class=\"navbox\"";
		int pos = s.indexOf(navboxTag);
		while (pos != -1) {
			v.add(pos);
			pos = s.indexOf(navboxTag, pos + 1);
		}
		return v;
	}
	

	/**
	 * 
	 * @param s
	 * @return 获得html的标题
	 */
	public String getPageTitle(String s) {
		String titleTag = "<h1 id=\"firstHeading\" class=\"firstHeading\"><span dir=\"auto\">";
		int posA = s.indexOf(titleTag) + titleTag.length();
		int posB = s.indexOf("<", posA);
		String title = s.substring(posA, posB);
		title = title.replace(" ", "_");
		return title;
	}
	

	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 抽取s中begin到end的表格标题，第一个代表标题，第二个代表是不是wiki超链接
	 */
	public String[] getBoxTitle(String s, int begin, int end) {
		String title[] = new String[2];
		String tableTitleTag = "style=\"font-size:110%;\">";
		String wikiTag = "<a href=\"/wiki/";
		String subString = s.substring(begin, end);
		int posA = subString.indexOf(tableTitleTag) + tableTitleTag.length();
		int posB = subString.indexOf("</div>", posA);
		title[0] = subString.substring(posA, posB);
		if (title[0].contains(wikiTag)
				|| title[0].contains("class=\"selflink\""))
			title[1] = "true";
		else
			title[1] = "false";
		title[0] = getWikiTerm(title[0]).get(0)[0];
		return title;
	}
	
	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 获取从begin到end之间右边的groupTerm
	 */

	public Vector<String[]> getGroupTerm(String s, int begin, int end) {
		Vector<String[]> vGroup = new Vector<String[]>();
		Vector<int[]> vsubBox = findSubBox(s, begin, end);
		Vector<int[]> vsubGroup = findSubGroup(s, begin, end);
		Vector<int[]> vunderSubBox = findUnderSubBox(s, begin, end);
		String subgroupTag = "class=\"nowraplinks collapsible collapsed navbox-subgroup\"";
		boolean flag = s.substring(begin, end).contains(subgroupTag);// 横subbox特殊处理标签
		if (vunderSubBox.size() != 0) {
			for (int i = 0; i < vunderSubBox.size(); i++) {
				int underSubBoxBegin = vunderSubBox.get(i)[0];
				int underSubBoxEnd = vunderSubBox.get(i)[1];
				String[] vunderSubBoxTitle = getBoxTitle(s, underSubBoxBegin,
						underSubBoxEnd);
				String groupUnit[] = new String[4];// group四元组
				groupUnit[0] = vunderSubBoxTitle[0];// 术语
				groupUnit[1] = vunderSubBoxTitle[1];// 是否是wiki词条
				groupUnit[2] = String.valueOf(underSubBoxBegin);// 本group开始位置
				groupUnit[3] = String.valueOf(underSubBoxEnd);// 本group结束位置
				vGroup.add(groupUnit);
			}
		}// 表示有横着的subbox
		else {
			String groupTag = "<th scope=\"row\" class=\"navbox-group\"";
			int groupBegin = 0, groupEnd = 0;
			int posTermEnd = 0, posNextGroup = 0;
			String groupStr = "";
			int posGroup = s.indexOf(groupTag, begin);
			while (posGroup != -1 && posGroup < end) {
				if (flag)
					while (existInAmong(posGroup, vsubBox)) {
						posGroup = s.indexOf(groupTag, posGroup + 1);
					}
				else
					while (existInAmong(posGroup, vsubBox)
							|| existInAmong(posGroup, vsubGroup)) {
						posGroup = s.indexOf(groupTag, posGroup + 1);
					}
				posTermEnd = s.indexOf("</th>", posGroup);
				groupStr = s
						.substring(posGroup + groupTag.length(), posTermEnd);
				posNextGroup = s
						.indexOf(groupTag, posGroup + groupTag.length());
				if (flag)
					while (existInAmong(posNextGroup, vsubBox)) {
						posNextGroup = s.indexOf(groupTag, posNextGroup + 1);
					}
				else
					while (existInAmong(posNextGroup, vsubBox)
							|| existInAmong(posNextGroup, vsubGroup)) {
						posNextGroup = s.indexOf(groupTag, posNextGroup + 1);
					}
				Vector<String[]> vGroupTerm = getWikiTerm(groupStr);
				groupBegin = posGroup + groupTag.length();// 本group的标志结束位置
				if (posNextGroup == -1 || posNextGroup > end)
					groupEnd = end;
				else
					groupEnd = posNextGroup;// 下一个group的标志开始位置
				for (int i = 0; i < vGroupTerm.size(); i++) {
					String groupUnit[] = new String[4];// group四元组
					String wikiTerm[] = vGroupTerm.get(i);
					groupUnit[0] = wikiTerm[0];// 术语
					groupUnit[1] = wikiTerm[1];// 是否是wiki词条
					groupUnit[2] = String.valueOf(groupBegin);// 本group开始位置
					groupUnit[3] = String.valueOf(groupEnd);// 本group结束位置
					vGroup.add(groupUnit);
				}
				posGroup = posNextGroup;
			}
		}// 没有横向的subbox
		return vGroup;
	}
	
	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 抽取begin到end的group中的wikiterm
	 */
	public Vector<String> extractTermFromGroup(String s, int begin, int end) {
		Vector<String> v = new Vector<String>();
		Vector<int[]> vAboveBelow = findAboveBelow(s, begin, end);
		String title = getPageTitle(s);
		String subString = s.substring(begin, end);
		String termTag = "<li>";
		String selflinkTag = "<strong class=\"selflink\">";
		int posA = s.indexOf(termTag, begin);
		int posB = 0;
		String term = "";
		while (posA != -1 && posA < end) {
			posB = s.indexOf("</li>", posA);
			if (existInAmong(posA, vAboveBelow)) {
				posA = s.indexOf(termTag, posB);
			}// 去除最下面的<li>
			else {
				term = s.substring(posA + termTag.length(), posB);
				term = getWikiTerm(term).get(0)[0];
				if (term.contains("#"))
					term = term.substring(0, term.indexOf("#"));
				v.add(term);
				posA = s.indexOf(termTag, posB);
			}
		}
		if (subString.contains(selflinkTag))
			v.add(title);
		return v;
	}

	/**
	 * 
	 * @param s
	 * @return 获取s里面的wiki术语
	 */
	public Vector<String[]> getWikiTerm(String s) {
		Vector<String[]> v = new Vector<String[]>();
		String wikiTag = "a href=\"/wiki/";
		if (!s.contains(wikiTag)) {
			int posA = s.indexOf("<");
			int posB = 0;
			while (posA != -1) {
				posB = s.indexOf(">", posA);
				s = s.substring(0, posA) + s.substring(posB + 1, s.length());
				posA = s.indexOf("<");
			}
			posA = s.indexOf(">");
			s = s.substring(posA + 1, s.length());
			s = s.replace(" ", "_");
			String temp[] = new String[2];
			temp[0] = s;
			temp[1] = "false";
			v.add(temp);
		} else {
			int posA = s.indexOf(wikiTag);
			int posB = 0;
			while (posA != -1) {
				posB = s.indexOf("\"", posA + wikiTag.length());
				String temp[] = new String[2];
				temp[0] = s.substring(posA + wikiTag.length(), posB);
				if (temp[0].contains("#"))
					temp[0] = temp[0].substring(0, temp[0].indexOf("#"));
				temp[0] = temp[0].replace("%E2%80%93", "–");
				temp[1] = "true";
				v.add(temp);
				posA = s.indexOf(wikiTag, posB);
			}
		}
		return v;
	}
	
	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 找到指定区间的subbox，返回一对一对的位置
	 */
	public Vector<int[]> findSubBox(String s, int begin, int end) {
		Vector<int[]> v = new Vector<int[]>();
		String subboxTag = "<table cellspacing=\"0\" class=\"nowraplinks collapsible autocollapse navbox-subgroup\"";
		int posA = s.indexOf(subboxTag, begin);
		while (posA != -1 && posA < end) {
			int posB = getBoxEnd(s, posA);
			int pos[] = new int[2];
			pos[0] = posA;
			pos[1] = posB;
			v.add(pos);
			posA = s.indexOf(subboxTag, posB);
		}
		return v;
	}

	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 找到指定区间的SubGroup，返回一对一对的位置
	 */
	public Vector<int[]> findSubGroup(String s, int begin, int end) {
		Vector<int[]> v = new Vector<int[]>();
		String subgroupTag = "<table cellspacing=\"0\" class=\"nowraplinks navbox-subgroup\"";
		int posA = s.indexOf(subgroupTag, begin);
		while (posA != -1 && posA < end) {
			int posB = getBoxEnd(s, posA);
			int pos[] = new int[2];
			pos[0] = posA;
			pos[1] = posB;
			v.add(pos);
			posA = s.indexOf(subgroupTag, posB);
		}
		return v;
	}

	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 找到指定区间的UnderSubBox（如Data_Mining中的第一个表格），返回一对一对的位置
	 */
	public Vector<int[]> findUnderSubBox(String s, int begin, int end) {
		Vector<int[]> v = new Vector<int[]>();
		String undersubboxTag = "<table cellspacing=\"0\" class=\"nowraplinks collapsible collapsed navbox-subgroup\"";
		int posA = s.indexOf(undersubboxTag, begin);
		while (posA != -1 && posA < end) {
			int posB = getBoxEnd(s, posA);
			int pos[] = new int[2];
			pos[0] = posA;
			pos[1] = posB;
			v.add(pos);
			posA = s.indexOf(undersubboxTag, posB);
		}
		return v;
	}

	/**
	 * 
	 * @param s
	 * @param begin
	 * @param end
	 * @return 找到指定区间的表格附属信息，返回一对一对的位置
	 */
	public Vector<int[]> findAboveBelow(String s, int begin, int end) {
		Vector<int[]> v = new Vector<int[]>();
		String abovebelowTag = "<td class=\"navbox-abovebelow\"";
		int posA = s.indexOf(abovebelowTag, begin);
		while (posA != -1 && posA < end) {
			int posB = s.indexOf("</td>", posA);
			int pos[] = new int[2];
			pos[0] = posA;
			pos[1] = posB;
			v.add(pos);
			posA = s.indexOf(abovebelowTag, posB);
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

	/**
	 * 
	 * @param s
	 * @param boxBegin
	 * @return 获取指定开头box的结尾位置
	 */
	public int getBoxEnd(String s, int boxBegin) {
		int boxEnd = 0;
		int posA = s.indexOf("</table>", boxBegin + 5);
		int posB = s.indexOf("<table", boxBegin + 5);
		if (posB == -1)
			boxEnd = posA;
		else if (posB > posA)
			boxEnd = posA;
		else {
			int number = 2;
			while (number != 0) {
				posA = s.indexOf("</table>", posB + 5);
				posB = s.indexOf("<table", posB + 5);
				if (posB == -1 || posB > posA) {
					number--;
					posB = posA;
				} else {
					number++;
				}
			}
		}
		boxEnd = posA + 8;
		return boxEnd;
	}

	/**
	 * 
	 * @param fileName
	 * @param dir
	 * @return 判断文件名是否在指定目录中存在
	 */
    public boolean existInDir(String fileName,String dir){
    	File f=new File(dir);
    	File child[]=f.listFiles();
    	for(int i=0;i<child.length;i++){
    		String name=child[i].getName();
    		if(name.equals(fileName))return true;
    		else continue;
    	}
    	return false;
    }
}
