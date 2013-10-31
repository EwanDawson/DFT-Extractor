package facetGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class MainFrame_old extends JFrame {

	private static final long serialVersionUID = -4209263941008740114L;
	/**
	 * @param args
	 */

	public MainFrame_old() {
		// 初始化一个容器
		Container c = this.getContentPane();
		// frame设置
		setTitle("分面分类体系构建平台");
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(300, 200);
		setSize(1020, 780);
		c.setBackground(Color.WHITE);
		try {
			Properties props = new Properties();
            props.put("logoString", "my company");
            
            com.jtattoo.plaf.mcwin.McWinLookAndFeel.setCurrentTheme(props);
			//com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Pink", "", "my company");
            // Select the Look and Feel
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");

            // Start the application
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        JTabbedPane funPane = new JTabbedPane();// 功能面板
    	CrawlerPanel jpCrawler = new CrawlerPanel();// 网络爬虫面板
    	Html2TxtPanel jpHtml2Txt = new Html2TxtPanel();// HTML正文提取面板
    	LinkFeaturePanel jpLinkFeature = new LinkFeaturePanel();// 链接特征分析面板
    	TextFeaturePanel jpTextFeature = new TextFeaturePanel();// 文本特征分析面板
    	NetFeaturePanel jpNetFeature = new NetFeaturePanel();// 网络特征分析面板
    	DTExtractionPanel jpDTExtraction = new DTExtractionPanel();// 领域术语抽取面板
    	HypRelationPanel_old jpHyponymy = new HypRelationPanel_old();// 上下位关系识别面板
    	FacetedTreePanel jpTaxonomy = new FacetedTreePanel();// 分类体系构建面板
		// 功能面板设置
		funPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// 功能面板布局
		c.add(funPane, BorderLayout.CENTER);
		funPane.addTab("网络爬虫", jpCrawler);
		funPane.addTab("HTML正文提取", jpHtml2Txt);
		funPane.addTab("链接特征分析", jpLinkFeature);
		funPane.addTab("文本特征分析", jpTextFeature);
		funPane.addTab("网络特征分析", jpNetFeature);
		funPane.addTab("领域术语抽取", jpDTExtraction);
		funPane.addTab("上下位关系识别", jpHyponymy);
		funPane.addTab("分类体系构建", jpTaxonomy);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame_old();
	}

}
