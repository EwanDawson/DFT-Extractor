package facetGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.chart.BarChart;
import twaver.table.TElementTable;
import twaver.table.TTableColumn;
import twaver.table.TTableModel;
import function.DTExtraction.Layer1Extractor;
import function.DTExtraction.Layer2Extractor;
import function.DTExtraction.Layer3Extractor;
import function.DTExtraction.ScanRunnable;
import function.util.FileUtil;

public class DTExtractionPanel_new extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8443678814523872488L;

	// domain select
	private JPanel jp = new JPanel();
	private JLabel domainLabel = new JLabel("Wiki Domain：");
	private JTextField domainTf = new JTextField();

	// save path
	private JLabel saveLabel = new JLabel("Save Path：");
	private JTextField saveTf = new JTextField();
	private JButton saveChooseBtn = new JButton("File...");

	// Crawling
	private Border crawlBorder = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border crawlTitle = BorderFactory.createTitledBorder(crawlBorder,
			"Layer1-Article", TitledBorder.LEFT, TitledBorder.TOP);
	private DefaultListModel dlm = new DefaultListModel();
	private JList pageList = new JList(dlm);
	private Border crawlTitle1 = BorderFactory.createTitledBorder(crawlBorder,
			"Layer2-Article", TitledBorder.LEFT, TitledBorder.TOP);
	private DefaultListModel dlm1 = new DefaultListModel();
	private JList pageList1 = new JList(dlm1);
	private Border crawlTitle2 = BorderFactory.createTitledBorder(crawlBorder,
			"Layer3-Article", TitledBorder.LEFT, TitledBorder.TOP);
	private DefaultListModel dlm2 = new DefaultListModel();
	private JList pageList2 = new JList(dlm2);
	private JScrollPane crawlJsp = new JScrollPane(pageList);
	private JScrollPane crawlJsp1 = new JScrollPane(pageList1);
	private JScrollPane crawlJsp2 = new JScrollPane(pageList2);

	// Feature Analysis
	private TDataBox categoryBox = new TDataBox();
	private TElementTable categoryTable = new TElementTable(categoryBox);
	private TTableModel categoryModel = categoryTable.getTableModel();
	private Border categoryBorder = BorderFactory.createEtchedBorder(
			Color.white, Color.gray);
	private Border categoryTitle = BorderFactory.createTitledBorder(
			categoryBorder, "Category", TitledBorder.LEFT, TitledBorder.TOP);
	private JScrollPane categoryJsp = new JScrollPane(categoryTable);

	private TDataBox fsBox = new TDataBox();
	private TElementTable fsTable = new TElementTable(fsBox);
	private TTableModel fsModel = fsTable.getTableModel();
	private Border fsBorder = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border fsTitle = BorderFactory.createTitledBorder(fsBorder,
			"Term In First Sentence", TitledBorder.LEFT, TitledBorder.TOP);
	private JScrollPane fsJsp = new JScrollPane(fsTable);

	private TDataBox historyBox = new TDataBox();
	private TElementTable historyTable = new TElementTable(historyBox);
	private TTableModel historyModel = historyTable.getTableModel();
	private Border historyBorder = BorderFactory.createEtchedBorder(
			Color.white, Color.gray);
	private Border historyTitle = BorderFactory.createTitledBorder(
			historyBorder, "Article Editor", TitledBorder.LEFT,
			TitledBorder.TOP);
	private JScrollPane historyJsp = new JScrollPane(historyTable);

	// Filtering
	private Border filterBorder = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border filterTitle = BorderFactory.createTitledBorder(filterBorder,
			"Filtering Result", TitledBorder.LEFT, TitledBorder.TOP);
	private DefaultListModel filterDlm = new DefaultListModel();
	private JList filterList = new JList(filterDlm);
	private JScrollPane filterJsp = new JScrollPane(filterList);
	private TDataBox chartBox = new TDataBox();
	private BarChart barChart = new BarChart(chartBox);
	private JScrollPane chartJsp = new JScrollPane(barChart);
	private JButton extractBtn = new JButton("<html>&nbsp;&nbsp;First<br>&nbsp;&nbsp;Layer</html>");

	// 其他属性定义
	Element eCrawl, eFilter;

	/**
	 * @param args
	 */

	public DTExtractionPanel_new() {

		setLayout(null);
		this.setBackground(Color.white);

		// domain select
		this.add(jp);
		jp.setLayout(null);
		jp.setBackground(new Color(230, 239, 248));
		jp.setBounds(10, 10, 990, 50);
		jp.add(domainLabel);
		jp.add(domainTf);
		domainLabel.setBounds(15, 15, 130, 25);
		domainTf.setBounds(105, 15, 300, 25);
		domainTf.setText("Euclidean_geometry");

		// save path
		jp.add(saveLabel);
		jp.add(saveTf);
		jp.add(saveChooseBtn);
		saveLabel.setBounds(460, 15, 130, 25);
		saveTf.setBounds(530, 15, 300, 25);
		saveChooseBtn.setBounds(840, 15, 60, 25);
		String savePath="f:\\DOFT-data";
		/*******动态变化路径*********//*
		String realPath = MainFrame.class.getClassLoader().getResource("").getFile();  
	        File file = new File(realPath);  
	        realPath = file.getAbsolutePath()+"\\DFT-Extractor";  
	        try {  
	            realPath = URLDecoder.decode(realPath, "utf-8");  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	    realPath=realPath.replace("\\\\", "\\");
	    savePath=realPath;
		*//******动态变化路径end**********/
		saveTf.setText(savePath);
		saveChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("F:");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					saveTf.setText(path);
				}
			}
		});

		/************** Crawling ****************/
		this.add(crawlJsp);
		this.add(crawlJsp1);
		this.add(crawlJsp2);
		crawlJsp.setBounds(10, 60, 293, 430);
		crawlJsp.setBorder(crawlTitle);
		crawlJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		crawlJsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		crawlJsp1.setBounds(313, 60, 293, 430);
		crawlJsp1.setBorder(crawlTitle1);
		crawlJsp1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		crawlJsp1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		crawlJsp2.setBounds(616, 60, 293, 430);
		crawlJsp2.setBorder(crawlTitle2);
		crawlJsp2
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		crawlJsp2
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// 设置List显示图标
		pageList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ImageIcon imgPage = new ImageIcon(getClass().getResource(
						"/resources/images/page.png"));
				setIcon(imgPage);
				setText(value.toString());

				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					// 设置选取与取消选取的前景与背景颜色.
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				return this;
			}
		});
		// 设置List显示图标
		pageList1.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ImageIcon imgPage = new ImageIcon(getClass().getResource(
						"/resources/images/page.png"));
				setIcon(imgPage);
				setText(value.toString());

				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					// 设置选取与取消选取的前景与背景颜色.
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				return this;
			}
		});
		// 设置List显示图标
		pageList2.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ImageIcon imgPage = new ImageIcon(getClass().getResource(
						"/resources/images/page.png"));
				setIcon(imgPage);
				setText(value.toString());

				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					// 设置选取与取消选取的前景与背景颜色.
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				return this;
			}
		});

		/************** Feature Analysis ****************/
		//this.add(categoryJsp);
		//this.add(fsJsp);
		//this.add(historyJsp);
		categoryJsp.setBounds(10, 260, 293, 230);
		fsJsp.setBounds(313, 260, 293, 230);
		historyJsp.setBounds(616, 260, 293, 230);
		categoryJsp.setBorder(categoryTitle);
		fsJsp.setBorder(fsTitle);
		historyJsp.setBorder(historyTitle);
		categoryJsp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		fsJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		historyJsp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		TTableColumn columnCategory = new TTableColumn("category", "category",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnFS = new TTableColumn("FS-term", "fs-term",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnHistory = new TTableColumn("editor", "editor",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnFrequency = new TTableColumn("frequency",
				"frequency", new twaver.table.renderer.StringRenderer());
		TTableColumn columnRecall = new TTableColumn("Recall", "Recall",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnHistoryRecall = new TTableColumn("Recall", "Recall",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnRatio = new TTableColumn("ratio", "ratio",
				new twaver.table.renderer.StringRenderer());
		columnCategory.setColumnWith(120);
		columnFS.setColumnWith(120);
		columnHistory.setColumnWith(70);
		columnFrequency.setColumnWith(73);
		columnRecall.setColumnWith(70);
		columnHistoryRecall.setColumnWith(70);
		columnRatio.setColumnWith(50);
		categoryTable.addColumn(columnCategory);
		categoryTable.addColumn(columnFrequency);
		categoryTable.addColumn(columnRecall);
		fsTable.addColumn(columnFS);
		fsTable.addColumn(columnFrequency);
		fsTable.addColumn(columnRecall);
		historyTable.addColumn(columnHistory);
		historyTable.addColumn(columnFrequency);
		historyTable.addColumn(columnRatio);
		historyTable.addColumn(columnHistoryRecall);

		/***************** Filtering *******************/
		this.add(filterJsp);
		this.add(chartJsp);
		this.add(extractBtn);
		filterJsp.setBounds(10, 500, 420, 250);
		chartJsp.setBounds(450, 510, 460, 238);
		extractBtn.setBounds(920, 580, 90, 60);
		Font font=new Font(extractBtn.getFont().getFontName(),Font.BOLD,14);
		extractBtn.setFont(font);
		filterJsp.setBorder(filterTitle);
		barChart.setYScaleTextVisible(true);
		// 设置Y最小的尺度值是否可见，默认不可见
		barChart.setYScaleMinTextVisible(true);
		// 添加一个节点
		eCrawl = new Node("Crawling");
		eCrawl.setName("Crawling");
		eCrawl.putChartColor(Color.GREEN);
		eFilter = new Node("Filtering");
		eFilter.setName("Filtering");
		eFilter.putChartColor(Color.BLUE);
		filterJsp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// 设置List显示图标
		filterList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ImageIcon imgPage = new ImageIcon(getClass().getResource(
						"/resources/images/page.png"));
				setIcon(imgPage);
				setText(value.toString());

				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					// 设置选取与取消选取的前景与背景颜色.
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				return this;
			}
		});
		//监测过滤的页面数量的线程
		final Thread filterT = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int filterSize=filterDlm.size();
					filterTitle = BorderFactory.createTitledBorder(filterBorder,
							"Filtering Result("+filterSize+")", TitledBorder.LEFT, TitledBorder.TOP);
					filterJsp.setBorder(filterTitle);
				}
			}
		});
		filterT.start();
		extractBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (extractBtn.getText().contains("First"))
					layer1Extract();
				else if (extractBtn.getText().contains("Second")){
					System.out.println("Second");
					layer2Extract();
				}
				else {
					layer3Extract();
					//copy html
					String desDir= saveTf.getText()+"\\hyperExtraction\\"+domainTf.getText()+"\\html";
					Layer3Extractor extractor = new Layer3Extractor(
							domainTf.getText(), saveTf.getText() + "\\DTExtraction");
					String layer12SelectHtmlPath=extractor.layer12SelectHtmlPath;
					String layer3CatCrawlHtmlPath=extractor.layer3CatCrawlHtmlPath;
					try {
						FileUtil.copyDirectiory(layer12SelectHtmlPath, desDir);
						FileUtil.copyDirectiory(layer3CatCrawlHtmlPath, desDir);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 第一层抽取过程
	 */
	public void layer1Extract() {
		final Layer1Extractor extractor = new Layer1Extractor(
				domainTf.getText(), saveTf.getText() + "\\DTExtraction");
		extractor.extract();
		// 扫描layer1rawHtml的thread
		ScanRunnable srLayer1CrawlHtml = new ScanRunnable(
				extractor.layer1CrawlHtmlConf, dlm, pageList);
		Thread tLayer1CrawlHtml = new Thread(srLayer1CrawlHtml);
		tLayer1CrawlHtml.start();
		// 扫描layer1CrawlHistory的thread
		ScanRunnable srLayer1CrawlHistory = new ScanRunnable(
				extractor.layer1CrawlHistoryConf, dlm, pageList);
		Thread tLayer1CrawlHistory = new Thread(srLayer1CrawlHistory);
		tLayer1CrawlHistory.start();
		// 扫描layer1Category特征的thread
		ScanRunnable srLayer1Category = new ScanRunnable(
				extractor.layer1CategoryDetailPath, categoryModel,
				categoryTable);
		Thread tLayer1Category = new Thread(srLayer1Category);
		tLayer1Category.start();
		// 扫描layer1FS特征的thread
		ScanRunnable srLayer1FS = new ScanRunnable(
				extractor.layer1FSDetailPath, fsModel, fsTable);
		Thread tLayer1FS = new Thread(srLayer1FS);
		tLayer1FS.start();
		// 扫描layer1History特征的thread
		ScanRunnable srLayer1History = new ScanRunnable(
				extractor.layer1HistoryDetailPath, historyModel, historyTable);
		Thread tLayer1History = new Thread(srLayer1History);
		tLayer1History.start();
		// 扫描layer1Select的thread
		ScanRunnable srLayer1Select = new ScanRunnable(
				extractor.layer1SelectConf, filterDlm, filterList);
		Thread tLayer1Select = new Thread(srLayer1Select);
		tLayer1Select.start();
		// 扫描chart的thread
		ScanRunnable srChart = new ScanRunnable(extractor.chartConf, chartBox,
				barChart, eCrawl, eFilter);
		Thread tChart = new Thread(srChart);
		tChart.start();
		// 扫描btn的thread
		ScanRunnable srBtn = new ScanRunnable(extractor.btnConf, extractBtn);
		Thread tBtn = new Thread(srBtn);
		tBtn.start();
	}

	/**
	 * 第二层抽取过程
	 */
	public void layer2Extract() {
		final Layer2Extractor extractor = new Layer2Extractor(
				domainTf.getText(), saveTf.getText() + "\\DTExtraction");
		extractor.extract();
		// 扫描layer2rawHtml的thread
		ScanRunnable srLayer2CrawlHtml = new ScanRunnable(
				extractor.layer2CrawlHtmlConf, dlm1, pageList1);
		Thread tLayer2CrawlHtml = new Thread(srLayer2CrawlHtml);
		tLayer2CrawlHtml.start();
		// 扫描layer2Category特征的thread
		ScanRunnable srLayer2Category = new ScanRunnable(
				extractor.layer2CategoryDetailPath, categoryModel,
				categoryTable);
		Thread tLayer2Category = new Thread(srLayer2Category);
		tLayer2Category.start();
		// 扫描layer2FS特征的thread
		ScanRunnable srLayer2FS = new ScanRunnable(
				extractor.layer2FSDetailPath, fsModel, fsTable);
		Thread tLayer2FS = new Thread(srLayer2FS);
		tLayer2FS.start();
		// 扫描layer2Select的thread
		ScanRunnable srLayer2Select = new ScanRunnable(
				extractor.layer2SelectConf, filterDlm, filterList);
		Thread tLayer1Select = new Thread(srLayer2Select);
		tLayer1Select.start();
		// 扫描chart的thread
		ScanRunnable srChart = new ScanRunnable(extractor.chartConf, chartBox,
				barChart, eCrawl, eFilter);
		Thread tChart = new Thread(srChart);
		tChart.start();
		// 扫描btn的thread
		ScanRunnable srBtn = new ScanRunnable(extractor.btnConf, extractBtn);
		Thread tBtn = new Thread(srBtn);
		tBtn.start();
	}

	/**
	 * 第三层抽取过程
	 */
	public void layer3Extract() {
		final Layer3Extractor extractor = new Layer3Extractor(
				domainTf.getText(), saveTf.getText() + "\\DTExtraction");
		extractor.extract();
		// 扫描layer3CatCrawlHtml的thread
		ScanRunnable srLayer3CatCrawlHtml = new ScanRunnable(
				extractor.layer3CatCrawlHtmlConf, dlm2, pageList2);
		Thread tLayer3CatCrawlHtml = new Thread(srLayer3CatCrawlHtml);
		tLayer3CatCrawlHtml.start();
		// 扫描chart的thread
		ScanRunnable srChart = new ScanRunnable(extractor.chartConf, chartBox,
				barChart, eCrawl, eFilter);
		Thread tChart = new Thread(srChart);
		tChart.start();
		// 扫描btn的thread
		ScanRunnable srBtn = new ScanRunnable(extractor.btnConf, extractBtn);
		Thread tBtn = new Thread(srBtn);
		tBtn.start();
	}

	/**
	 * 获取领域名称
	 * 
	 * @return
	 */
	public String getDomainName() {
		return domainTf.getText();
	}

	/**
	 * 获取保存路径
	 * 
	 * @return
	 */
	public String getSavePath() {
		return saveTf.getText();
	}
}
