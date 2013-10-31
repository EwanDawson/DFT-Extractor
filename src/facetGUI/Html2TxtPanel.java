package facetGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import function.jsoup.WikiHtml2Txt;

public class Html2TxtPanel extends JPanel {

	private static final long serialVersionUID = 1770238087081107574L;
	
	private JTabbedPane funPane = new JTabbedPane(JTabbedPane.TOP);// 功能面板
	private JPanel urlPanel = new JPanel();//url提取面板
	private JPanel filePanel = new JPanel();//文件抽取面板
	private JPanel dirPanel = new JPanel();//文件集抽取面板
	
	private JLabel urlLabel = new JLabel("   请输入HTML源URL：");
	private JTextField urlTf = new JTextField();
	private JButton urlGetTextBtn = new JButton("提取正文");
	private JLabel fileLabel = new JLabel(" 请选择HTML文件：");
	private JTextField fileTf = new JTextField();
	private JButton filechooseBtn = new JButton("浏览...");
	private JButton fileGetTextBtn = new JButton("提取正文");
	private JLabel dirLabel = new JLabel("请选择HTML文件夹：");
	private JTextField dirTf = new JTextField();
	private JButton dirchooseBtn = new JButton("浏览...");
	private JButton dirGetTextBtn = new JButton("提取正文");
	private JTextArea htmlTa = new JTextArea();
	private JTextArea textTa = new JTextArea();
	private JScrollPane htmlSp = new JScrollPane(htmlTa);
	private JScrollPane textSp = new JScrollPane(textTa);
	private Border border = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border htmlTitle = BorderFactory.createTitledBorder(border,
			"网页源文件", TitledBorder.LEFT, TitledBorder.TOP);
	private Border textTitle = BorderFactory.createTitledBorder(border, "正文",
			TitledBorder.LEFT, TitledBorder.TOP);
	private JButton textAreaGetTextBtn = new JButton("提取正文");
	private String htmlContent = "";
	private String textContent = "";

	/**
	 * @param args
	 */
	public Html2TxtPanel() {
		setLayout(null);
		final WikiHtml2Txt wht = new WikiHtml2Txt();
		this.setBackground(Color.white);
		funPane.setBounds(0, 10, 1000, 150);
		htmlSp.setBounds(0, 170, 1000, 200);
		textSp.setBounds(0, 400, 1000, 200);
		textAreaGetTextBtn.setBounds(880, 375, 85, 25);
		
		//功能选项卡布局
		this.add(funPane);
		funPane.addTab("根据源url提取",urlPanel);
		funPane.addTab("根据单个文件提取",filePanel);
		funPane.addTab("根据多个文件提取",dirPanel);
		
		// URL方法布局设置
		urlPanel.add(urlLabel);
		urlPanel.add(urlTf);
		urlPanel.add(urlGetTextBtn);
		urlPanel.setLayout(null);
		urlPanel.setBackground(new Color(230,239,248));
		
		urlLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		urlLabel.setBounds(10, 20, 150, 25);
		urlTf.setBounds(155, 20, 400, 25);
		urlGetTextBtn.setBounds(470, 65, 85, 25);
		urlGetTextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String url = urlTf.getText();
				if (url.length() == 0) {
					JOptionPane.showMessageDialog(Html2TxtPanel.this,
							"请输入HTML源URL地址！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							htmlContent = wht.parseURLHtml(url);
							textContent = wht.parseURLText(url);
							htmlTa.setText(htmlContent);
							textTa.setText(textContent);
						}
					});
					t.start();
				}
			}
		});
		// 文件方法布局设置
		filePanel.add(fileLabel);
		filePanel.add(fileTf);
		filePanel.add(filechooseBtn);
		filePanel.add(fileGetTextBtn);
		filePanel.setLayout(null);
		filePanel.setBackground(new Color(230,239,248));
		
		fileLabel.setBounds(10, 20, 150, 25);
		fileTf.setBounds(155, 20, 400, 25);
		filechooseBtn.setBounds(570, 20, 60, 25);
		fileGetTextBtn.setBounds(470, 65, 85, 25);
		filechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("f:/Data");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					fileTf.setText(path);
				}
			}
		});
		fileGetTextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String path = fileTf.getText();
				if (path.length() == 0) {
					JOptionPane.showMessageDialog(Html2TxtPanel.this,
							"请选择HTML文件路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								FileReader fr = new FileReader(path);
								BufferedReader br = new BufferedReader(fr);
								String temp = br.readLine();
								htmlTa.setText("");
								while (temp != null) {
									htmlTa.append(temp);
									temp = br.readLine();
								}
								br.close();
								fr.close();
								textContent = wht.parsePathText(path);
								textTa.setText(textContent);
								if (JOptionPane.showConfirmDialog(
										Html2TxtPanel.this,
										"是否要将结果在同个文件夹下保存成文本文件？", "保存提示",
										JOptionPane.YES_NO_OPTION) == 0) {
									String txtPath = path.substring(0,
											path.length() - 5)
											+ ".txt";
									FileWriter fw = new FileWriter(txtPath);
									BufferedWriter bw = new BufferedWriter(fw);
									bw.write(textContent);
									bw.flush();
									bw.close();
									fw.close();
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
					t.start();
					textTa.setText(textContent);
				}

			}
		});
		// 文件夹方法布局设置
		dirPanel.add(dirLabel);
		dirPanel.add(dirTf);
		dirPanel.add(dirchooseBtn);
		dirPanel.add(dirGetTextBtn);
		dirPanel.setLayout(null);
		dirPanel.setBackground(new Color(230,239,248));
		
		dirLabel.setBounds(10, 20, 150, 25);
		dirTf.setBounds(155, 20, 400, 25);
		dirchooseBtn.setBounds(570, 20, 60, 25);
		dirGetTextBtn.setBounds(470, 65, 85, 25);
		dirchooseBtn.addActionListener(new ActionListener() {
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
					dirTf.setText(path);
				}
			}
		});
		dirGetTextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String dir = dirTf.getText();
				if (dir.length() == 0) {
					JOptionPane.showMessageDialog(Html2TxtPanel.this,
							"请选择HTML文件夹路径！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							wht.parseDirText(dir);
							JOptionPane.showMessageDialog(Html2TxtPanel.this,
									"结果文件已保存到" + dir + "-txt下！");
						}

					});
					t.start();
				}
			}
		});
		// 网页源文件布局设置
		this.add(htmlSp);
		this.add(textAreaGetTextBtn);
		htmlSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		htmlSp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		htmlTa.setLineWrap(true);
		htmlSp.setBorder(htmlTitle);
		
		
		textAreaGetTextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				htmlContent = htmlTa.getText();
				if (htmlContent.length() == 0) {
					JOptionPane.showMessageDialog(Html2TxtPanel.this,
							"请输入网页内容！");
				} else {
					if (htmlContent.toLowerCase().contains("<html>"))
						textContent = wht.parseHtmlString(htmlContent);
					else
						textContent = wht.parseBodyString(htmlContent);
					textTa.setText(textContent);
				}
			}
		});
		// 正文布局设置
		this.add(textSp);
		textSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textSp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textTa.setLineWrap(true);// 自动换行
		textSp.setBorder(textTitle);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
