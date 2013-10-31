package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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

import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.network.TNetwork;
import twaver.table.TElementTable;
import twaver.table.TTableColumn;
import twaver.table.TTableModel;
import function.netAnalysis.Degree;
import function.netAnalysis.motif.Motif;
import function.util.StringUtil;

public class NetFeaturePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7870708291584840831L;
	private JLabel netLabel = new JLabel(" 术语关系文件：");
	private JTextField netTf = new JTextField();
	private JButton filechooseBtn = new JButton("浏览...");
	private JButton degreeBtn = new JButton(" 度 ");
	private JButton coefficientBtn = new JButton("聚集系数");
	private JButton motifBtn = new JButton("Motif");
	private JButton pageRankBtn = new JButton("PageRank");

	private TDataBox tableBox = new TDataBox();
	private TElementTable table = new TElementTable(tableBox);
	private TTableModel model = table.getTableModel();
	private Border border = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border resultTitle = BorderFactory.createTitledBorder(border, "参数",
			TitledBorder.LEFT, TitledBorder.TOP);
	private JScrollPane resultSp = new JScrollPane(table);

	private TDataBox box = new TDataBox();
	private TNetwork network = new TNetwork(box);
	private JScrollPane jsp = new JScrollPane(network);
	
	private JPanel jp = new JPanel();

	private String path; // 所选文件路径

	/**
	 * @param args
	 */

	public NetFeaturePanel() {
		setLayout(null);
		this.setBackground(Color.white);
		
		this.add(jp);
		jp.setLayout(null);
		jp.setBackground(new Color(230,239,248));
		jp.setBounds(0, 10, 1000, 150);
		resultSp.setBounds(0, 170, 400, 430);
		jsp.setBounds(450, 170, 550, 430);
		

		// 术语关系文件选择布局设置
		jp.add(netLabel);
		jp.add(netTf);
		jp.add(filechooseBtn);
		netLabel.setBounds(80, 30, 110, 25);
		netTf.setBounds(185, 30, 550, 25);
		filechooseBtn.setBounds(745, 30, 60, 25);

		filechooseBtn.addActionListener(new ActionListener() { // 此处获得path作为调用的接口
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						JFileChooser file = new JFileChooser("f:");
						int result = file.showOpenDialog(null);
						// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
						if (result == JFileChooser.APPROVE_OPTION) {
							// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
							path = file.getSelectedFile().getAbsolutePath();
							netTf.setText(path);
						}
					}
				});

		// 功能按钮布局设置
		jp.add(degreeBtn);
		jp.add(coefficientBtn);
		jp.add(motifBtn);
		jp.add(pageRankBtn);
		degreeBtn.setBounds(570, 70, 100, 25);
		motifBtn.setBounds(700, 70, 100, 25);
		coefficientBtn.setBounds(570, 110, 100, 25);
		pageRankBtn.setBounds(700, 110, 100, 25);

		degreeBtn.addActionListener(new ActionListener() { // 此处添加“度”按钮处理程序
					@Override
					public void actionPerformed(ActionEvent e) {
						final String path = netTf.getText();
						if (path.length() == 0) {
							JOptionPane.showMessageDialog(NetFeaturePanel.this,
									"请选择术语关系文件！");
						} else {
							final Degree degree = new Degree(path);
							final Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									degree.run(degree);
									JOptionPane.showMessageDialog(
											NetFeaturePanel.this,
											"结果文件已保存到源文件里！");
								}
							});
							t.start();
						}
					}
				});

		coefficientBtn.addActionListener(new ActionListener() { // 此处添加“聚集系数”按钮处理程序
					@Override
					public void actionPerformed(ActionEvent e) {

					}
				});

		motifBtn.addActionListener(new ActionListener() { // 此处添加“Motif”按钮处理程序
			@Override
			public void actionPerformed(ActionEvent e) {
				final String path = netTf.getText();
				if (path.length() == 0) {
					JOptionPane.showMessageDialog(NetFeaturePanel.this,
							"请选择术语关系文件！");
				} else {
					final Motif motif = new Motif();
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							/*******计算motif******/
							motif.computeMotif3(path);
							/*******填表******/
							String htmlFileName = path.substring(0,
									path.lastIndexOf("."))
									+ "_Edge0.html";// 生成的网页文件
							Vector<String[]> vResult = motif
									.getHtmlContent(htmlFileName);
							addRow(vResult);
							/*******画图******/
							String dumpFileName = path.substring(0,
									path.lastIndexOf("."))
									+ "_Edge.txt.OUT.dump";// fanmod生成的motif枚举文件
							HashMap<String, String[]> hmExample=motif.getMotif3Example(path, dumpFileName);
							addMotif3Example(hmExample);
							JOptionPane.showMessageDialog(NetFeaturePanel.this,
									"结果文件已保存到源文件里！");
						}
					});
					t.start();
				}
			}
		});
		pageRankBtn.addActionListener(new ActionListener() { // 此处添加“PageRank”按钮处理程序
					@Override
					public void actionPerformed(ActionEvent e) {

					}
				});

		// 参数结果布局设置
		this.add(resultSp);
		resultSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultSp.setBorder(resultTitle);
		

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
		table.addColumn(columnID);
		table.addColumn(columnAdj);
		table.addColumn(columnFrequency);
		table.addColumn(columnMean_Freq);
		table.addColumn(columnZ_Score);
		table.addColumn(columnp_Value);
		table.setRowHeight(70);
		// 拓扑结果展示布局设置
		this.add(jsp);
		
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}

	/**
	 * 把vResult添加到表格中
	 * 
	 * @param vResult
	 */
	public void addRow(Vector<String[]> vResult) {
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
			model.addRow(row);
		}
	}

	/**
	 * 把motif3例子添加到databox中
	 * 
	 * @param hmExample
	 */
	public void addMotif3Example(HashMap<String, String[]> hmExample) {
		int basePosX[] = { 90, 40, 140 };
		int basePosY[] = { 40, 126, 126 };
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
				node[i].putCustomDrawFill3D(true);
				node[i].putCustomDrawShapeFactory(TWaverConst.SHAPE_CIRCLE);
				node[i].setName(title[i]);
				if (i == 0)
					node[i].putLabelPosition(TWaverConst.POSITION_TOP);
				box.addElement(node[i]);
			}
			// 添加边
			char c01=motifId.charAt(1);
			char c02=motifId.charAt(2);
			char c10=motifId.charAt(3);
			char c12=motifId.charAt(5);
			char c20=motifId.charAt(6);
			char c21=motifId.charAt(7);
			Link l01 = new Link(node[0],node[1]);
			Link l02 = new Link(node[0],node[2]);
			Link l12 = new Link(node[1],node[2]);
			if(c01=='1')
				l01.putLinkToArrow(true);
			if(c10=='1')
				l01.putLinkFromArrow(true);
			if(c02=='1')
				l02.putLinkToArrow(true);
			if(c20=='1')
				l02.putLinkFromArrow(true);
			if(c12=='1')
				l12.putLinkToArrow(true);
			if(c21=='1')
				l12.putLinkFromArrow(true);
			if(l01.isLinkFromArrow()||l01.isLinkToArrow()){
				l01.putLinkColor(new Color(79,79,79));
				l01.putLinkWidth(1);
				l01.putLinkOutlineWidth(0);
				box.addElement(l01);
			}
			if(l02.isLinkFromArrow()||l02.isLinkToArrow()){
				l02.putLinkColor(new Color(79,79,79));
				l02.putLinkWidth(1);
				l02.putLinkOutlineWidth(0);
				box.addElement(l02);
			}
			if(l12.isLinkFromArrow()||l12.isLinkToArrow()){
				l12.putLinkColor(new Color(79,79,79));
				l12.putLinkWidth(1);
				l12.putLinkOutlineWidth(0);
				box.addElement(l12);
			}
			id++;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
