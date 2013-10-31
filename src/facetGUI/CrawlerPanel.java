package facetGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import function.crawler.WebCrawler;
import function.crawler.WikiCategoryCrawler;
import function.crawler.WikiHistoryCrawler;

public class CrawlerPanel extends JPanel {

	private static final long serialVersionUID = 9008861303227633496L;
	private HashMap<String, String> hm = new HashMap<String, String>();// 保存网页名称和存放地址的映射
	
	private JTabbedPane funPane = new JTabbedPane(JTabbedPane.TOP);// 功能面板
	private JPanel urlCrawlPanel = new JPanel();//url爬取面板
	private JPanel fileCrawlPanel = new JPanel();//术语集文件爬取面板
	private JPanel historyCrawlPanel = new JPanel();//历史页面爬取面板
	private JPanel categoryCrawlPanel = new JPanel();//目录爬取面板
	
	private JLabel urlLabel = new JLabel("  请输入HTML源URL:");
	private JTextField urlTf = new JTextField();
	private JLabel urlSaveLabel = new JLabel("  请选择保存路径：");
	private JTextField urlSaveTf = new JTextField();
	private JButton urlSaveFilechooseBtn = new JButton("浏览...");
	private JLabel layerLabel = new JLabel("  请选择爬取层次：");
	private String[] layer = { "1", "2", "3", "4", "5", "6", "7", "8" };
	private JComboBox jcb = new JComboBox(layer);
	private JButton urlCrawlBtn = new JButton("爬取网页");
	private JLabel termSetLabel = new JLabel("  请选择术语集文件：");
	private JTextField termSetTf = new JTextField();
	private JButton termSetFilechooseBtn = new JButton("浏览...");
	private JLabel termSetSaveLabel = new JLabel("  请选择保存路径：");
	private JTextField termSetSaveTf = new JTextField();
	private JButton termSetSaveFilechooseBtn = new JButton("浏览...");
	private JButton termSetCrawlBtn = new JButton("爬取网页");
	private JLabel file2Label = new JLabel("  请选择术语集文件：");
	private JTextField file2Tf = new JTextField();
	private JButton file2chooseBtn = new JButton("浏览...");
	private JButton historyCrawlBtn = new JButton("历史爬取");
	private JLabel file3Label = new JLabel("  请选择目录术语集文件：");
	private JTextField file3Tf = new JTextField();
	private JButton file3chooseBtn = new JButton("浏览...");
	private JButton categoryCrawlBtn = new JButton("目录爬取");
	private int selectedLayer = 1;
	private Border border = BorderFactory.createEtchedBorder(Color.white,
			Color.gray);
	private Border title = BorderFactory.createTitledBorder(border, "爬取结果",
			TitledBorder.LEFT, TitledBorder.TOP);
	private DefaultListModel dlm = new DefaultListModel();
	private JList pageList = new JList(dlm);
	private JScrollPane jsp = new JScrollPane(pageList);
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem jit = new JMenuItem("删除");

