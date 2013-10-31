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
import function.crawler.WikiCategoryCrawler;
import function.hypRelation.ExtractCategory;
import function.util.FileUtil;
import function.util.SetUtil;

public class Layer3Extractor {

	private String DTPath = "";// 领域术语存放路径
	private String domainName = "";// 领域名称
	// html
	private String htmlPath = "";// html存放路径
	private String layer1SelectHtmlPath = "";// 第1层选择的页面路径
	private String layer2SelectHtmlPath = "";// 第二层选择的页面路径
	public String layer12SelectHtmlPath = "";// 1,2层选择的页面路径
	public String layer3CatCrawlHtmlPath = "";// 第3层根据选择的目录爬取的页面路径

	// process
	private String processPath = "";// 处理过程文件存放路径
	private double processId = 0;// 流程控制标签
	private String processIdFile = "";// 流程控制文件路径
	private String processFile = "";// 已经执行的流程文件
	private Vector<String> vProcess = new Vector<String>();// 已经执行的流程
	private String layer1SelectListPath = "";// 第一层最终选择术语列表路径
	private String layer2SelectListPath = "";// 第2层根据领域术语选择的列表路径
	private String layer2HrefPath = "";// 第二层选择的页面的超链接集合路径
	private String layer3CatCrawlListPath = "";// 第3层根据选择的目录爬取的页面列表路径
	private String layer12SelectListPath = "";// 第一二层选择的页面列表路径
	private String layer12SelectCatFeatureFile = "";// 第一二层选择的页面目录特征
	private String layer0ListPath = "";// 第0层列表路径
	private String selectCategoryPath = "";// 选择出的目录集合
	private String categoryPath = "";// 爬取下来的目录路径
	private String layer1ListPath="";//第一层列表文件
	private String layer2ListPath="";//第二层列表文件

	// other
	int layer2HrefNumber = 0;// 第二层超链接数量
	private int sum = 0;// 总数
	private int currentSize = 0;// 扫描的爬取文件个数

	// gui
	public String guiPath = "";// 前台显示文件夹
	public String layer3CatCrawlHtmlConf = "";// 第3层根据选择的目录爬取的页面配置路径
	public String layer3SelectConf="";//第三层筛选的结果配置路径
	public String chartConf = "";// chart配置路径
	public String btnConf = "";// btn配置路径

	public static void main(String[] args) {
		Layer3Extractor extractor = new Layer3Extractor("Computer_network",
				"F:\\DOFT-data\\DTExtraction");
		extractor.extract();
	}

