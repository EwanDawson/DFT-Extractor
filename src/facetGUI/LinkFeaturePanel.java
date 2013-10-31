package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import function.linkAnalysis.NavboxExtractor;
import function.linkAnalysis.URLtoExcel07;
import function.linkAnalysis.redirectProcess.RedirectProcess;
import function.util.SetUtil;

public class LinkFeaturePanel extends JPanel {

	private static final long serialVersionUID = 7584977725711945646L;
	
	private JTabbedPane funPane = new JTabbedPane(JTabbedPane.TOP);// 功能面板
	private JPanel getLinkPanel = new JPanel();//链接抽取面板
	private JPanel redirectAnalysisPanel = new JPanel();//重定向分析面板
	private JPanel boxGetPanel = new JPanel();//NavBox抽取面板
	
	private JLabel excelLabel = new JLabel("HTML文件夹路径：");
	private JTextField excelTf = new JTextField();
	private JButton excelChooseBtn = new JButton("浏览...");
	private JButton excelGetLinkBtn = new JButton("链接抽取");
	private JLabel redirectLabel = new JLabel("术语集文件路径：");
	private JTextField redirectTf = new JTextField();
	private JButton redirectChooseBtn = new JButton("浏览...");
	//private String[] redirectTag = { "Web","本地" };
	//private JComboBox redirectJcb = new JComboBox(redirectTag);
	private JButton redirectAnalysisBtn = new JButton("重定向分析");
	private JLabel boxLabel = new JLabel("术语集文件路径：");
	private JTextField boxTf = new JTextField();
	private JButton boxChooseBtn = new JButton("浏览...");
	private JButton boxGetTextBtn = new JButton("NavBox抽取");
	private Object[] colNames = { "术语", "从属领域" };
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
	public LinkFeaturePanel() {
		setLayout(null);
		this.setBackground(Color.white);
		funPane.setBounds(0, 10, 1000, 150);
		resultSp.setBounds(0, 180, 1000, 420);
		
		//功能面板布局设置
		this.add(funPane);
		funPane.addTab("链接抽取",getLinkPanel);
		funPane.add("重定向分析",redirectAnalysisPanel);
		funPane.add("NavBox抽取",boxGetPanel);
		
		// URLtoExcel布局设置
		getLinkPanel.setLayout(null);
		getLinkPanel.add(excelLabel);
		getLinkPanel.add(excelTf);
		getLinkPanel.add(excelChooseBtn);
		getLinkPanel.add(excelGetLinkBtn);
		getLinkPanel.setBackground(new Color(230,239,248));
		
		excelLabel.setBounds(30, 20, 150, 25);
		excelTf.setBounds(155, 20, 400, 25);
		excelChooseBtn.setBounds(570, 20, 60, 25);
		excelGetLinkBtn.setBounds(545, 65, 85, 25);
		
		excelChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件夹
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					excelTf.setText(path);
				}
			}
		});
		excelGetLinkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = excelTf.getText();
				final String desPath = dir + "-xlsx\\result.xlsx";
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(LinkFeaturePanel.this,
							"请选择HTML文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							URLtoExcel07 ue = new URLtoExcel07();
							ue.extractUrlToExcel(dir, desPath);
							JOptionPane.showMessageDialog(
									LinkFeaturePanel.this, "结果文件已保存到" + desPath
											+ "里！");
						}
					});
					t.start();
				}
			}
		});
		// 重定向分析布局设置
		redirectAnalysisPanel.setLayout(null);
		redirectAnalysisPanel.add(redirectLabel);
		redirectAnalysisPanel.add(redirectTf);
		redirectAnalysisPanel.add(redirectChooseBtn);
		//this.add(redirectJcb);
		redirectAnalysisPanel.add(redirectAnalysisBtn);
		redirectAnalysisPanel.setBackground(new Color(230,239,248));
		
		redirectLabel.setBounds(30, 20, 150, 25);
		redirectTf.setBounds(155, 20, 400, 25);
		redirectChooseBtn.setBounds(570, 20, 60, 25);
		//redirectJcb.setBounds(655, 65, 40, 25);
		redirectAnalysisBtn.setBounds(545, 65, 85, 25);
		redirectChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					redirectTf.setText(path);
				}
			}
		});
		redirectAnalysisBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String fileName = redirectTf.getText();
				final String desPath = fileName
						.replace(".txt", "-redirect.xls");
				if (fileName.length() == 0) {
					JOptionPane.showMessageDialog(LinkFeaturePanel.this,
							"请选择术语集文件路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							RedirectProcess ue = new RedirectProcess(fileName,false);
							ue.run(ue);
							JOptionPane.showMessageDialog(
									LinkFeaturePanel.this, "结果文件已保存到" + desPath
											+ "里！");
						}
					});
					t.start();
				}
			}
		});
		// NavBox抽取布局设置
		boxGetPanel.add(boxLabel);
		boxGetPanel.add(boxTf);
		boxGetPanel.add(boxChooseBtn);
		boxGetPanel.add(boxGetTextBtn);
		boxGetPanel.setLayout(null);
		boxGetPanel.setBackground(new Color(230,239,248));
		
		boxLabel.setBounds(30, 20, 150, 25);
		boxTf.setBounds(155, 20, 400, 25);
		boxChooseBtn.setBounds(570, 20, 60, 25);
		boxGetTextBtn.setBounds(545, 65, 90, 25);
		boxChooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					boxTf.setText(path);
				}
			}
		});
		boxGetTextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = boxTf.getText();
				final Vector<String> vWikiTerm = SetUtil
						.readSetFromFile(fileName);
				final String desPath = fileName.substring(0,
						fileName.lastIndexOf("\\") + 1)
						+ "navbox";
				if (fileName.length() == 0) {
					JOptionPane.showMessageDialog(LinkFeaturePanel.this,
							"请选择术语集文件路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							NavboxExtractor.extractNavBox(vWikiTerm, desPath);
							showmatchRelation(desPath); //显示结果
							JOptionPane.showMessageDialog(
									LinkFeaturePanel.this, "结果文件已保存到" + desPath
											+ "里！");
						}
					});
					t.start();
				}
			}
		});
		// 结果布局设置
		this.add(resultSp);
		resultSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultSp.setBorder(resultTitle);
		
	}
	
	//将matchRelation内容显示到表格里
	public void showmatchRelation(String desPath) {
		String matchRelationFile=desPath+"/matchRelation.csv";
		
		for (int index = tableModel.getRowCount() - 1; index >= 0; index--) {
			tableModel.removeRow(index);
		}
		
		try {
			FileReader fr = new FileReader(matchRelationFile);
			BufferedReader br = new BufferedReader(fr);
			String line= br.readLine();     //保存读取的一行
			String term[] = new String[2];  //保存该行的两个术语
			
			while(line != null) {
				term = line.split(",");
				tableModel.addRow(term);
				line = br.readLine();
				repaint();
			}
			br.close();
			fr.close();
		}catch(IOException e)  {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

	}

}
