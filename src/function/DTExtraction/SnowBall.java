package function.DTExtraction;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import function.util.SetUtil;

public class SnowBall {

	/**
	 * @param args
	 */
	private String initialDir = "F:\\Data\\testData\\init";
	private String candidateDir = "F:\\Data\\testData\\candidate";
	private HashMap<String, Vector<String>> hm = new HashMap<String, Vector<String>>();
	private Vector<String> vInitial = new Vector<String>();
	private Vector<String> vCandidate = new Vector<String>();
	private String termSetPath = "F:\\FacetedTaxonomy\\Data_mining\\DM_termset.txt";
	private Vector<String> vTerm = new Vector<String>();
	private int termSize = 0;
	private HashMap<String,Integer> hmFsFrequency=new HashMap<String, Integer>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SnowBall sb = new SnowBall();
		sb.init();
		sb.snowing();
	}

	public void snowing() {
		while (vInitial.size() < 500) {
			int importance = 0;
			int maxId = 0;
			String maxTerm = "";
			for (int i = 0; i < vCandidate.size(); i++) {
				String term = vCandidate.get(i);
				int tempImportance = computeFsImportance(hm.get(term));
				if (tempImportance > importance) {
					importance = tempImportance;
					maxId = i;
				}
			}
			maxTerm = vCandidate.get(maxId);
			vInitial.add(maxTerm);
			vCandidate.removeElementAt(maxId);
			System.out.println("addTerm:" + maxTerm);
			if (vTerm.contains(maxTerm))
				termSize++;
			System.out.println("termSize:" + termSize + "/" + vInitial.size());
		}
	}

	/**
	 * 初始化
	 */
	public void init() {
		WikiHrefProcess whp=new WikiHrefProcess();
		vTerm = SetUtil.readSetFromFile(termSetPath);
		File f = new File(initialDir);
		File childs[] = f.listFiles();
		for (int i = 0; i < childs.length; i++) {
			String term = childs[i].getName().replace(".html", "");
			String filePath = childs[i].getAbsolutePath();
			Vector<String> vWikiTerm = whp.getFirstSentenceWikiTerm(filePath);
			hm.put(term, vWikiTerm);
			adjustFrequency(vWikiTerm);
			vInitial.add(term);
		}
		termSize = vInitial.size();
		System.out.println("初始term：" + termSize);
		File fC = new File(candidateDir);
		File childsC[] = fC.listFiles();
		for (int i = 0; i < childsC.length; i++) {
			String term = childsC[i].getName().replace(".html", "");
			String filePath = childsC[i].getAbsolutePath();
			Vector<String> vWikiTerm = whp.getFirstSentenceWikiTerm(filePath);
			hm.put(term, vWikiTerm);
			vCandidate.add(term);
		}
	}

	/**
	 * 计算连通度
	 * 
	 * @param vCandidateTerm
	 * @return
	 */
	public int computeConnectivity(Vector<String> vCandidateTerm) {
		int connectivity = 0;
		for (int i = 0; i < vCandidateTerm.size(); i++) {
			String term = vCandidateTerm.get(i);
			Vector<String> vWikiTerm = hm.get(term);
			for (String wikiTerm : vWikiTerm) {
				if (vCandidateTerm.contains(wikiTerm))
					connectivity++;
			}
		}
		return connectivity;
	}
	
	/**
	 * 根据给定术语调整首句term频率
	 * @param vFsTerm
	 */
	public void adjustFrequency(Vector<String> vFsTerm){
		for(String term:vFsTerm){
			if(hmFsFrequency.containsKey(term)){
				int frequency=hmFsFrequency.get(term)+1;
				hmFsFrequency.put(term, frequency);
			}
			else
				hmFsFrequency.put(term, 1);
		}
	}
	
	/**
	 * 根据首句术语计算重要性
	 * @param vFsTerm
	 * @return
	 */
	public int computeFsImportance(Vector<String> vFsTerm){
		int importance=0;
		for(String term:vFsTerm){
			if(hmFsFrequency.containsKey(term))
				importance+=hmFsFrequency.get(term);
		}
		return importance;
	}

	/**
	 * 获取指定字符串内的维基词条
	 * 
	 * @param filePath
	 * @return
	 */
	public Vector<String> getWikiTermFromStr(String s) {
		Vector<String> vTerm = new Vector<String>();
		String wikiTag = "a href=\"/wiki/";
		int posA = s.indexOf(wikiTag);
		int posB = 0;
		while (posA != -1) {
			posB = s.indexOf("\"", posA + wikiTag.length());
			String temp = s.substring(posA + wikiTag.length(), posB);
			if (temp.contains("#"))
				temp = temp.substring(0, temp.indexOf("#"));
			if (!temp.contains(":") && !temp.equals("Main_Page")
					&& !temp.contains(",") && !vTerm.contains(temp))
				vTerm.add(temp);
			posA = s.indexOf(wikiTag, posB);
		}
		return vTerm;
	}
	
}
