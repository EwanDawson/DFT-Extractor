package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import function.txtAnalysis.TFIDF;
import function.txtAnalysis.Vectorization;
import function.txtAnalysis.basicTxtFeatures.GenerateBTF;
import function.txtAnalysis.similarity.Similarity;

public class TextFeaturePanel extends JPanel {

	private static final long serialVersionUID = 3309560379839023243L;
	
	private JTabbedPane funPane = new JTabbedPane(JTabbedPane.TOP);// 功能面板
	private JPanel vectorPanel = new JPanel();//文本向量化面板
	private JPanel tfidfPanel = new JPanel();//TF/IDF计算面板
	private JPanel posPanel = new JPanel();//位置特征计算面板
	private JPanel simPanel = new JPanel();//相似度计算面板
	
	// 文本向量化组件定义
	private JLabel vectorLabel = new JLabel("HTML文件夹路径：");
	private JTextField vectorTf = new JTextField();
	private JButton vectorFilechooseBtn = new JButton("浏览...");
	private JButton vectorBtn = new JButton("文本向量化");
	// tfidf组件定义
	private JLabel tfidfLabel = new JLabel("向量文件夹路径：");
	private JTextField tfidfTf = new JTextField();
	private JButton tfidfFilechooseBtn = new JButton("浏览...");
	private JButton tfidfBtn = new JButton("TF/IDF计算");
	// 位置特征计算组件定义
	private JLabel posLabel = new JLabel("HTML文件夹路径：");
	private JTextField posTf = new JTextField();
	private JButton posFilechooseBtn = new JButton("浏览...");
	private JButton posBtn = new JButton("位置特征计算");
	// 相似度组件定义
	private JLabel simLabel = new JLabel("HTML(TXT)路径：");
	private JTextField simTf = new JTextField();
	private JButton simFilechooseBtn = new JButton("浏览...");
	private JLabel simALabel = new JLabel("        文件列表A：");
	private JTextField simATf = new JTextField();
	private JButton simAFilechooseBtn = new JButton("浏览...");
	private JLabel simBLabel = new JLabel("文件列表B：");
	private JTextField simBTf = new JTextField();
	private JButton simBFilechooseBtn = new JButton("浏览...");
	private JLabel simTypeLabel = new JLabel("       相似度类型：");
	private String[] simType = { "余弦相似度" };
	private JComboBox simTypeBox = new JComboBox(simType);
	private JLabel simWeightLabel = new JLabel("相似度权重：");
	private String[] simWeight = { "TF", "IDF", "TF/IDF" };
	private JComboBox simWeightBox = new JComboBox(simWeight);
	private JLabel simVectorLabel = new JLabel("向量化方法：");
	private String[] simVector = { "Word向量化", "术语向量化" };
	private JComboBox simVectorBox = new JComboBox(simVector);
	private JButton simBtn = new JButton("相似度计算");
	private Object[] colNames = { "文本A", "文本B", "相似度" };
	private DefaultTableModel tableModel = new DefaultTableModel(colNames, 50);
	private JTable table = new JTable(tableModel);
	private Border border = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border resultTitle = BorderFactory.createTitledBorder(border,
			"结果展示", TitledBorder.LEFT, TitledBorder.TOP);
	private JScrollPane resultSp = new JScrollPane(table);

