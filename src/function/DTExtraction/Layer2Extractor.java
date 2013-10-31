package function.DTExtraction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import function.crawler.WebCrawler;
import function.hypRelation.ExtractCategory;
import function.linkAnalysis.redirectProcess.RedirectProcess;
import function.util.ExcelUtil;
import function.util.FileUtil;
import function.util.SetUtil;

public class Layer2Extractor {

	private String DTPath = "";// 领域术语存放路径
	private String domainName = "";// 领域名称
	// html
	private String htmlPath = "";// html存放路径
	private String layer1SelectHtmlPath = "";// 第1层选择的页面路径
	private String layer2RawHtmlPath = "";// 第2层页面初始路径
	private String layer2HtmlPath = "";// 第2层去除重定向后的页面路径
	private String layer2SelectHtmlPath = "";// 第二层选择的页面路径

	// process
	private String processPath = "";// 处理过程文件存放路径
	private double processId = 0;// 流程控制标签
	private String processIdFile = "";// 流程控制文件路径
	private String processFile = "";// 已经执行的流程文件
	private Vector<String> vProcess = new Vector<String>();// 已经执行的流程
	private String layer1SelectListPath = "";// 第一层最终选择术语列表路径
	private String layer0ListPath = "";// 第0层术语列表
	private String layer1HrefPath = "";// 第1层页面链接文件路径
	private String layer2ListPath = "";// 第2层页面列表文件路径
	private String layer2CatFeatureFile = "";// 第2层目录特征文件
	private String layer2FSFeatureFile = "";// 第2层首句特征文件
	private String layer2File = "";// 第2层术语特征文件
	private String layer2SelectListPath = "";// 第2层根据领域术语选择的列表路径
	private String layer1ListPath = "";// 第一层列表
	// exception
	private String domainExceptionPath = "";// 领域例外文件
	private String fsExceptionPath = "";// 首句例外文件
	private String termExceptionPath = "";// 术语例外文件
	private String personOrganizationListPath = "";// 人名组织例外列表
	private String nonPersonOrganizationListPath = "";// 非人名组织例外列表

	// other
	private int sum = 0;// 总数
	private int currentSize = 0;// 扫描的爬取文件个数

	// gui
	public String guiPath = "";// 前台显示文件夹
	public String layer2CrawlHtmlConf = "";// 第2层爬取的网页配置路径
	public String layer2CategoryDetailPath = "";// 第2层目录特征详细路径
	public String layer2FSDetailPath = "";// 第2层FS特征详细路径
	public String layer2SelectConf = "";// 第一层选择的网页配置路径
	public String chartConf = "";// chart配置路径
	public String btnConf = "";// btn配置路径

	public static void main(String[] args) {
		Layer2Extractor extractor = new Layer2Extractor("Computer_network",
				"F:\\DOFT-data\\DTExtraction");
		extractor.extract();
	}

	public Layer2Extractor(String domainName, String savePath) {
		this.domainName = domainName;
		this.DTPath = savePath + "/" + this.domainName;
		init();
	}

