package function.hypRelation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import function.crawler.WikiCategoryCrawler;
import function.linkAnalysis.NavboxExtractor;
import function.linkAnalysis.URLtoExcel;
import function.linkAnalysis.redirectProcess.RedirectProcess;
import function.netAnalysis.motif.Motif;
import function.util.DownloadFile;
import function.util.ExcelUtil;
import function.util.FileUtil;
import function.util.MapUtil;
import function.util.SetUtil;

/**
 * 
 * @author MJ
 * @description 给定html文件夹抽取上下位，分为三个流程
 */
public class HypRelationExtractor {

	public Vector<String> vGraphRoot = new Vector<String>();// navbox和category构成的图的根
	private double processId = 0;// 流程控制标签
	private String htmlPath = "";
	public String desDir = "";
	private String rawXlsPath = "";
	public String newRelationXlsFileName = "";
	public Motif motif = new Motif();
	private String processIdFile = "";
	private String processFile = "";// 已经执行的流程文件
	private Vector<String> vProcess = new Vector<String>();// 已经执行的流程
	private String categoryException = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String htmlPath = "F:\\DOFT-data\\hyperExtraction\\DS\\DS_html";
		HypRelationExtractor hre = new HypRelationExtractor(htmlPath);
		hre.motifCompute();
		hre.annotation();
		hre.hyponymExtraction();
	}

	public HypRelationExtractor(String htmlPath) {
		this.htmlPath = htmlPath;
		desDir = htmlPath + "-hypRelation";
		File fDesDir = new File(desDir);
		fDesDir.mkdirs();
		rawXlsPath = desDir + "/relation-raw.xls";
		newRelationXlsFileName = desDir + "/relation.xls";
		processIdFile = desDir + "/processId.txt";
		File f = new File(processIdFile);
		if (!f.exists())
			FileUtil.writeStringFile("0", processIdFile);
		
		categoryException = getClass().getResource(
				"/resources/exception/category-exception.txt").getPath();
		categoryException=categoryException.replace("%20", " ");
		categoryException=categoryException.substring(1, categoryException.length());
		processFile=desDir + "/process.txt";
		File fProcessTxt = new File(processFile);
		if (!fProcessTxt.exists())
			try {
				fProcessTxt.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public boolean isFinishedProcess(String process) {
		vProcess = SetUtil.readSetFromFile(processFile);
		if (vProcess.contains(process))
			return true;
		else
			return false;
	}

	public void addProcess(String process) {
		vProcess = SetUtil.readSetFromFile(processFile);
		if (!vProcess.contains(process)) {
			vProcess.add(process);
			SetUtil.writeSetToFile(vProcess, processFile);
		}
		String id = String.valueOf(processId);
		FileUtil.writeStringFile(id, processIdFile);
	}
	
	/**
	 * 计算motif——流程1
	 */
	public void motifCompute() {
		/********* 下载fanmod *********/
		String fanmodPath = "C:\\Program Files\\fanmod.exe";
		File f = new File(fanmodPath);
		if (!f.exists()) {
			new DownloadFile().downloadFanmod();
		}
		/********* URLtoExcel *********/
		if (getProcessId() < 0.1) {
			System.out.println("Extracting articles' URL to Excel……");
			if (!isFinishedProcess("Extract URL to Excel")) {
				URLtoExcel utoe = new URLtoExcel();
				utoe.extractNoRepeatUrlToExcel(htmlPath, rawXlsPath);
				addProcess("Extract URL to Excel");
			}
			setProcessId(0.1);
		}
		/********* 去除重定向 *********/
		if (getProcessId() < 0.2) {
			System.out.println("Checking redirect articles……");
			if (!isFinishedProcess("Checking redirect")) {
				RedirectProcess rp = new RedirectProcess(htmlPath, true,
						rawXlsPath, 1);
				rp.run(rp);
				addProcess("Checking redirect");
			}
			setProcessId(0.2);
		}
		if (getProcessId() < 0.3) {
			System.out.println("Replacing redirect articles……");
			RedirectReplace rr = new RedirectReplace(rawXlsPath, 0, 1, 2);
			rr.run(rr);
			setProcessId(0.3);
		}
		/********* 创建新的关系表 *********/
		if (getProcessId() < 0.4) {
			System.out.println("Creating new relation table……");
			generateFinalRelationExcel(rawXlsPath, 2, newRelationXlsFileName);
			setProcessId(0.4);
		}
		/********* 计算Motif *********/
		if (getProcessId() < 0.5) {
			System.out.println("Computing motif……");
			motif.computeMotif3(newRelationXlsFileName);
			setProcessId(1);
		}
		f.delete();
	}
	
	/**
	 * 自动标注——流程2，如果第一步没有完成，该步返回false
	 * 
	 * @param htmlPath
	 */
	public boolean annotation() {
		if (getProcessId() < 1) {
			return false;// motif没有计算完毕
		} else {
			/********* 抽取NAVBOX *********/
			System.out.println("正在抽取Navbox……");

			String navboxPath = desDir + "/navbox";
			NavboxExtractor.extractNavBox(htmlPath, navboxPath);

			/********* 统计superCategory *********/
			System.out.println("正在统计superCategory……");
			String catDir = desDir + "/category";
			File fCatDir = new File(catDir);
			fCatDir.mkdirs();
			String categoryXlsPath = catDir + "/category.xls";
			ExtractCategory ec = new ExtractCategory(htmlPath, categoryXlsPath);
			ec.run(ec);
			/********* 选择根并爬取目录 *********/
			System.out.println("正在爬取目录……");
			Vector<String> vCatResult = getCatRoot(categoryXlsPath);
			String catPath = catDir + "/root.txt";
			SetUtil.writeSetToFile(vCatResult, catPath);
			WikiCategoryCrawler wcc = new WikiCategoryCrawler();
			wcc.buildCategoryTree(catPath, 4);
			/********* 标注数据 *********/
			System.out.println("正在标注数据……");
			HashMap<String, String> hmRedirect = new HashMap<String, String>();
			String columnNames[] = new String[2];
			columnNames[0] = "term";
			columnNames[1] = "title";
			Vector<String[]> vRedirect = ExcelUtil.readSetFromExcel(rawXlsPath,
					1, columnNames);
			for (int i = 0; i < vRedirect.size(); i++) {
				String record[] = vRedirect.get(i);
				hmRedirect.put(record[0], record[1]);
			}// 重定向表
			TermGraph tg = new TermGraph();
			String navboxDataPath = navboxPath + "/data";
			tg = addEdge(tg, navboxDataPath, hmRedirect);
			String categoryDataPath = desDir + "/category/category_data";
			tg = addEdge(tg, categoryDataPath, hmRedirect);
			Annotator at = new Annotator(newRelationXlsFileName, 0, tg,
					vGraphRoot);
			at.run(at);
			setProcessId(2);
			return true;
		}
	}

	/**
	 * 抽取上下位——流程3，如果前两步没有完成，这步操作返回false
	 * 
	 * @param htmlPath
	 */
	public boolean hyponymExtraction() {
		if (getProcessId() < 2) {
			return false;// 标注没有计算完毕
		} else {
			/********* 训练测试 *********/
			System.out.println("正在产生训练集和测试集……");
			String trainFileName = desDir + "/train.csv";
			String testFileName = desDir + "/test.csv";
			generateTrainTestFile(newRelationXlsFileName, trainFileName,
					testFileName);
			System.out.println("正在训练测试……");
			WekaTrainTest wtt = new WekaTrainTest();
			HashMap<Integer, String> hmPredict = wtt.trainTest(trainFileName,
					testFileName, "weka.classifiers.trees.RandomForest");
			AddPredirectRelation gfr = new AddPredirectRelation(
					newRelationXlsFileName, hmPredict);
			gfr.run(gfr);
			SymDeliverDetect sdd = new SymDeliverDetect(newRelationXlsFileName,
					1);
			sdd.run(sdd);
			RectifyRelation rr=new RectifyRelation(newRelationXlsFileName, 1, "checkRelation");
			rr.run(rr);
			generateFinalRelation(newRelationXlsFileName, 1);
			setProcessId(3);
			System.out.println("上下位抽取完毕！");
			return true;
		}
	}

	/**
	 * 根据根统计表返回需要爬取的根
	 * 
	 * @param xlsFileName
	 * @return
	 */
	private Vector<String> getCatRoot(String xlsFileName) {
		Vector<String> vCatResult = new Vector<String>();// 最终选出的根存放结果
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		String columnNames[] = new String[2];
		columnNames[0] = "category";
		columnNames[1] = "frequency";
		Vector<String[]> vCat = ExcelUtil.readSetFromExcel(xlsFileName, 2,
				columnNames);
		for (int i = 0; i < vCat.size(); i++) {
			String record[] = vCat.get(i);
			hm.put(record[0],
					Integer.valueOf(record[1].substring(0,
							record[1].indexOf("."))));
		}
		Vector<String[]> v = MapUtil.sortMapValueDes(hm);
		Vector<String> vCatException = SetUtil
				.readSetFromFile(categoryException);
		for (int i = 0; i < 6; i++) {// 6是根的数量
			String root = v.get(i)[0];
			boolean addTag = true;
			for (String exception : vCatException) {
				if (root.contains(exception)) {
					addTag = false;
					break;
				}
			}
			if (addTag)
				vCatResult.add(root);
			System.out.println("root:" + root);
		}
		return vCatResult;
	}

	/**
	 * 添加边，顶点需要按照指定的重定向来替换
	 * 
	 * @param dataDir
	 * @param hmRedirect
	 * @return
	 */
	private TermGraph addEdge(TermGraph tg, String dataDir,
			HashMap<String, String> hmRedirect) {
		File f = new File(dataDir);
		File childs[] = f.listFiles();
		for (int i = 0; i < childs.length; i++) {
			String path = childs[i].getAbsolutePath();
			System.out.println("添加边文件：" + path);
			Vector<String> vTemp = SetUtil.readSetFromFile(path);
			for (int j = 0; j < vTemp.size(); j++) {
				String record[] = vTemp.get(j).split(",");
				if (hmRedirect.containsKey(record[0]))
					record[0] = hmRedirect.get(record[0]);
				if (hmRedirect.containsKey(record[1]))
					record[1] = hmRedirect.get(record[1]);
				tg.addEdge(record[0], record[1]);
				if (j == 0)
					vGraphRoot.add(record[0]);// 把根保存下来
			}
		}
		return tg;
	}

	/**
	 * 把relation-raw中的关系sheet单独提出来
	 * 
	 * @param xlsFileName
	 * @param sheetID
	 * @param desXlsFileName
	 */
	private void generateFinalRelationExcel(String xlsFileName, int sheetID,
			String desXlsFileName) {
		String ColumnNames[] = new String[2];
		ColumnNames[0] = "sourceURLName";
		ColumnNames[1] = "toURLName";
		Vector<String[]> vRecord = ExcelUtil.readSetFromExcel(xlsFileName,
				sheetID, ColumnNames);
		Vector<Vector<Serializable>> vRecordWithId = new Vector<Vector<Serializable>>();
		for (int i = 0; i < vRecord.size(); i++) {
			Vector<Serializable> recordWithId = new Vector<Serializable>();
			String record[] = vRecord.get(i);
			recordWithId.add(String.valueOf(i + 1));
			recordWithId.add(record[0]);
			recordWithId.add(record[1]);
			vRecordWithId.add(recordWithId);
		}
		String ColumnNamesWithId[] = new String[3];
		ColumnNamesWithId[0] = "id";
		ColumnNamesWithId[1] = ColumnNames[0];
		ColumnNamesWithId[2] = ColumnNames[1];
		ExcelUtil.writeSetToExcel(vRecordWithId, desXlsFileName, 0,
				ColumnNamesWithId);
	}

	/**
	 * 根据给定关系表产生训练集和测试集
	 * 
	 * @param xlsFileName
	 * @param trainFileName
	 * @param testFileName
	 */
	private void generateTrainTestFile(String xlsFileName,
			String trainFileName, String testFileName) {
		String ColumnNames[] = new String[15];
		ColumnNames[0] = "id";
		ColumnNames[1] = "m000000110";
		ColumnNames[2] = "m000001100";
		ColumnNames[3] = "m000001110";
		ColumnNames[4] = "m000100100";
		ColumnNames[5] = "m000100110";
		ColumnNames[6] = "m000101110";
		ColumnNames[7] = "m001001110";
		ColumnNames[8] = "m001100110";
		ColumnNames[9] = "m010001100";
		ColumnNames[10] = "m010100100";
		ColumnNames[11] = "m010100110";
		ColumnNames[12] = "m010101110";
		ColumnNames[13] = "m011101110";
		ColumnNames[14] = "relation";
		Vector<String[]> vRecord = ExcelUtil.readSetFromExcel(xlsFileName, 0,
				ColumnNames);
		Vector<String> vTrain = new Vector<String>();// 存放训练数据
		Vector<String> vTest = new Vector<String>();// 存放测试数据
		String title = "";
		for (int i = 0; i < ColumnNames.length; i++) {
			if (i < ColumnNames.length - 1)
				title = title + ColumnNames[i] + ",";
			else
				title = title + ColumnNames[i];
		}
		vTrain.add(title);
		vTest.add(title);
		String relation[] = new String[3];
		relation[0] = "A is a B";
		relation[1] = "B is a A";
		relation[2] = "Other";
		Random rd = new Random();
		for (int i = 0; i < vRecord.size(); i++) {
			String record[] = vRecord.get(i);
			if (!record[14].equals("noExist")) {
				String trainRecord = "";
				for (int j = 0; j < ColumnNames.length; j++) {
					if (j < ColumnNames.length - 1)
						trainRecord = trainRecord + record[j] + ",";
					else
						trainRecord = trainRecord + record[j];
				}
				vTrain.add(trainRecord);
			} else {
				String testRecord = "";
				for (int j = 0; j < ColumnNames.length - 1; j++) {
					testRecord = testRecord + record[j] + ",";
				}
				testRecord = testRecord + relation[Math.abs(rd.nextInt() % 3)];
				vTest.add(testRecord);
			}
		}// end for
		SetUtil.writeSetToFile(vTrain, trainFileName);
		SetUtil.writeSetToFile(vTest, testFileName);
	}

	/**
	 * 根据给定的关系文件产生csv关系文件
	 * 
	 * @param xlsFileName
	 * @param sheetId
	 */
	private void generateFinalRelation(String xlsFileName, int sheetId) {
		String fileName = xlsFileName.replace(".xls", ".csv");
		Vector<String> v = new Vector<String>();
		String title = "sourceURLName,toURLName,checkRelation";
		v.add(title);
		String columns[] = new String[3];
		columns[0] = "sourceURLName";
		columns[1] = "toURLName";
		columns[2] = "checkRelation";
		Vector<String[]> vRecord = ExcelUtil.readSetFromExcel(xlsFileName,
				sheetId, columns);
		for (int i = 0; i < vRecord.size(); i++) {
			String record[] = vRecord.get(i);
			record[0] = record[0].replace(",", "@");
			record[1] = record[1].replace(",", "@");
			v.add(record[0] + "," + record[1] + "," + record[2]);
		}
		SetUtil.writeSetToFile(v, fileName);
	}
}