	/**
	 * @param args
	 */
	public TextFeaturePanel() {
		setLayout(null);
		this.setBackground(Color.white);
		funPane.setBounds(0, 10, 1000, 200);
		resultSp.setBounds(0, 230, 1000, 370);
		
		//功能面板布局设置
		this.add(funPane);
		funPane.addTab("文本向量化",vectorPanel);
		funPane.addTab("TF/IDF计算",tfidfPanel);
		funPane.addTab("位置特征计算",posPanel);
		funPane.addTab("相似度计算",simPanel);
		
		
		// 文本向量化布局设置
		vectorPanel.add(vectorLabel);
		vectorPanel.add(vectorTf);
		vectorPanel.add(vectorFilechooseBtn);
		vectorPanel.add(vectorBtn);
		vectorPanel.setLayout(null);
		vectorPanel.setBackground(new Color(230,239,248));
		vectorLabel.setBounds(10, 40, 150, 25);
		vectorTf.setBounds(155, 40, 400, 25);
		vectorFilechooseBtn.setBounds(570, 40, 60, 25);
		vectorBtn.setBounds(545, 95, 90, 25);
		vectorFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					vectorTf.setText(path);
				}
			}
		});
		vectorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = vectorTf.getText();
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(TextFeaturePanel.this,
							"请选择HTML文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Vectorization vectorization = new Vectorization();
							String vectorDir = vectorization.wordVector(dir);
							JOptionPane.showMessageDialog(
									TextFeaturePanel.this, "结果文件已保存到"
											+ vectorDir + "下！");
						}
					});
					t.start();
				}
			}
		});
		// tfidf布局设置
		tfidfPanel.add(tfidfLabel);
		tfidfPanel.add(tfidfTf);
		tfidfPanel.add(tfidfFilechooseBtn);
		tfidfPanel.add(tfidfBtn);
		tfidfPanel.setLayout(null);
		tfidfPanel.setBackground(new Color(230,239,248));
		tfidfLabel.setBounds(10, 40, 150, 25);
		tfidfTf.setBounds(155, 40, 400, 25);
		tfidfFilechooseBtn.setBounds(570, 40, 60, 25);
		tfidfBtn.setBounds(545, 95, 90, 25);
		tfidfFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					tfidfTf.setText(path);
				}
			}
		});
		tfidfBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = tfidfTf.getText();
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(TextFeaturePanel.this,
							"请选择向量文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							TFIDF tfidf = new TFIDF();
							tfidf.computeTFIDF_wv(dir);
							JOptionPane.showMessageDialog(
									TextFeaturePanel.this, "结果文件已保存到" + dir
											+ "_tfidf下！");
						}
					});
					t.start();
				}
			}
		});
		// 位置特征计算布局设置
		posPanel.add(posLabel);
		posPanel.add(posTf);
		posPanel.add(posFilechooseBtn);
		posPanel.add(posBtn);
		posPanel.setLayout(null);
		posPanel.setBackground(new Color(230,239,248));
		posLabel.setBounds(10, 40, 150, 25);
		posTf.setBounds(155, 40, 400, 25);
		posFilechooseBtn.setBounds(570, 40, 60, 25);
		posBtn.setBounds(535, 95, 100, 25);
		posFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					posTf.setText(path);
				}
			}
		});
		posBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = posTf.getText();
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(TextFeaturePanel.this,
							"请选择HTML文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							GenerateBTF gbtf=new GenerateBTF();
							gbtf.generateBTF(dir);
							JOptionPane.showMessageDialog(
									TextFeaturePanel.this, "结果文件已保存到" + dir
											+ "_xls下！");
						}
					});
					t.start();
				}
			}
		});
		// 相似度计算布局设置
		simPanel.add(simLabel);
		simPanel.add(simTf);
		simPanel.add(simFilechooseBtn);
		simPanel.add(simALabel);
		simPanel.add(simATf);
		simPanel.add(simAFilechooseBtn);
		simPanel.add(simBLabel);
		simPanel.add(simBTf);
		simPanel.add(simBFilechooseBtn);
		simPanel.add(simTypeLabel);
		simPanel.add(simTypeBox);
		simPanel.add(simWeightLabel);
		simPanel.add(simWeightBox);
		simPanel.add(simVectorLabel);
		simPanel.add(simVectorBox);
		simPanel.add(simBtn);
		this.add(resultSp);
		simPanel.setLayout(null);
		simPanel.setBackground(new Color(230,239,248));
		
		simLabel.setBounds(10, 10, 110, 25);
		simTf.setBounds(120, 10, 485, 25);
		simFilechooseBtn.setBounds(620, 10, 60, 25);
		
		simALabel.setBounds(10, 50, 110, 25);
		simATf.setBounds(120, 50, 165, 25);
		simAFilechooseBtn.setBounds(295, 50, 60, 25);
		simBLabel.setBounds(360, 50, 75, 25);
		simBTf.setBounds(430, 50, 175, 25);
		simBFilechooseBtn.setBounds(620, 50, 60, 25);
		
		simTypeLabel.setBounds(10, 90, 110, 25);
		simTypeBox.setBounds(120, 90, 100, 25);
		simWeightLabel.setBounds(275, 90, 110, 25);
		simWeightBox.setBounds(355, 90, 70, 25);
		simVectorLabel.setBounds(490, 90, 110, 25);
		simVectorBox.setBounds(570, 90, 100, 25);
		
		simBtn.setBounds(585, 130, 90, 25);
		
		
		
		resultSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultSp.setBorder(resultTitle);
		
		simFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					simTf.setText(path);
				}
			}
		});
		simAFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					simATf.setText(path);
				}
			}
		});
		simBFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					simBTf.setText(path);
				}
			}
		});
		simBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = simTf.getText();
				final String listAPath = simATf.getText();
				final String listBPath = simBTf.getText();
				final int simWeight = simWeightBox.getSelectedIndex();
				final int simVector = simVectorBox.getSelectedIndex();
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(TextFeaturePanel.this,
							"请选择HTML(txt)文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Similarity sim = new Similarity();
							HashMap<String[], Double> hm = sim
									.computeSimilarity(dir, listAPath,
											listBPath, simWeight, simVector);
							addSimilarityInfo(hm);
							JOptionPane.showMessageDialog(
									TextFeaturePanel.this, "结果文件已保存到" + dir
											+ "-similarity.csv里！");
						}
					});
					t.start();
				}
			}
		});
	}
	// 将相似度结果显示到表格里面
	public void addSimilarityInfo(HashMap<String[],Double> hm) {
		for (int index = tableModel.getRowCount() - 1; index >= 0; index--) {
			tableModel.removeRow(index);
		}
		Iterator<String[]> it=hm.keySet().iterator();
		while(it.hasNext()){
			String record[]=new String[3];
			String term[]=it.next();
			double similarity=hm.get(term);
			record[0]=term[0];
			record[1]=term[1];
			record[2]=String.valueOf(similarity);
			tableModel.addRow(record);
			repaint();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
