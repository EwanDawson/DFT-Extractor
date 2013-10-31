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
import function.crawler.WikiHistoryCrawler_new;
import function.hypRelation.ExtractCategory;
import function.linkAnalysis.redirectProcess.RedirectProcess;
import function.util.ExcelUtil;
import function.util.FileUtil;
import function.util.SetUtil;

public class Layer1Extractor {

	private String DTPath = "";// 领域术语存放路径
	private String domainName = "";// 领域名称
	// html
	private String htmlPath = "";// html存放路径
	private String layer0HtmlPath = "";// 第0层页面路径
	private String layer1RawHtmlPath = "";// 第1层页面初始路径
	private String layer1HtmlPath = "";// 第1层去除重定向后的页面路径
	private String layer1DTSelectHtmlPath = "";// 第一层根据领域术语选择的页面路径
	private String layer1SelectHtmlPath = "";// 第1层选择的页面路径
	// process
	private String processPath = "";// 处理过程文件存放路径
	private double processId = 0;// 流程控制标签
	private String processIdFile = "";// 流程控制文件路径
	private String processFile = "";// 已经执行的流程文件
	private Vector<String> vProcess = new Vector<String>();// 已经执行的流程
	private String layer0ListPath = "";// 第0层页面列表文件路径
	private String layer0HrefPath = "";// 第0层页面链接文件路径
	private String layer1ListPath = "";// 第1层页面列表文件路径
	private String layer1CatFeatureFile = "";// 第一层目录特征文件
	private String layer1FSFeatureFile = "";// 第一层首句特征文件
	private String layer1HistoryFeatureFile = "";// 第一层历史特征文件
	private String layer1File = "";// 第一层术语特征文件
	private String layer1DTSelectListPath = "";// 第一层根据领域术语选择的列表路径
	private String layer1SelectListPath = "";// 第一层最终选择术语列表路径
	// history
	private String historyPath = "";// 历史编辑页面存放路径
	private String layer0HistoryPath = "";// 第0层历史页面路径
	private String layer1HistoryPath = "";// 第1层历史页面路径
	private String layer1DTSelectHistoryPath = "";// 第一层根据领域术语选择的历史页面路径

	// other
	private int sum = 0;// 总数
	private int currentSize = 0;// 扫描的爬取文件个数

	// gui
	public String guiPath = "";// 前台显示文件夹
	public String layer1CrawlHtmlConf = "";// 第一层爬取的网页配置路径
	public String layer1CrawlHistoryConf = "";// 第一层爬取的历史页面配置路径
	public String layer1CategoryDetailPath = "";// 第一层目录特征详细路径
	public String layer1FSDetailPath = "";// 第一层FS特征详细路径
	public String layer1HistoryDetailPath = "";// 第一层历史特征详细路径
	public String layer1SelectConf = "";// 第一层选择的网页配置路径
	public String chartConf = "";// chart配置路径
	public String btnConf = "";// btn配置路径

	public static void main(String[] args) {
		Layer1Extractor extractor = new Layer1Extractor("Computer_network",
				"F:\\DOFT-data\\DTExtraction");
		extractor.extract();
	}

	public Layer1Extractor(String domainName, String savePath) {
		this.domainName = domainName;
		this.DTPath = savePath + "/" + this.domainName;
		init();
	}