	public Layer3Extractor(String domainName, String savePath) {
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
		// gui
		guiPath = DTPath + "/gui";
		layer3CatCrawlHtmlConf = guiPath + "/layer3CatCrawlHtmlConf.txt";
		layer3SelectConf = guiPath + "/layer3SelectConf.txt";
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
		final Layer3ExtractorThread let = new Layer3ExtractorThread(100, "");
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
	class Layer3ExtractorThread implements Runnable {
		// 任务的当前完成量
		public volatile int current;
		// 任务的当前提示
		public volatile String currentTask;
		// 总任务量
		public int amount;

		public Layer3ExtractorThread(int amount, String currentTask) {
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
			setProcessId(2);
			// 1.抽取layer2-select超链接
			layer2SelectHtmlPath = htmlPath + "/layer2-select";
			layer2HrefPath = processPath + "/layer2-href.txt";
			layer0ListPath = processPath + "/layer0.txt";
			layer1SelectListPath = processPath + "/layer1-select.txt";
			layer2SelectListPath = processPath + "/layer2-select.txt";
			if (getProcessId() < 2.1) {
				setCurrent(1, "Analysis hyperLink of layer2……");
				if (!isFinishedProcess("layer2-select->analysisHref")) {
					Vector<String> vLayer2Href = new Vector<String>();
					vLayer2Href.addAll(whp
							.getWikiTermFromDir(layer2SelectHtmlPath));
					Vector<String> vLayer0Term = SetUtil
							.readSetFromFile(layer0ListPath);
					Vector<String> vLayer1Term = SetUtil
							.readSetFromFile(layer1SelectListPath);
					Vector<String> vLayer2Term = SetUtil
							.readSetFromFile(layer2SelectListPath);
					vLayer2Href = SetUtil.getNoRepeatVectorIgnoreCase(SetUtil
							.getSubSet(vLayer2Href, SetUtil.getUnionSet(SetUtil
									.getUnionSet(vLayer0Term, vLayer1Term),
									vLayer2Term)));
					layer2HrefNumber = vLayer2Href.size();
					SetUtil.writeSetToFile(vLayer2Href, layer2HrefPath);
					addProcess("layer2-select->analysisHref");
				}
				setProcessId(2.1);
				layer2HrefNumber = SetUtil.readSetFromFile(layer2HrefPath)
						.size();
			}
			// 2.选择category
			selectCategoryPath = processPath + "/select-category.txt";
			layer1SelectHtmlPath = htmlPath + "/layer1-select";
			layer12SelectHtmlPath = htmlPath + "/layer12-select";
			layer12SelectListPath = processPath + "/layer12-select.txt";
			layer12SelectCatFeatureFile = processPath
					+ "/layer12-select-cat.xls";
			if (getProcessId() < 2.2) {
				setCurrent(4, "Combine layer1-select and layer2-select……");
				try {
					FileUtil.copyDirectiory(layer1SelectHtmlPath,
							layer12SelectHtmlPath);
					FileUtil.copyDirectiory(layer2SelectHtmlPath,
							layer12SelectHtmlPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SetUtil.writeSetToFile(
						FileUtil.getDirFileSet(layer12SelectHtmlPath),
						layer12SelectListPath);
				setCurrent(6, "Extracting layer1,2-select category……");
				ExtractCategory ec = new ExtractCategory(layer12SelectHtmlPath,
						layer12SelectCatFeatureFile);
				ec.run(ec);
				setCurrent(8, "Select category root of layer2……");
				File f = new File(selectCategoryPath);
				if (!f.exists()) {
					// 选择
					SelectCategory sc = new SelectCategory(
							layer12SelectCatFeatureFile, 2, 6,
							selectCategoryPath);
					sc.run(sc);
				}
				setProcessId(2.2);
			}
			// 3.爬取category
			categoryPath = processPath + "/category";
			if (getProcessId() < 2.3) {
				setCurrent(10, "Crawing the select category root……");
				if (!isFinishedProcess("select-category->crawl")) {
					WikiCategoryCrawler wcc = new WikiCategoryCrawler();
					Vector<String> vCat = SetUtil
							.readSetFromFile(selectCategoryPath);
					wcc.buildCategoryTree(vCat, categoryPath, 1);
					addProcess("select-category->crawl");
				}
				setProcessId(2.3);
			}
			// 4.爬取category中的术语
			layer3CatCrawlListPath = processPath + "/layer3CatCrawl.txt";
			layer3CatCrawlHtmlPath=htmlPath+"/layer3CatCrawl";
			layer1ListPath= processPath + "/layer1.txt";
			layer2ListPath= processPath + "/layer2.txt";
			if (getProcessId() < 2.4) {
				setCurrent(12, "Analysis term in select category……");
				File f=new File(layer3CatCrawlListPath);
				if(!f.exists()){
					CategoryAnalysis ca = new CategoryAnalysis(
							layer12SelectListPath, categoryPath,
							layer3CatCrawlListPath);
					ca.getCategoryTerm();
				}
				sum=SetUtil.readSetFromFile(layer3CatCrawlListPath).size();
				FileUtil.writeStringFile(layer3CatCrawlHtmlPath + "," + sum,
						layer3CatCrawlHtmlConf);// 生成前台显示配置文件
				File fLayer3CatRawHtml = new File(layer3CatCrawlHtmlPath);
				fLayer3CatRawHtml.mkdirs();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 防止文件读取错误
				setCurrent(14, "Crawling the term in select category……");
				Thread tDetect = new Thread(new Runnable() {
					@Override
					public void run() {
						if (ExtractorUtil.scanDirPage(layer3CatCrawlHtmlPath).size() >= sum) {
							for (int i = 1; i <= sum; i++) {
								setCurrent(14 + i * 76 / sum, "Crawling page(" + i
										+ "/" + sum + ")……");
							}
						}// 已经爬取完成
						else {
							Vector<String> vExistPage = new Vector<String>();
							while (currentSize < sum) {
								HashMap<String, String> hm = ExtractorUtil
										.scanDirPage(layer3CatCrawlHtmlPath);
								Vector<String> vNewAddPage = ExtractorUtil
										.getNewAddPage(vExistPage, hm);
								while (vNewAddPage.size() == 0) {
									hm = ExtractorUtil
											.scanDirPage(layer3CatCrawlHtmlPath);
									vNewAddPage = ExtractorUtil.getNewAddPage(
											vExistPage, hm);
								}
								vExistPage.addAll(vNewAddPage);
								currentSize = vExistPage.size();
								setCurrent(4 + currentSize * 76 / sum,
										"Crawling page(" + currentSize + "/" + sum
												+ ")……");
							}
						}// 未爬取完成
					}
				});
				tDetect.start();
				wc.crawlPageByList(layer3CatCrawlListPath,layer3CatCrawlHtmlPath, 10);
				while (tDetect.isAlive()) {
				}// 执行完再继续……
				Vector<String> vConf=new Vector<String>();
				vConf.add(layer1SelectHtmlPath + "," + SetUtil.readSetFromFile(layer1SelectListPath).size());
				vConf.add(layer2SelectHtmlPath + "," + SetUtil.readSetFromFile(layer2SelectListPath).size());
				vConf.add(layer3CatCrawlHtmlPath + "," + SetUtil.readSetFromFile(layer3CatCrawlListPath).size());
				setCurrent(95, "Generating the GUI configuration……");
				SetUtil.writeSetToFile(vConf, layer3SelectConf);// 生成前台显示配置文件
				int crawl1Number=SetUtil.readSetFromFile(layer1ListPath).size();
				int filter1Number=SetUtil.readSetFromFile(layer1SelectListPath).size();
				int crawl2Number=SetUtil.readSetFromFile(layer2ListPath).size();
				int filter2Number=SetUtil.readSetFromFile(layer2SelectListPath).size();
				int layer3CatCrawl=SetUtil.readSetFromFile(layer3CatCrawlListPath).size();
				Vector<String> vChartData=new Vector<String>();
				vChartData.add(crawl1Number+"@@@"+filter1Number);
				vChartData.add(crawl2Number+"@@@"+filter2Number);
				vChartData.add(layer2HrefNumber+"@@@"+layer3CatCrawl);
				SetUtil.writeSetToFile(vChartData, chartConf);
				FileUtil.writeStringFile("First Layer", btnConf);
				setProcessId(2.4);
			}
			setProcessId(2.8);
			setCurrent(100, "success");
		}
	}
}