	public CrawlerPanel() {
		setLayout(null);
		
		this.setBackground(Color.white);
		funPane.setBounds(0, 10, 1000, 200);
		jsp.setBounds(0, 230, 1000, 370);
		
		//功能选项卡布局
		this.add(funPane);
		funPane.addTab("根据源url爬取",urlCrawlPanel);
		funPane.addTab("根据术语集文件爬取",fileCrawlPanel);
		funPane.addTab("历史页面爬取",historyCrawlPanel);
		funPane.addTab("目录爬取",categoryCrawlPanel);

		// 按URL爬取面板布局设置
		urlCrawlPanel.add(urlLabel);
		urlCrawlPanel.add(urlTf);
		urlCrawlPanel.add(urlSaveLabel);
		urlCrawlPanel.add(urlSaveTf);
		urlCrawlPanel.add(urlSaveFilechooseBtn);
		urlCrawlPanel.add(layerLabel);
		urlCrawlPanel.add(jcb);
		urlCrawlPanel.add(urlCrawlBtn);
		urlCrawlPanel.setLayout(null);
		urlCrawlPanel.setBackground(new Color(230,239,248));
		
		urlLabel.setBounds(10, 10, 150, 25);
		urlTf.setBounds(155, 10, 400, 25);
		urlSaveLabel.setBounds(10, 50, 150, 25);
		urlSaveTf.setBounds(155, 50, 400, 25);
		urlSaveFilechooseBtn.setBounds(570, 50, 60, 25);
		layerLabel.setBounds(10,90,150,25);
		jcb.setBounds(155, 90, 40, 25);
		urlCrawlBtn.setBounds(550, 110, 85, 25);

		jcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedLayer = jcb.getSelectedIndex() + 1;
			}
		});
		final JFileChooser fileSave = new JFileChooser("");
		urlSaveFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (selectedLayer != 1)
					fileSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				else
					fileSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileSave.showSaveDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = fileSave.getSelectedFile().getAbsolutePath();
					urlSaveTf.setText(path);
				}
			}
		});

		urlCrawlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String url = urlTf.getText();
				if (url.length() == 0) {
					JOptionPane
							.showMessageDialog(CrawlerPanel.this, "请输入爬取路径！");
				} else {
					final WebCrawler wc = new WebCrawler();
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (selectedLayer == 1) {
								String filePath = urlSaveTf.getText();
								String dir = filePath.substring(0,
										filePath.lastIndexOf("\\") + 1);
								String fileName = filePath.substring(
										filePath.lastIndexOf("\\") + 1,
										filePath.length());
								wc.setFilePath(dir);
								wc.crawlPageByUrl(url, fileName);
								JOptionPane.showMessageDialog(
										CrawlerPanel.this, "结果文件已保存成"
												+ filePath);
							} else {
								wc.setFilePath(urlSaveTf.getText() + '/');
								wc.crawlPageByLayer(url, selectedLayer);
								try {
									Thread.currentThread();
									Thread.sleep(2000);// 休眠两秒钟，保证扫描完毕！
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								JOptionPane.showMessageDialog(
										CrawlerPanel.this, "结果文件已保存到"
												+ urlSaveTf.getText() + "下！");
							}

						}
					});
					Thread tDetect = new Thread(new Runnable() {
						@Override
						public void run() {
							dlm.clear();
							hm.clear();
							try {
								while (t.isAlive()) {
									HashMap<String, String> hmTemp = scanPage(
											urlSaveTf.getText(), hm);
									Iterator<String> it = hmTemp.keySet()
											.iterator();
									while (it.hasNext()) {
										String tempPageName = it.next();
										String tempPagePath = hmTemp
												.get(tempPageName);
										dlm.addElement(tempPageName);
										hm.put(tempPageName, tempPagePath);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// TODO Auto-generated method stub
						}
					});
					t.start();
					tDetect.start();
				}
			}
		});

		// 按术语集文件爬取面板布局设置
		fileCrawlPanel.add(termSetLabel);
		fileCrawlPanel.add(termSetTf);
		fileCrawlPanel.add(termSetFilechooseBtn);
		fileCrawlPanel.add(termSetSaveLabel);
		fileCrawlPanel.add(termSetSaveTf);
		fileCrawlPanel.add(termSetSaveFilechooseBtn);
		fileCrawlPanel.add(termSetCrawlBtn);
		fileCrawlPanel.setLayout(null);
		fileCrawlPanel.setBackground(new Color(230,239,248));
		
		termSetLabel.setBounds(10, 10, 150, 25);
		termSetTf.setBounds(155, 10, 400, 25);
		termSetFilechooseBtn.setBounds(570, 10, 60, 25);
		termSetSaveLabel.setBounds(10, 50, 150, 25);
		termSetSaveTf.setBounds(155, 50, 400, 25);
		termSetSaveFilechooseBtn.setBounds(570, 50, 60, 25);
		termSetCrawlBtn.setBounds(550, 90, 85, 25);
		termSetFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					termSetTf.setText(path);
				}
			}
		});
		final JFileChooser termSetSave = new JFileChooser("");
		termSetSaveFilechooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				termSetSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择文件夹
				int result = termSetSave.showSaveDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = termSetSave.getSelectedFile()
							.getAbsolutePath();
					termSetSaveTf.setText(path);
				}
			}
		});

		termSetCrawlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String path = termSetTf.getText();
				final String savePath = termSetSaveTf.getText();
				if (path.length() == 0) {
					JOptionPane.showMessageDialog(CrawlerPanel.this,
							"请选择术语集文件！");
				} else {
					final WebCrawler wc = new WebCrawler();
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							wc.crawlPageByList(path, savePath, 5);
							try {
								Thread.currentThread();
								Thread.sleep(2000);// 休眠两秒钟，保证扫描完毕！
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							JOptionPane.showMessageDialog(CrawlerPanel.this,
									"结果文件已保存到" + savePath + "下！");
						}
					});
					Thread tDetect = new Thread(new Runnable() {
						@Override
						public void run() {
							dlm.clear();
							hm.clear();
							try {
								while (t.isAlive()) {
									HashMap<String, String> hmTemp = scanPage(
											savePath, hm);
									Iterator<String> it = hmTemp.keySet()
											.iterator();
									while (it.hasNext()) {
										String tempPageName = it.next();
										String tempPagePath = hmTemp
												.get(tempPageName);
										dlm.addElement(tempPageName);
										hm.put(tempPageName, tempPagePath);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// TODO Auto-generated method stub
						}
					});
					t.start();
					tDetect.start();
				}

			}
		});

		// 历史页面爬取面板布局设置
		historyCrawlPanel.add(file2Label);
		historyCrawlPanel.add(file2Tf);
		historyCrawlPanel.add(file2chooseBtn);
		historyCrawlPanel.add(historyCrawlBtn);
		historyCrawlPanel.setLayout(null);
		historyCrawlPanel.setBackground(new Color(230,239,248));
		
		file2Label.setBounds(10, 30, 150, 25);
		file2Tf.setBounds(155, 30, 400, 25);
		file2chooseBtn.setBounds(570, 30, 60, 25);
		historyCrawlBtn.setBounds(550, 70, 85, 25);

		file2chooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					file2Tf.setText(path);
				}
			}
		});

		historyCrawlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String path = file2Tf.getText();
				if (path.length() == 0) {
					JOptionPane.showMessageDialog(CrawlerPanel.this,
							"请选择术语集文件！");
				} else {
					final WikiHistoryCrawler whc = new WikiHistoryCrawler();
					final Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							String historySavePath = "";
							// TODO Auto-generated method stub
							whc.crawlPageByList(path);
							if (path.contains("\\"))
								historySavePath = path.substring(0,
										path.lastIndexOf("."))
										+ "_history";
							else
								historySavePath = path.substring(0,
										path.lastIndexOf("."))
										+ "_history";
							JOptionPane.showMessageDialog(CrawlerPanel.this,
									"结果文件已保存到" + historySavePath + "下！");
						}
					});
					Thread tDetect = new Thread(new Runnable() {
						@Override
						public void run() {
							dlm.clear();
							hm.clear();
							try {
								while (t.isAlive()) {
									String historySavePath = "";
									if (path.contains("\\"))
										historySavePath = path.substring(0,
												path.lastIndexOf("."))
												+ "_history";
									else
										historySavePath = path.substring(0,
												path.lastIndexOf("."))
												+ "_history";
									HashMap<String, String> hmTemp = scanPage(
											historySavePath, hm);
									Iterator<String> it = hmTemp.keySet()
											.iterator();
									while (it.hasNext()) {
										String tempPageName = it.next();
										String tempPagePath = hmTemp
												.get(tempPageName);
										dlm.addElement(tempPageName);
										hm.put(tempPageName, tempPagePath);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// TODO Auto-generated method stub
						}
					});
					t.start();
					tDetect.start();
				}
			}
		});

		// 目录爬取面板布局设置

		categoryCrawlPanel.add(file3Label);
		categoryCrawlPanel.add(file3Tf);
		categoryCrawlPanel.add(file3chooseBtn);
		categoryCrawlPanel.add(categoryCrawlBtn);
		categoryCrawlPanel.setLayout(null);
		categoryCrawlPanel.setBackground(new Color(230,239,248));
		
		
		file3Label.setBounds(10, 30, 150, 25);
		file3Tf.setBounds(155, 30, 400, 25);
		file3chooseBtn.setBounds(570, 30, 60, 25);
		categoryCrawlBtn.setBounds(550, 70, 85, 25);

		file3chooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser file = new JFileChooser("");
				int result = file.showOpenDialog(null);
				// JFileChooser.APPROVE_OPTION是0，代表已经选择了文件
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获得你选择的文件绝对路径。并输出。当然，我们获得这个路径后还可以做很多的事。
					String path = file.getSelectedFile().getAbsolutePath();
					file3Tf.setText(path);
				}
			}
		});

		categoryCrawlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final String path = file3Tf.getText();
				if (path.length() == 0) {
					JOptionPane.showMessageDialog(CrawlerPanel.this,
							"请选择目录术语集文件！");
				} else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							WikiCategoryCrawler wcc = new WikiCategoryCrawler();
							wcc.buildCategoryTree(path,4);
							String cvsCategoryFile = "";
							if (path.contains("\\"))
								cvsCategoryFile = path.substring(0,
										path.lastIndexOf("."))
										+ "_category.csv";
							else
								cvsCategoryFile = path.substring(0,
										path.lastIndexOf("."))
										+ "_category.csv";
							JOptionPane.showMessageDialog(CrawlerPanel.this,
									"结果文件已保存到" + cvsCategoryFile + "下！");
						}
					});
					t.start();
				}

			}
		});

		// 显示爬取的頁面
		this.add(jsp,BorderLayout.SOUTH);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBorder(title);
		
		popupMenu.add(jit);
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
		// pageList添加右键删除功能
		pageList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pageList.setSelectedIndex(pageList.locationToIndex(e.getPoint())); // 获取鼠标点击的项
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		jit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String selectedPage = (String) dlm.getElementAt(pageList
						.getSelectedIndex());
				dlm.removeElementAt(pageList.getSelectedIndex());
				String filePath = hm.get(selectedPage);
				File f = null;
				if (filePath != null) {
					f = new File(filePath);
					f.delete();
				}
			}

		});
	}

	/**
	 * 
	 * @param dir
	 * @param vExistPage
	 * @return dir目录下除去vExistPage的页面路径映射
	 */
	public HashMap<String, String> scanPage(String dir,
			HashMap<String,String> hmExistPage) {
		HashMap<String, String> hm = new HashMap<String, String>();
		HashMap<String, String> hmResult = new HashMap<String, String>();
		hm = scanDirPage(dir, hm);
		Iterator<String> it = hm.keySet().iterator();
		while (it.hasNext()) {
			String pageName = it.next();
			if (!hmExistPage.containsKey(pageName))
				hmResult.put(pageName, hm.get(pageName));
		}
		return hmResult;
	}

	/**
	 * 
	 * @param dir
	 * @param hm
	 *            存放网页名称和路径
	 * @return dir目录下的网页
	 */
	public HashMap<String, String> scanDirPage(String dir,
			HashMap<String, String> hm) {
		File f = new File(dir);
		File childs[] = f.listFiles();
		for (int i = 0; i < childs.length; i++) {
			if (childs[i].isDirectory())
				scanDirPage(childs[i].getAbsolutePath(), hm);
			else if (childs[i].getName().endsWith(".html"))
				hm.put(childs[i].getName(), childs[i].getAbsolutePath());
			else
				continue;
		}
		return hm;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