	public void init() {
		// html
		htmlPath = DTPath + "/html";
		// process
		processPath = DTPath + "/process";
		// 流程初始化
		processIdFile = processPath + "/processId.txt";
		processFile = processPath + "/process.txt";
		File fProcessId = new File(processIdFile);
		if (!fProcessId.exists())
			FileUtil.writeStringFile("1", processIdFile);
		File fProcessTxt = new File(processFile);
		if (!fProcessTxt.exists())
			try {
				fProcessTxt.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// exception
		domainExceptionPath = getClass().getResource(
				"/resources/exception/domainException.txt").getPath();
		domainExceptionPath = domainExceptionPath.replace("%20", " ");
		domainExceptionPath = domainExceptionPath.substring(1,
				domainExceptionPath.length());
		fsExceptionPath = getClass().getResource(
				"/resources/exception/FSException.txt").getPath();
		fsExceptionPath = fsExceptionPath.replace("%20", " ");
		fsExceptionPath = fsExceptionPath
				.substring(1, fsExceptionPath.length());
		termExceptionPath = getClass().getResource(
				"/resources/exception/termException.txt").getPath();
		termExceptionPath = termExceptionPath.replace("%20", " ");
		termExceptionPath = termExceptionPath.substring(1,
				termExceptionPath.length());
		personOrganizationListPath = getClass().getResource(
				"/resources/exception/personOrganizationList.txt").getPath();
		personOrganizationListPath = personOrganizationListPath.replace("%20",
				" ");
		personOrganizationListPath = personOrganizationListPath.substring(1,
				personOrganizationListPath.length());
		nonPersonOrganizationListPath = getClass().getResource(
				"/resources/exception/nonPersonOrganizationList.txt").getPath();
		nonPersonOrganizationListPath = nonPersonOrganizationListPath.replace(
				"%20", " ");
		nonPersonOrganizationListPath = nonPersonOrganizationListPath
				.substring(1, nonPersonOrganizationListPath.length());
		// gui
		guiPath = DTPath + "/gui";
		layer2CrawlHtmlConf = guiPath + "/layer2CrawlHtmlConf.txt";
		layer2CategoryDetailPath = guiPath + "/layer2-categoryDetail.txt";
		layer2FSDetailPath = guiPath + "/layer2-FSDetail.txt";
		layer2SelectConf = guiPath + "/layer2SelectConf.txt";
		chartConf = guiPath + "/chartConf.txt";
		btnConf = guiPath + "/btnConf.txt";
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

	Timer timer;

	/**
	 * 抽取方法
	 */
	public void extract() {
		final Layer2ExtractorThread let = new Layer2ExtractorThread(100, "");
		// 以启动一条线程的方式来执行一个耗时的任务
		final Thread extractThread = new Thread(let);
		extractThread.start();
		// 创建进度对话框
		final ProgressMonitor pm = new ProgressMonitor(null, "",
				let.getCurrentTask(), 0, let.getAmount());
		// 创建一个计时器
		timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 以任务的当前完成量设置进度对话框的完成比例
				pm.setProgress(let.getCurrent());
				pm.setNote(let.getCurrentTask());
				// 如果用户单击了进度对话框的”取消“按钮
				if (pm.isCanceled()) {
					// 停止计时器
					timer.stop();
					// 中断任务的执行线程
					extractThread.interrupt();
				}
				if (pm.getNote().equals("success")) {
					try {
						Thread.sleep(1000);
						timer.stop();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		timer.start();
	}

	// 抽取的线程类
	class Layer2ExtractorThread implements Runnable {
		// 任务的当前完成量
		public volatile int current;
		// 任务的当前提示
		public volatile String currentTask;
		// 总任务量
		public int amount;

		public Layer2ExtractorThread(int amount, String currentTask) {
			current = 0;
			this.amount = amount;
			this.currentTask = currentTask;
		}

		public int getAmount() {
			return amount;
		}

		public int getCurrent() {
			return current;
		}

		public String getCurrentTask() {
			return currentTask;
		}

		public void setCurrent(int current, String currentTask) {
			this.current = current;
			this.currentTask = currentTask;
			System.out.println(currentTask);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 抽取过程
		public void run() {
			WebCrawler wc = new WebCrawler();
			WikiHrefProcess whp = new WikiHrefProcess();
			setProcessId(1);
			// 2.抽取layer1-select超链接
			layer1SelectHtmlPath = htmlPath + "/layer1-select";
			layer1HrefPath = processPath + "/layer1-href.txt";
			layer0ListPath = processPath + "/layer0.txt";
			layer1SelectListPath = processPath + "/layer1-select.txt";
			if (getProcessId() < 1.1) {
				setCurrent(1, "Extracting hyperLink of layer1……");
				Vector<String> vLayer1Href = new Vector<String>();
				vLayer1Href
						.addAll(whp.getWikiTermFromDir(layer1SelectHtmlPath));
				Vector<String> vLayer0Term = SetUtil
						.readSetFromFile(layer0ListPath);
				Vector<String> vLayer1Term = SetUtil
						.readSetFromFile(layer1SelectListPath);
				vLayer1Href = SetUtil.getNoRepeatVectorIgnoreCase(SetUtil
						.getSubSet(vLayer1Href,
								SetUtil.getUnionSet(vLayer0Term, vLayer1Term)));
				sum = vLayer1Href.size();
				SetUtil.writeSetToFile(vLayer1Href, layer1HrefPath);
				setProcessId(1.1);
			}
			sum = SetUtil.readSetFromFile(layer1HrefPath).size();
			// 2.爬取layer2页面
			layer2RawHtmlPath = htmlPath + "/layer2-raw";
			if (getProcessId() < 1.2) {
				FileUtil.writeStringFile(layer2RawHtmlPath + "," + sum,
						layer2CrawlHtmlConf);// 生成前台显示配置文件
				File fLayer2RawHtml = new File(layer2RawHtmlPath);
				fLayer2RawHtml.mkdirs();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 防止文件读取错误
				setCurrent(4, "Crawling layer2's page……");
				Thread tDetect = new Thread(new Runnable() {
					@Override
					public void run() {
						if (ExtractorUtil.scanDirPage(layer2RawHtmlPath).size() >= sum) {
							for (int i = 1; i <= sum; i++) {
								setCurrent(4 + i * 70 / sum,
										"Crawling layer2's page(" + i + "/"
												+ sum + ")……");
							}
						}// 已经爬取完成
						else {
							Vector<String> vExistPage = new Vector<String>();
							while (currentSize < sum) {
								HashMap<String, String> hm = ExtractorUtil
										.scanDirPage(layer2RawHtmlPath);
								Vector<String> vNewAddPage = ExtractorUtil
										.getNewAddPage(vExistPage, hm);
								while (vNewAddPage.size() == 0) {
									hm = ExtractorUtil
											.scanDirPage(layer2RawHtmlPath);
									vNewAddPage = ExtractorUtil.getNewAddPage(
											vExistPage, hm);
								}
								vExistPage.addAll(vNewAddPage);
								currentSize = vExistPage.size();
								setCurrent(4 + currentSize * 70 / sum,
										"Crawling layer2's page(" + currentSize
												+ "/" + sum + ")……");
							}
						}// 未爬取完成
					}
				});
				tDetect.start();
				wc.crawlPageByList(layer1HrefPath, layer2RawHtmlPath, 10);
				while (tDetect.isAlive()) {
				}// 执行完再继续……
				setProcessId(1.2);
			}
			// 3.去除重定向
			layer2HtmlPath = htmlPath + "/layer2";
			layer2ListPath = processPath + "/layer2.txt";
			if (getProcessId() < 1.3) {
				setCurrent(76, "Analysis the redirect……");
				if (!isFinishedProcess("layer2-raw->redirect")) {
					String layer2RedirectXls = processPath
							+ "/layer2-raw-redirect.xls";
					RedirectProcess rp = new RedirectProcess(layer2RawHtmlPath,
							layer2RedirectXls, layer2HtmlPath, true);
					rp.run(rp);
					SetUtil.writeSetToFile(
							FileUtil.getDirFileSet(layer2HtmlPath),
							layer2ListPath);
					addProcess("layer2-raw->redirect");
				}
				setProcessId(1.3);
			}
			// 4.layer2特征抽取
			layer2CatFeatureFile = processPath + "/layer2-cat.xls";
			layer2FSFeatureFile = processPath + "/layer2-fs.xls";
			layer2File = processPath + "/layer2.xls";
			if (getProcessId() < 1.4) {
				setCurrent(82, "Extracting category feature……");
				if (!isFinishedProcess("layer2->extracting category feature")) {
					ExtractCategory ec = new ExtractCategory(layer2HtmlPath,
							layer2CatFeatureFile);
					ec.run(ec);
					setCurrent(85, "Extracting FS feature……");
					addProcess("layer2->extracting category feature");
				}
				if (!isFinishedProcess("layer2->extracting FS feature")) {
					ExtractFirstSentence efs = new ExtractFirstSentence(
							layer2HtmlPath, layer2FSFeatureFile);
					efs.run(efs);
					addProcess("layer2->extracting FS feature");
				}
				setCurrent(88, "Building term feature table……");
				Vector<String> vTerm = ExcelUtil.readSetFromExcel(
						layer2CatFeatureFile, 1, "term");
				Vector<String> vCat = ExcelUtil.readSetFromExcel(
						layer2CatFeatureFile, 1, "category");
				Vector<String> vFS = ExcelUtil.readSetFromExcel(
						layer2FSFeatureFile, 1, "FSWikiTerm");
				ExcelUtil.writeSetToExcel(vTerm, layer2File, 0, "term");
				ExcelUtil.writeSetToExcel(vCat, layer2File, 0, "category");
				ExcelUtil.writeSetToExcel(vFS, layer2File, 0, "FSWikiTerm");
				setProcessId(1.4);
			}
			// 5.去除无关机构组织人名
			if (getProcessId() < 1.5) {
				setCurrent(89, "Removing person-org page……");
				RemoveSpecial rs = new RemoveSpecial(layer2File, 0, 1,
						termExceptionPath, personOrganizationListPath,
						nonPersonOrganizationListPath);
				rs.run(rs);
				setProcessId(1.5);
			}
			// 6.layer1-select特征抽取
			String layer1SelectCatFeatureFile = processPath
					+ "/layer1-select-cat.xls";
			String layer1SelectFSFeatureFile = processPath
					+ "/layer1-select-fs.xls";
			String layer1CatFeatureFile = processPath + "/layer1-cat.xls";
			String layer1FsFeatureFile = processPath + "/layer1-fs.xls";
			if (getProcessId() < 1.6) {
				setCurrent(90, "Extracting layer1-select category……");
				ExtractCategory ec = new ExtractCategory(layer1SelectHtmlPath,
						layer1SelectCatFeatureFile);
				ec.run(ec);
				setCurrent(91, "Extracting layer1-select FS……");
				ExtractFirstSentence efs = new ExtractFirstSentence(
						layer1SelectHtmlPath, layer1SelectFSFeatureFile);
				efs.run(efs);
				setCurrent(92, "Computing layer1-select category ratio……");
				Vector<String> vTerm = ExcelUtil.readSetFromExcel(
						layer1CatFeatureFile, 2, "category");
				Vector<Integer> vFrequency = ExcelUtil.readIntegerSetFromExcel(
						layer1CatFeatureFile, 2, "frequency");
				ExcelUtil.writeSetToExcel(vTerm, layer1SelectCatFeatureFile, 3,
						"category");
				ExcelUtil.writeIntegerSetToExcel(vFrequency,
						layer1SelectCatFeatureFile, 3, "frequencyAll");
				AddFeature af = new AddFeature(layer1SelectCatFeatureFile, 3,
						2, "frequencyAll", "frequencyAll", "Integer");
				af.run(af);
				ComputeFeature cf = new ComputeFeature(
						layer1SelectCatFeatureFile, 2, "frequency",
						"frequencyAll", "/", "ratio");
				cf.run(cf);
				setCurrent(94, "Computing layer1-select FS ratio……");
				Vector<String> vTermFs = ExcelUtil.readSetFromExcel(
						layer1FsFeatureFile, 2, "FSWikiTerm");
				Vector<Integer> vFrequencyFs = ExcelUtil
						.readIntegerSetFromExcel(layer1FsFeatureFile, 2,
								"frequency");
				ExcelUtil.writeSetToExcel(vTermFs, layer1SelectFSFeatureFile,
						3, "FSWikiTerm");
				ExcelUtil.writeIntegerSetToExcel(vFrequencyFs,
						layer1SelectFSFeatureFile, 3, "frequencyAll");
				AddFeature afFs = new AddFeature(layer1SelectFSFeatureFile, 3,
						2, "frequencyAll", "frequencyAll", "Integer");
				afFs.run(afFs);
				ComputeFeature cfFs = new ComputeFeature(
						layer1SelectFSFeatureFile, 2, "frequency",
						"frequencyAll", "/", "ratio");
				cfFs.run(cfFs);
				setProcessId(1.6);
			}
			// 7.筛选特征
			if (getProcessId() < 1.7) {
				setCurrent(95, "Select category feature……");
				SelectFeature sfCat = new SelectFeature(
						layer1SelectCatFeatureFile, 2, "category", 2, 1,
						layer2File, 2, domainExceptionPath);
				sfCat.run(sfCat);
				setCurrent(96, "Select FS feature……");
				SelectFeature sfFS = new SelectFeature(
						layer1SelectFSFeatureFile, 2, "FSWikiTerm", 2, 1,
						layer2File, 3, fsExceptionPath);
				sfFS.run(sfFS);
				setProcessId(1.7);
			}
			// 8.过滤
			layer2SelectListPath = processPath + "/layer2-select.txt";
			layer2SelectHtmlPath = htmlPath + "/layer2-select";
			layer1ListPath = processPath + "/layer1.txt";
			if (getProcessId() < 1.8) {
				setCurrent(97, "Filtering according category……");
				SelectTerm stCat = new SelectTerm(layer2File, "category", 2, 0,
						4, layer2CategoryDetailPath, false);
				stCat.run(stCat);
				setCurrent(98, "Filtering according FS……");
				SelectTerm stFS = new SelectTerm(layer2File, "FSWikiTerm", 3,
						0, 4, layer2FSDetailPath, false);
				stFS.run(stFS);
				setCurrent(99, "Generating related Files……");
				Vector<String> vSelect = ExcelUtil.readSetFromExcel(layer2File,
						4, "term");
				SetUtil.writeSetToFile(vSelect, layer2SelectListPath);
				wc.crawlPageByList(layer2SelectListPath, layer2HtmlPath,
						layer2SelectHtmlPath, 10);
				Vector<String> vConf = new Vector<String>();
				vConf.add(layer1SelectHtmlPath + ","
						+ SetUtil.readSetFromFile(layer1SelectListPath).size());
				vConf.add(layer2SelectHtmlPath + "," + vSelect.size());
				SetUtil.writeSetToFile(vConf, layer2SelectConf);// 生成前台显示配置文件
				int crawl1Number = SetUtil.readSetFromFile(layer1ListPath)
						.size();
				int filter1Number = SetUtil.readSetFromFile(
						layer1SelectListPath).size();
				int crawl2Number = SetUtil.readSetFromFile(layer2ListPath)
						.size();
				int filter2Number = vSelect.size();
				Vector<String> vChartData = new Vector<String>();
				vChartData.add(crawl1Number + "@@@" + filter1Number);
				vChartData.add(crawl2Number + "@@@" + filter2Number);
				SetUtil.writeSetToFile(vChartData, chartConf);
				FileUtil.writeStringFile("<html>&nbsp;&nbsp;Third<br>&nbsp;&nbsp;Layer</html>", btnConf);
			}
			setProcessId(1.8);
			setCurrent(100, "success");
		}
	}
}
