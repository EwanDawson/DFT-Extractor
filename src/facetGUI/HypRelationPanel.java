package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.ResizableNode;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.chart.BarChart;
import twaver.network.TNetwork;
import twaver.table.TElementTable;
import twaver.table.TTableColumn;
import twaver.table.TTableModel;
import function.hypRelation.HypRelationExtractor;
import function.util.ExcelUtil;
import function.util.FileUtil;
import function.util.StringUtil;

public class HypRelationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4034136806675760042L;
	// 文件选择区域
	private JPanel jp = new JPanel();
	private JLabel htmldirLabel = new JLabel("Wiki Domain Article Directory：");
	private JTextField htmldirTf = new JTextField();
	private JButton htmldirChooseBtn = new JButton("File...");
	// motif计算区域
	private TDataBox argBox = new TDataBox();
	private TElementTable argTable = new TElementTable(argBox);
	private TTableModel argModel = argTable.getTableModel();
	private Border argBorder = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border argTitle = BorderFactory.createTitledBorder(argBorder,
			"Motif", TitledBorder.LEFT, TitledBorder.TOP);
	private JScrollPane argSp = new JScrollPane(argTable);
	private TDataBox egBox = new TDataBox();
	private TNetwork egNetwork = new TNetwork(egBox);
	private JScrollPane egJsp = new JScrollPane(egNetwork);
	private JButton motifBtn = new JButton(
			"<html>&nbsp;&nbsp;&nbsp;Motif<br>Analysis</html>");
	// 自动标注区域
	private TDataBox annotationBox = new TDataBox();
	private TElementTable annotationTable = new TElementTable(annotationBox);
	private TTableModel annotationModel = annotationTable.getTableModel();
	private Border annotationBorder = BorderFactory.createEtchedBorder(
			Color.white, Color.gray);
	private Border annotationTitle = BorderFactory.createTitledBorder(
			annotationBorder, "Annotation Result", TitledBorder.LEFT,
			TitledBorder.TOP);
	private JScrollPane annotationSp = new JScrollPane(annotationTable);
	private JButton annotationBtn = new JButton(
			"<html>Automatic<br>Annotation</html>");
	private TDataBox chartBox = new TDataBox();
	private BarChart barChart = new BarChart(chartBox);
	private JScrollPane chartJsp = new JScrollPane(barChart);

	// 上下位识别结果区域
	private TDataBox hypBox = new TDataBox();
	private TNetwork hypNetwork = new TNetwork(hypBox);
	private JScrollPane hypJsp = new JScrollPane(hypNetwork);
	private JButton hypBtn = new JButton("<html>Hyponym<br>Extraction</html>");

	// 其他属性定义
	HypRelationExtractor hre = null;
	int annotationNumber[] = new int[4];
	Element A, B, O, N;

	/**
	 * @param args
	 */

	public HypRelationPanel() {
		setLayout(null);
		this.setBackground(Color.white);

		// 文件选择布局设置
		this.add(jp);
		jp.setLayout(null);
		jp.setBackground(new Color(230, 239, 248));
		jp.setBounds(10, 10, 990, 50);
		jp.add(htmldirLabel);
		jp.add(htmldirTf);
		jp.add(htmldirChooseBtn);
		htmldirLabel.setBounds(45, 15, 170, 25);
		htmldirTf.setBounds(215, 15, 510, 25);
		htmldirChooseBtn.setBounds(735, 15, 60, 25);
		htmldirTf.setText("F:\\DOFT-data\\hyperExtraction\\CN\\html");

		htmldirChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser(
						"F:\\DOFT-data\\hyperExtraction\\DS\\DS_html");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					htmldirTf.setText(path);
				}
			}
		});

		// Motif计算布局设置
		this.add(argSp);
		this.add(egJsp);
		this.add(motifBtn);
		argSp.setBounds(510, 60, 400, 270);
		egJsp.setBounds(430, 70, 480, 190);
		motifBtn.setBounds(920, 150, 90, 60);
		argSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		argSp.setBorder(argTitle);
		egJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		egJsp.setVisible(false);
		TTableColumn columnID = new TTableColumn("ID", "ID",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnAdj = new TTableColumn("Adj", "Adj",
				new twaver.table.renderer.IconRenderer());
		TTableColumn columnFrequency = new TTableColumn("Frequency",
				"Frequency", new twaver.table.renderer.StringRenderer());
		TTableColumn columnMean_Freq = new TTableColumn("Mean-Freq",
				"Mean-Freq", new twaver.table.renderer.StringRenderer());
		TTableColumn columnZ_Score = new TTableColumn("Z-Score", "Z-Score",
				new twaver.table.renderer.StringRenderer());
		TTableColumn columnp_Value = new TTableColumn("p-Value", "p-Value",
				new twaver.table.renderer.StringRenderer());
		columnID.setColumnWith(30);
		columnAdj.setColumnWith(80);
		columnFrequency.setColumnWith(75);
		columnMean_Freq.setColumnWith(75);
		columnZ_Score.setColumnWith(60);
		columnp_Value.setColumnWith(50);
		argTable.addColumn(columnID);
		argTable.addColumn(columnAdj);
		argTable.addColumn(columnFrequency);
		argTable.addColumn(columnMean_Freq);
		argTable.addColumn(columnZ_Score);
		argTable.addColumn(columnp_Value);
		argTable.setRowHeight(70);

		motifBtn.addActionListener(new ActionListener() { // 此处添加“Motif”按钮处理程序
			@Override
			public void actionPerformed(ActionEvent e) {
				final String path = htmldirTf.getText();
				if (path.length() == 0) {
					JOptionPane
							.showMessageDialog(HypRelationPanel.this,
									"Please select Wikipedia domain article directory！");
				} else {
					if (hre == null)
						hre = new HypRelationExtractor(path);
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							if (hre.getProcessId() < 1) {
								/******* 计算motif ******/
								hre.motifCompute();
								/******* 填表 ******/
								String htmlFileName = hre.desDir
										+ "/relation_Edge0.html";// 生成的网页文件
								Vector<String[]> vResult = hre.motif
										.getHtmlContent(htmlFileName);
								addArgRow(vResult);
								/******* 画图 ******/
								String dumpFileName = hre.desDir
										+ "/relation_Edge.txt.OUT.dump";// fanmod生成的motif枚举文件
								HashMap<String, String[]> hmExample = hre.motif
										.getMotif3Example(
												hre.newRelationXlsFileName,
												dumpFileName);
								addMotif3Example(hmExample);
								JOptionPane.showMessageDialog(
										HypRelationPanel.this,
										"Motif computing finished！");
							}
						}
					});
					t.start();
				}
			}
		});

		// 自动标注布局设置
		this.add(annotationSp);
		this.add(annotationBtn);
		this.add(chartJsp);
		annotationSp.setBounds(10, 60, 370, 270);
		annotationBtn.setBounds(390, 150, 90, 60);
		chartJsp.setBounds(430, 270, 480, 218);
		chartJsp.setVisible(false);
		// 设置Y的尺度值是否可见，默认是不可见的
		barChart.setYScaleTextVisible(true);
		// 设置Y最小的尺度值是否可见，默认不可见
		barChart.setYScaleMinTextVisible(true);
		annotationSp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		annotationSp.setBorder(annotationTitle);
		TTableColumn columnSourceURLName = new TTableColumn("SourceTerm",
				"SourceTerm", new twaver.table.renderer.StringRenderer());
		TTableColumn columnToURLName = new TTableColumn("TargetTerm",
				"TargetTerm", new twaver.table.renderer.StringRenderer());
		TTableColumn columnRelation = new TTableColumn("RelationType", "RelationType",
				new twaver.table.renderer.StringRenderer());
		columnSourceURLName.setColumnWith(120);
		columnToURLName.setColumnWith(120);
		columnRelation.setColumnWith(100);
		annotationTable.addColumn(columnSourceURLName);
		annotationTable.addColumn(columnToURLName);
		annotationTable.addColumn(columnRelation);
		// 添加一个节点
		A = new Node("A is a B");
		A.setName("A is a B");
		A.putChartColor(Color.GREEN);
		B = new Node("B is a A");
		B.setName("B is a A");
		B.putChartColor(Color.BLUE);
		O = new Node("Other");
		O.setName("Other");
		O.putChartColor(Color.YELLOW);
		N = new Node("noExist");
		N.setName("noExist");
		N.putChartColor(Color.RED);
		annotationBtn.addActionListener(new ActionListener() { // 此处添加“annotation”按钮处理程序
					@Override
					public void actionPerformed(ActionEvent e) {

						final String path = htmldirTf.getText();
						if (path.length() == 0) {
							JOptionPane
									.showMessageDialog(HypRelationPanel.this,
											"Please select Wikipedia domain article directory！");
						} else {
							if (hre == null)
								hre = new HypRelationExtractor(path);
							final Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									if (hre.getProcessId() < 1) {
										JOptionPane.showMessageDialog(
												HypRelationPanel.this,
												"Please compute motif first！");
									} else if (hre.getProcessId() == 1) {
										hre.annotation();
									}
									String relationFile = hre.newRelationXlsFileName;// 关系文件
									String ColumnNames[] = new String[3];
									ColumnNames[0] = "sourceURLName";
									ColumnNames[1] = "toURLName";
									ColumnNames[2] = "relation";
									Vector<String[]> vRelation = getRelation(
											relationFile, 0, ColumnNames);
									/******* 填表 ******/
									addAnnotationRow(vRelation);
									/******* 画图 ******/
									annotationNumber = getRelationNumber(vRelation);
									showAnnotationChart(annotationNumber);
									JOptionPane.showMessageDialog(
											HypRelationPanel.this,
											"Automatic annotation finished！");

								}
							});
							t.start();
						}
					}
				});

		// 上下位结果展示布局设置
		this.add(hypJsp);
		this.add(hypBtn);

		hypJsp.setBounds(10, 350, 900, 400);
		hypBtn.setBounds(920, 490, 90, 60);
		hypJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		hypBtn.addActionListener(new ActionListener() { // 此处添加“上下位抽取”按钮处理程序
			@Override
			public void actionPerformed(ActionEvent e) {

				final String path = htmldirTf.getText();
				if (path.length() == 0) {
					JOptionPane
							.showMessageDialog(HypRelationPanel.this,
									"Please select Wikipedia domain article directory！");
				} else {
					if (hre == null)
						hre = new HypRelationExtractor(path);
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							if (hre.getProcessId() < 1) {
								JOptionPane.showMessageDialog(
										HypRelationPanel.this,
										"Please compute motif first！");
							} else if (hre.getProcessId() < 2) {
								JOptionPane.showMessageDialog(
										HypRelationPanel.this,
										"Please automatic annotation first！");
							} else if (hre.getProcessId() == 2) {
								hre.hyponymExtraction();
							}
							String relationFile = hre.desDir + "/relation.xls";// 关系文件
							String ColumnNames[] = new String[3];
							ColumnNames[0] = "sourceURLName";
							ColumnNames[1] = "toURLName";
							ColumnNames[2] = "predictRelation";
							Vector<String[]> vRelation = getRelation(
									relationFile, 1, ColumnNames);
							int[] relationNumber = getRelationNumber(vRelation);
							/******* 画图 ******/
							showExtractionChart(relationNumber);
							showCompareChart(annotationNumber,relationNumber);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							hypNetwork.showFullScreen();
							showHypGraph(vRelation);
							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							hypNetwork.exitFullScreen();
							//copy关系文件
							String src = hre.desDir + "/relation.csv";// 关系文件
							String srcDir=hre.desDir;
							File fSrcDir=new File(srcDir);
							File fSavePath=fSrcDir.getParentFile().getParentFile().getParentFile();
							String domainName=fSrcDir.getParentFile().getName();
							String desDir=fSavePath.getAbsolutePath()+"\\facetedTree\\"+domainName;
							File fDesDir=new File(desDir);
							fDesDir.mkdirs();
							String des=desDir+"\\relation.csv";
							File fSrc=new File(src);
							File fDes=new File(des);
							try {
								FileUtil.copyFile(fSrc, fDes);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							hre.setProcessId(0);
							JOptionPane.showMessageDialog(
									HypRelationPanel.this,
									"Hyponym extraction finished！");

						}
					});
					t.start();
				}
			}
		});

	}

	/**
	 * 根据关系集合显示拓扑图
	 * 
	 * @param relationPath
	 */
	public void showHypGraph(final Vector<String[]> vRelation) {
		hypBox.clear();
		final HashSet<String> hs = new HashSet<String>();
		final HashSet<String> hsRelation = new HashSet<String>();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 加入节点和边
				// 统计节点个数
				for (int i = 1; i < vRelation.size(); i++) {
					String record[] = vRelation.get(i);
					hs.add(record[0]);
					hs.add(record[1]);
				}
				final int count = hs.size();
				hs.clear();
				// 加入节点和边
				for (int i = 1; i < vRelation.size(); i++) {
					String record[] = vRelation.get(i);
					String relationStr = "";
					if (record[2].equals("A is a B"))
						relationStr = record[1] + "->" + record[0];
					else if (record[2].equals("B is a A"))
						relationStr = record[0] + "->" + record[1];
					if (hsRelation.contains(relationStr)
							|| relationStr.equals(""))
						continue;
					else {
						hsRelation.add(relationStr);
						if (!hs.contains(record[0])) {
							ResizableNode node = new ResizableNode() {
								/**
								 * 
								 */
								private static final long serialVersionUID = 1L;

								public int getHeight() {
									return 10;
								}

								public int getWidth() {
									return 10;
								}
							};
							node.setName(record[0]);
							double angle = Math.PI * 2 / count * hs.size();
							int x = 500 + (int) (500 * Math.cos(angle));
							int y = 500 + (int) (500 * Math.sin(angle));
							node.setLocation(x, y);
							node.putCustomDraw(true);
							//node.putCustomDrawFill3D(true);
							node.putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
							node.putLabelColor(new Color(0, 0, 0, 120)); // 设置名字的颜色有一定透明度
							hypBox.addElement(node);
							hs.add(record[0]);
						}
						if (!hs.contains(record[1])) {
							ResizableNode node = new ResizableNode() {
								/**
								 * 
								 */
								private static final long serialVersionUID = 1L;

								public int getHeight() {
									return 10;
								}

								public int getWidth() {
									return 10;
								}
							};
							node.setName(record[1]);
							double angle = Math.PI * 2 / count * hs.size();
							int x = 500 + (int) (500 * Math.cos(angle));
							int y = 500 + (int) (500 * Math.sin(angle));
							node.setLocation(x, y);
							node.putCustomDraw(true);
							//node.putCustomDrawFill3D(true);
							node.putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
							node.putLabelColor(new Color(0, 0, 0, 120));// 设置名字的颜色有一定透明度
							hypBox.addElement(node);
							hs.add(record[1]);
						}
						String nodeName[] = relationStr.split("->");
						ResizableNode from = (ResizableNode) hypBox
								.getElementByName(nodeName[1]);
						ResizableNode to = (ResizableNode) hypBox
								.getElementByName(nodeName[0]);
						Link l = new Link(from, to);
						l.putLinkFromArrow(true);
						l.putLinkColor(new Color(0, 0, 255, 100)); // 设置线的颜色为部分透明的浅蓝
						l.putLinkWidth(1);
						l.putLinkOutlineWidth(0);
						hypBox.addElement(l);
					}
				}
				// 调整节点大小
				@SuppressWarnings("unchecked")
				List<ResizableNode> nodes = hypBox
						.getElementsByType(ResizableNode.class); // 获得所有节点
				for (ResizableNode node : nodes) {
					int degree = node.getAllLinks().size();
					int size = degree + 5;
					if (size > 30)
						size = 30;
					node.setSize(size, size); // 设置节点大小
					Color c; // 节点颜色
					if (degree < 3)
						c = new Color(0, 240, 80); // 绿
					else if (degree < 8)
						c = new Color(180, 240, 80); // 浅绿
					else if (degree < 20)
						c = new Color(250, 250, 80); // 黄
					else if (degree < 30)
						c = new Color(240, 180, 80); // 黄红
					else
						c = new Color(240, 120, 80); // 浅红
					node.putCustomDrawFill(true);
					node.putCustomDrawFillColor(c);// 设置节点颜色
				}
				hypNetwork.doLayout(TWaverConst.LAYOUT_SYMMETRIC, true);
			}
		});
		t.start();
	}

	/**
	 * 把vResult添加到参数表格中
	 * 
	 * @param vResult
	 */
	public void addArgRow(Vector<String[]> vResult) {
		argModel.clearRawData();
		for (int i = 0; i < vResult.size(); i++) {
			Vector<Serializable> row = new Vector<Serializable>();
			String[] record = vResult.get(i);
			row.add(StringUtil.getCenterString(record[0], 6));
			ImageIcon imgPage = new ImageIcon(getClass().getResource(
					"/resources/images/" + record[1]));
			row.addElement(imgPage);
			if (record[2].length() > 8)
				record[2] = record[2].substring(0, 7) + "%";
			if (record[3].length() > 8)
				record[3] = record[3].substring(0, 7) + "%";
			row.add(StringUtil.getCenterString(record[2], 13));
			row.add(StringUtil.getCenterString(record[3], 13));
			row.add(StringUtil.getCenterString(record[4], 12));
			row.add(StringUtil.getCenterString(record[5], 10));
			argModel.addRow(row);
		}
	}

	/**
	 * 把motif3例子添加到databox中
	 * 
	 * @param hmExample
	 */
	public void addMotif3Example(HashMap<String, String[]> hmExample) {
		egBox.clear();
		int basePosX[] = { 90, 40, 140 };
		int basePosY[] = { 25, 111, 111 };
		int xAdd = 200;
		int yAdd = 150;
		int id = 0;
		Iterator<String> it = hmExample.keySet().iterator();
		while (it.hasNext()) {
			int posX[] = new int[3];
			int posY[] = new int[3];
			int xAddCount = id % 2;
			int yAddCount = id / 2;
			for (int i = 0; i < 3; i++) {
				posX[i] = basePosX[i] + xAddCount * xAdd;
				posY[i] = basePosY[i] + yAddCount * yAdd;
			}// 确定节点坐标
			String motifId = it.next();
			String[] title = hmExample.get(motifId);
			// 添加节点
			Node node[] = new Node[3];
			for (int i = 0; i < 3; i++) {
				node[i] = new Node() {
					private static final long serialVersionUID = 1L;

					public int getHeight() {
						return 5;
					}

					public int getWidth() {
						return 5;
					}
				};
				node[i].setLocation(posX[i], posY[i]);
				node[i].putCustomDraw(true);
				//node[i].putCustomDrawFill3D(true);
				node[i].putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
				node[i].setName(title[i]);
				if (i == 0)
					node[i].putLabelPosition(TWaverConst.POSITION_TOP);
				egBox.addElement(node[i]);
			}
			// 添加边
			char c01 = motifId.charAt(1);
			char c02 = motifId.charAt(2);
			char c10 = motifId.charAt(3);
			char c12 = motifId.charAt(5);
			char c20 = motifId.charAt(6);
			char c21 = motifId.charAt(7);
			Link l01 = new Link(node[0], node[1]);
			Link l02 = new Link(node[0], node[2]);
			Link l12 = new Link(node[1], node[2]);
			if (c01 == '1')
				l01.putLinkToArrow(true);
			if (c10 == '1')
				l01.putLinkFromArrow(true);
			if (c02 == '1')
				l02.putLinkToArrow(true);
			if (c20 == '1')
				l02.putLinkFromArrow(true);
			if (c12 == '1')
				l12.putLinkToArrow(true);
			if (c21 == '1')
				l12.putLinkFromArrow(true);
			if (l01.isLinkFromArrow() || l01.isLinkToArrow()) {
				l01.putLinkColor(new Color(79, 79, 79));
				l01.putLinkWidth(1);
				l01.putLinkOutlineWidth(0);
				egBox.addElement(l01);
			}
			if (l02.isLinkFromArrow() || l02.isLinkToArrow()) {
				l02.putLinkColor(new Color(79, 79, 79));
				l02.putLinkWidth(1);
				l02.putLinkOutlineWidth(0);
				egBox.addElement(l02);
			}
			if (l12.isLinkFromArrow() || l12.isLinkToArrow()) {
				l12.putLinkColor(new Color(79, 79, 79));
				l12.putLinkWidth(1);
				l12.putLinkOutlineWidth(0);
				egBox.addElement(l12);
			}
			id++;
		}
	}

	/**
	 * 从xls中获取关系数据
	 * 
	 * @param xlsFile
	 * @param sheetID
	 * @return
	 */
	public Vector<String[]> getRelation(String xlsFile, int sheetID,
			String ColumnNames[]) {
		Vector<String[]> vRelation = ExcelUtil.readSetFromExcel(xlsFile,
				sheetID, ColumnNames);
		return vRelation;
	}

	/**
	 * 把vRelation添加到标注表格中
	 * 
	 * @param vRelation
	 */
	public void addAnnotationRow(Vector<String[]> vRelation) {
		annotationModel.clearRawData();
		for (int i = 0; i < vRelation.size(); i++) {
			Vector<String> row = new Vector<String>();
			String[] record = vRelation.get(i);
			row.add(record[0]);
			row.add(record[1]);
			row.add(record[2]);
			annotationModel.addRow(row);
		}
	}

	/**
	 * 从关系集合中获取各种关系的数量
	 * 
	 * @param vRelation
	 * @return
	 */
	public int[] getRelationNumber(Vector<String[]> vRelation) {
		int number[] = new int[4];
		for (int i = 0; i < vRelation.size(); i++) {
			String record[] = vRelation.get(i);
			if (record[2].equals("A is a B"))
				number[0]++;
			else if (record[2].equals("B is a A"))
				number[1]++;
			else if (record[2].equals("Other"))
				number[2]++;
			else
				number[3]++;
		}
		return number;
	}

	/**
	 * 把标注关系的数量显示到chart上面
	 * 
	 * @param relationNumber
	 */
	public void showAnnotationChart(int[] relationNumber) {
		int sum = relationNumber[0] + relationNumber[1] + relationNumber[2]
				+ relationNumber[3];
		// 设置Y最大的尺度值
		barChart.setUpperLimit(sum);
		// 设置Y坐标的间距
		int gap = sum / 15;
		gap = gap / 100 * 100;
		barChart.setYScaleValueGap(gap);
		A.putChartValue(relationNumber[0]);
		B.putChartValue(relationNumber[1]);
		O.putChartValue(relationNumber[2]);
		N.putChartValue(relationNumber[3]);
		if (!chartBox.contains(A))
			chartBox.addElement(A);
		if (!chartBox.contains(B))
			chartBox.addElement(B);
		if (!chartBox.contains(O))
			chartBox.addElement(O);
		if (!chartBox.contains(N))
			chartBox.addElement(N);
	}
	
	/**
	 * 把抽取关系的数量显示到chart上面
	 * 
	 * @param relationNumber
	 */
	public void showExtractionChart(int[] relationNumber) {
		chartAnimation(A, annotationNumber[0], relationNumber[0]);
		chartAnimation(B, annotationNumber[1], relationNumber[1]);
		chartAnimation(O, annotationNumber[2], relationNumber[2]);
	}

	/**
	 * 柱状图动画
	 * 
	 * @param e
	 * @param begin
	 * @param end
	 */
	public void chartAnimation(Element e, int begin, int end) {
		int gap = end - begin;
		int inc = 0;
		if (gap < 100)
			inc = 1;
		else if (gap < 1000)
			inc = 10;
		else if (gap < 10000)
			inc = 100;
		else
			inc = 1000;
		for (int i = 1; i < gap / inc + 1; i++) {
			e.putChartValue(begin + i * inc);
			N.putChartValue(N.getChartValue()-inc);//no exist 减少
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		int lastGap=(int) (end-e.getChartValue());
		N.putChartValue(N.getChartValue()-lastGap);
		e.putChartValue(end);
	}

	/**
	 * 把标注和识别的关系数量显示到chart上面
	 * @param annotationNumber
	 * @param relationNumber
	 */
	public void showCompareChart(int[] annotationNumber,int[] relationNumber){
		barChart.setBarType(TWaverConst.BAR_TYPE_GROUP);
		barChart.addXScaleText("Annotation");
		barChart.addXScaleText("After extraction");
		chartBox.removeElement(A);
		A.addChartValue(annotationNumber[0]);
		A.addChartValue(relationNumber[0]);
		chartBox.addElement(A);
		chartBox.removeElement(B);
		B.addChartValue(annotationNumber[1]);
		B.addChartValue(relationNumber[1]);
		chartBox.addElement(B);
		chartBox.removeElement(O);
		O.addChartValue(annotationNumber[2]);
		O.addChartValue(relationNumber[2]);
		chartBox.addElement(O);
		chartBox.removeElement(N);
		N.addChartValue(annotationNumber[3]);
		N.addChartValue(relationNumber[3]);
		chartBox.addElement(N);
	}
	
	/**
	 * 设置html网页集合路径
	 * @param path
	 */
	public void setHtmlDirPath(String path){
		htmldirTf.setText(path);
	}
	
	/**
	 * 获取html路径
	 * 
	 * @return
	 */
	public String getHtmlDirPath() {
		return htmldirTf.getText();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