	public void init() {
		// html
		htmlPath = DTPath + "/html";
		File fHtml = new File(htmlPath);
		fHtml.mkdirs();
		// process
		processPath = DTPath + "/process";
		File fProcess = new File(processPath);
		fProcess.mkdirs();
		// history
		historyPath = DTPath + "/history";
		File fHistory = new File(historyPath);
		fHistory.mkdirs();
		// 流程初始化
		processIdFile = processPath + "/processId.txt";
		processFile = processPath + "/process.txt";
		File fProcessId = new File(processIdFile);
		if (!fProcessId.exists())
			FileUtil.writeStringFile("0", processIdFile);
		File fProcessTxt = new File(processFile);
		if (!fProcessTxt.exists())
			try {
				fProcessTxt.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// gui
		guiPath = DTPath + "/gui";
		File fGui = new File(guiPath);
		fGui.mkdirs();
		layer1CrawlHtmlConf = guiPath + "/layer1CrawlHtmlConf.txt";
		layer1CrawlHistoryConf = guiPath + "/layer1CrawlHistoryConf.txt";
		layer1CategoryDetailPath = guiPath + "/layer1-categoryDetail.txt";
		layer1FSDetailPath = guiPath + "/layer1-FSDetail.txt";
		layer1HistoryDetailPath = guiPath + "/layer1-historyDetail.txt";
		layer1SelectConf = guiPath + "/layer1SelectConf.txt";
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
		final Layer1ExtractorThread let = new Layer1ExtractorThread(100, "");
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
	class Layer1ExtractorThread implements Runnable {
		// 任务的当前完成量
		public volatile int current;
		// 任务的当前提示
		public volatile String currentTask;
		// 总任务量
		public int amount;

		public Layer1ExtractorThread(int amount, String currentTask) {
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
			WikiHistoryCrawler_new whc = new WikiHistoryCrawler_new();
			WikiHrefProcess whp = new WikiHrefProcess();
			setProcessId(0);
			// 1.爬取初始页面
			layer0HtmlPath = htmlPath + "/layer0";
			layer0ListPath = processPath + "/layer0.txt";
			if (getProcessId() < 0.1) {
				setCurrent(1, "Building layer0 related files……");
				File fLayer0 = new File(layer0HtmlPath);
				fLayer0.mkdirs();
				Vector<String> vLayer0Term = new Vector<String>();
				vLayer0Term.add(domainName);
				SetUtil.writeSetToFile(vLayer0Term, layer0ListPath);
				setCurrent(2, "Crawing the domain page……");
				wc.removeProxy();
				wc.crawlPageByList(layer0ListPath, layer0HtmlPath, 10);
				setProcessId(0.1);
			}
			// 2.抽取layer0超链接
			layer0HrefPath = processPath + "/layer0-href.txt";
			Vector<String> vNavboxTerm = ExtractorUtil
					.checkNavbox(layer0HtmlPath + "/" + domainName + ".html");
			System.out.println(vNavboxTerm);
			if (getProcessId() < 0.2) {
				setCurrent(4, "Extracting hyperLinks of domain page……");
				Vector<String> vLayer0Href = new Vector<String>();
				vLayer0Href.addAll(whp.getWikiTermFromDir(layer0HtmlPath));
				vLayer0Href.addAll(vNavboxTerm);
				vLayer0Href=SetUtil.getNoRepeatVectorIgnoreCase(vLayer0Href);
				Vector<String> vLayer0Term = SetUtil
						.readSetFromFile(layer0ListPath);
				vLayer0Href = SetUtil.getSubSet(
						SetUtil.getNoRepeatVector(vLayer0Href), vLayer0Term);
				sum = vLayer0Href.size();
				SetUtil.writeSetToFile(vLayer0Href, layer0HrefPath);
				setProcessId(0.2);
			}
			sum = SetUtil.readSetFromFile(layer0HrefPath).size();
			System.out.println(sum);
			// 3.爬取layer1页面
			layer1RawHtmlPath = htmlPath + "/layer1-raw";
			if (getProcessId() < 0.3) {
				FileUtil.writeStringFile(layer1RawHtmlPath + "," + sum,
						layer1CrawlHtmlConf);// 生成前台显示配置文件
				File fLayer1RawHtml = new File(layer1RawHtmlPath);
				fLayer1RawHtml.mkdirs();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 防止文件读取错误
				setCurrent(5, "Crawling the page of layer 1……");
				Thread tDetect = new Thread(new Runnable() {
					@Override
					public void run() {
						if (ExtractorUtil.scanDirPage(layer1RawHtmlPath).size() >= sum) {
							for (int i = 1; i <= sum; i++) {
								setCurrent(4 + i * 40 / sum, "Crawling layer1 page(" + i
										+ "/" + sum + ")……");
							}
						}// 已经爬取完成
						else {
							Vector<String> vExistPage = new Vector<String>();
							while (currentSize < sum) {
								HashMap<String, String> hm = ExtractorUtil
										.scanDirPage(layer1RawHtmlPath);
								Vector<String> vNewAddPage = ExtractorUtil
										.getNewAddPage(vExistPage, hm);
								while (vNewAddPage.size() == 0) {
									hm = ExtractorUtil
											.scanDirPage(layer1RawHtmlPath);
									vNewAddPage = ExtractorUtil.getNewAddPage(
											vExistPage, hm);
								}
								vExistPage.addAll(vNewAddPage);
								currentSize = vExistPage.size();
								setCurrent(4 + currentSize * 40 / sum,
										"Crawling layer1 page(" + currentSize + "/" + sum
												+ ")……");
							}
						}// 未爬取完成
					}
				});
				tDetect.start();
				wc.crawlPageByList(layer0HrefPath, layer1RawHtmlPath, 10);
				while (tDetect.isAlive()) {
				}// 执行完再继续……
				setProcessId(0.3);
			}
			// 4.去除重定向
			layer1HtmlPath = htmlPath + "/layer1";
			layer1ListPath = processPath + "/layer1.txt";
			if (getProcessId() < 0.4) {
				setCurrent(46, "Analysis articles' redirect……");
				if (!isFinishedProcess("layer1-raw->redirect")) {
					String layer1RedirectXls = processPath
							+ "/layer1-raw-redirect.xls";
					RedirectProcess rp = new RedirectProcess(layer1RawHtmlPath,
							layer1RedirectXls, layer1HtmlPath, true);
					rp.run(rp);
					SetUtil.writeSetToFile(
							FileUtil.getDirFileSet(layer1HtmlPath),
							layer1ListPath);
					addProcess("layer1-raw->redirect");
				}
				setProcessId(0.4);
			}
			// 5.爬取历史页面
			layer0HistoryPath = historyPath + "/layer0";
			layer1HistoryPath = historyPath + "/layer1";
			if (getProcessId() < 0.5) {
				sum = SetUtil.readSetFromFile(layer1ListPath).size();
				FileUtil.writeStringFile(layer1HistoryPath + "," + sum,
						layer1CrawlHistoryConf);// 生成前台显示配置文件
				File fLayer1History = new File(layer1HistoryPath);
				fLayer1History.mkdirs();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 防止文件读取错误
				setCurrent(47, "Crawling history page of layer1……");
				whc.crawlPageByList(layer0ListPath, layer0HistoryPath, 1);
				whc.crawlPageByList(layer1ListPath, layer1HistoryPath, 10);
				Thread tDetect = new Thread(new Runnable() {
					@Override
					public void run() {
						if (ExtractorUtil.scanDirPage(layer1HistoryPath).size() >= sum) {
							for (int i = 1; i <= sum; i++) {
								setCurrent(47 + i * 30 / sum, "Crawling history page(" + i
										+ "/" + sum + ")……");
							}
						}// 已经爬取完成
						else {
							while (currentSize < sum) {
								HashMap<String, String> hmTemp = ExtractorUtil
										.scanDirPage(layer1HistoryPath);
								currentSize = hmTemp.size();
								setCurrent(47 + currentSize * 30 / sum,
										"Crawling history page(" + currentSize + "/" + sum
												+ ")……");
							}
						}// 未爬取完成
					}
				});
				tDetect.start();
				while (tDetect.isAlive()) {
				}// 执行完再继续……
				setProcessId(0.5);
			}
			// 6.layer1特征抽取
			layer1CatFeatureFile = processPath + "/layer1-cat.xls";
			layer1FSFeatureFile = processPath + "/layer1-fs.xls";
			layer1HistoryFeatureFile = processPath + "/layer1-history.xls";
			layer1File = processPath + "/layer1.xls";
			if (getProcessId() < 0.6) {
				setCurrent(77, "Extracting category feature……");
				ExtractCategory ec = new ExtractCategory(layer1HtmlPath,
						layer1CatFeatureFile);
				ec.run(ec);
				setCurrent(80, "Extracting FS feature……");
				ExtractFirstSentence efs = new ExtractFirstSentence(
						layer1HtmlPath, layer1FSFeatureFile);
				efs.run(efs);
				setCurrent(83, "Extracting history feature……");
				ExtractHistory eh = new ExtractHistory(layer1HistoryPath,
						layer1HistoryFeatureFile);
				eh.run(eh);
				setCurrent(85, "Building term feature table……");
				Vector<String> vTerm = ExcelUtil.readSetFromExcel(
						layer1CatFeatureFile, 1, "term");
				Vector<String> vCat = ExcelUtil.readSetFromExcel(
						layer1CatFeatureFile, 1, "category");
				Vector<String> vFS = ExcelUtil.readSetFromExcel(
						layer1FSFeatureFile, 1, "FSWikiTerm");
				Vector<String> vHistory = ExcelUtil.readSetFromExcel(
						layer1HistoryFeatureFile, 1, "editor");
				ExcelUtil.writeSetToExcel(vTerm, layer1File, 0, "term");
				ExcelUtil.writeSetToExcel(vCat, layer1File, 0, "category");
				ExcelUtil.writeSetToExcel(vFS, layer1File, 0, "FSWikiTerm");
				ExcelUtil.writeSetToExcel(vHistory, layer1File, 0, "editor");
				setProcessId(0.6);
			}
			// 7.按照领域词筛选
			layer1DTSelectListPath = processPath + "/layer1-DTSelect.txt";
			layer1DTSelectHtmlPath = htmlPath + "/layer1-DTSelect";
			layer1DTSelectHistoryPath = historyPath + "/layer1-DTSelect";
			if (getProcessId() < 0.7) {
				setCurrent(87, "Filtering according Domain Name……");
				if (vNavboxTerm.size() != 0) {
					String redirectFile = processPath
							+ "/layer1-raw-redirect.xls";
					RedirectNavboxTerm rnt = new RedirectNavboxTerm(
							redirectFile, vNavboxTerm, layer1File, 1);
					rnt.run(rnt);
				} else {
					SelectAccordingDT sadt = new SelectAccordingDT(layer1File,
							domainName);
					sadt.run(sadt);
				}
				SetUtil.writeSetToFile(
						ExcelUtil.readSetFromExcel(layer1File, 1, "term"),
						layer1DTSelectListPath);
				wc.crawlPageByList(layer1DTSelectListPath, layer1HtmlPath,
						layer1DTSelectHtmlPath, 1);
				whc.crawlPageByList(layer1DTSelectListPath, layer1HistoryPath,
						layer1DTSelectHistoryPath, 10);
				setProcessId(0.7);
			}
			// 8.layer1-DTSelect特征抽取
			String layer1DTSelectCatFeatureFile = processPath
					+ "/layer1-DTSelect-cat.xls";
			String layer1DTSelectFSFeatureFile = processPath
					+ "/layer1-DTSelect-fs.xls";
			String layer1DTSelectHistoryFeatureFile = processPath
					+ "/layer1-DTSelect-history.xls";
			if (getProcessId() < 0.8) {
				setCurrent(88, "Extracting layer1-DTSelect category……");
				ExtractCategory ec = new ExtractCategory(
						layer1DTSelectHtmlPath, layer1DTSelectCatFeatureFile);
				ec.run(ec);
				setCurrent(89, "Extracting layer1-DTSelect FS……");
				ExtractFirstSentence efs = new ExtractFirstSentence(
						layer1DTSelectHtmlPath, layer1DTSelectFSFeatureFile);
				efs.run(efs);
				setCurrent(90, "Extractinglayer1-DTSelect history……");
				ExtractHistory eh = new ExtractHistory(
						layer1DTSelectHistoryPath,
						layer1DTSelectHistoryFeatureFile);
				eh.run(eh);
				setCurrent(91, "Computing layer1-DTSelect history ratio……");
				Vector<String> vTerm = ExcelUtil.readSetFromExcel(
						layer1HistoryFeatureFile, 2, "editor");
				Vector<Integer> vFrequency = ExcelUtil.readIntegerSetFromExcel(
						layer1HistoryFeatureFile, 2, "frequency");
				ExcelUtil.writeSetToExcel(vTerm,
						layer1DTSelectHistoryFeatureFile, 3, "editor");
				ExcelUtil.writeIntegerSetToExcel(vFrequency,
						layer1DTSelectHistoryFeatureFile, 3, "frequencyAll");
				AddFeature af = new AddFeature(
						layer1DTSelectHistoryFeatureFile, 3, 2, "frequencyAll",
						"frequencyAll", "Integer");
				af.run(af);
				ComputeFeature cf = new ComputeFeature(
						layer1DTSelectHistoryFeatureFile, 2, "frequency",
						"frequencyAll", "/", "ratio");
				cf.run(cf);
				setProcessId(0.8);
			}
			// 9.筛选特征
			if (getProcessId() < 0.9) {
				setCurrent(92, "Select category feature……");
				SelectFeature sfCat = new SelectFeature(
						layer1DTSelectCatFeatureFile, 2, "category", 2,
						layer1File, 2);
				sfCat.run(sfCat);
				setCurrent(93, "Select FS feature……");
				SelectFeature sfFS = new SelectFeature(
						layer1DTSelectFSFeatureFile, 2, "FSWikiTerm", 2,
						layer1File, 3);
				sfFS.run(sfFS);
				setCurrent(94, "Select history feature……");
				SelectFeature sfHistory = new SelectFeature(
						layer1DTSelectHistoryFeatureFile, 2, "editor", 3, 0.6,
						layer1File, 4);
				sfHistory.run(sfHistory);
				setProcessId(0.9);
			}
			// 10.过滤
			layer1SelectListPath = processPath + "/layer1-select.txt";
			layer1SelectHtmlPath = htmlPath + "/layer1-select";
			if (getProcessId() < 1) {
				setCurrent(95, "Filtering according category……");
				SelectTerm stCat = new SelectTerm(layer1File, "category", 2, 0,
						5, layer1CategoryDetailPath, false);
				stCat.run(stCat);
				setCurrent(96, "Filtering according FS……");
				SelectTerm stFS = new SelectTerm(layer1File, "FSWikiTerm", 3,
						0, 5, layer1FSDetailPath, false);
				stFS.run(stFS);
				setCurrent(97, "Filtering according history……");
				SelectTerm stHistory = new SelectTerm(layer1File, "editor", 4,
						0, 5, layer1HistoryDetailPath, false);
				stHistory.run(stHistory);
				setCurrent(98, "Generating the related files……");
				String columnNames[] = { "category", "FSWikiTerm", "editor" };
				CopyTerm ct = new CopyTerm(layer1File, "term", 1, 5,
						columnNames, false);
				ct.run(ct);
				Vector<String> vSelect = ExcelUtil.readSetFromExcel(layer1File,
						5, "term");
				SetUtil.writeSetToFile(vSelect, layer1SelectListPath);
				wc.crawlPageByList(layer1SelectListPath, layer1HtmlPath,
						layer1SelectHtmlPath, 1);
				FileUtil.writeStringFile(
						layer1SelectHtmlPath + "," + vSelect.size(),
						layer1SelectConf);// 生成前台显示配置文件
				int crawlNumber = SetUtil.readSetFromFile(layer1ListPath)
						.size();
				int filterNumber = vSelect.size();
				FileUtil.writeStringFile(crawlNumber + "@@@" + filterNumber,
						chartConf);
				FileUtil.writeStringFile(
						"<html>Second<br>&nbsp;&nbsp;Layer</html>", btnConf);
			}
			setProcessId(1);
			setCurrent(100, "success");
		}
	}
}
