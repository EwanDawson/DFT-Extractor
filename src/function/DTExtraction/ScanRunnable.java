package function.DTExtraction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;

import twaver.Element;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.chart.BarChart;
import twaver.table.TElementTable;
import twaver.table.TTableModel;
import function.util.FileUtil;
import function.util.SetUtil;

public class ScanRunnable implements Runnable {

	private String path = "";// 要扫描的配置文件路径
	private DefaultListModel listModel = null;
	private TTableModel tableModel = null;
	private JList list = null;
	private TDataBox chartBox = null;
	private BarChart barChart = null;
	private Element e1, e2;
	private JButton btn = null;

	public ScanRunnable(String path, DefaultListModel listModel, JList list) {
		this.path = path;
		this.listModel = listModel;
		this.list = list;
	}// 列表构造函数

	public ScanRunnable(String path, TDataBox chartBox, BarChart barChart,
			Element e1, Element e2) {
		this.path = path;
		this.chartBox = chartBox;
		this.barChart = barChart;
		this.e1 = e1;
		this.e2 = e2;
	}// chart构造函数

	public ScanRunnable(String path, TTableModel tableModel, TElementTable table) {
		this.path = path;
		this.tableModel = tableModel;
	}// 表格构造函数

	public ScanRunnable(String path, JButton btn) {
		this.path = path;
		this.btn = btn;
	}// 按钮构造函数

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File f = new File(path);
		while (!f.exists()) {
			// 不存在停留在此处
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 防止文件读取错误
		if (listModel != null) {
			addList();
			f.delete();// 方便下次显示
		} else if (tableModel != null) {
			addTable();
			f.delete();// 方便下次显示
		} else if (chartBox != null) {
			addChart();
			f.delete();// 方便下次显示
		} else {
			setBtnText();
			f.delete();// 方便下次显示
		}
	}

	public void addList() {
		listModel.clear();
		list.updateUI();
		Vector<String> vConf = SetUtil.readSetFromFile(path);
		for (String s : vConf) {
			String pagePath = s.substring(0, s.indexOf(","));
			int sum = Integer.valueOf(s.substring(s.indexOf(",") + 1,
					s.length()));
			if (ExtractorUtil.scanDirPage(pagePath).size() >= sum) {
				HashMap<String, String> hm = ExtractorUtil
						.scanDirPage(pagePath);
				Iterator<String> it = hm.keySet().iterator();
				while (it.hasNext()) {
					String tempPageName = it.next();
					listModel.addElement(tempPageName);
					list.updateUI();
				}
			}// 已经爬取完成
			else {
				Vector<String> vExistPage = new Vector<String>();
				HashMap<String, String> hm = ExtractorUtil
						.scanDirPage(pagePath);
				while (vExistPage.size() < sum) {
					hm.clear();
					hm = ExtractorUtil.scanDirPage(pagePath);
					Vector<String> vNewAddPage = ExtractorUtil.getNewAddPage(
							vExistPage, hm);
					vExistPage.addAll(vNewAddPage);
					for (String pageName : vNewAddPage) {
						listModel.addElement(pageName);
						list.updateUI();
					}
				}
			}// 未爬取完成
		}
	}// 列表添加

	public void addTable() {
		tableModel.clearRawData();
		Vector<String> vDetail = SetUtil.readSetFromFile(path);
		for (int i = 0; i < vDetail.size(); i++) {
			Vector<String> row = new Vector<String>();
			String record[] = vDetail.get(i).split("@@@");
			for (int j = 0; j < record.length - 1; j++) {
				row.add(record[j]);
			}
			tableModel.addRow(row);
		}
	}// 表格添加

	public void addChart() {
		Vector<String> vNumber = SetUtil.readSetFromFile(path);
		int crawlNumber[] = new int[vNumber.size()];
		int filterNumber[] = new int[vNumber.size()];
		for (int i = 0; i < vNumber.size(); i++) {
			String record[] = vNumber.get(i).split("@@@");
			crawlNumber[i] = Integer.valueOf(record[0]);
			filterNumber[i] = Integer.valueOf(record[1]);
		}
		barChart.setBarType(TWaverConst.BAR_TYPE_GROUP);
		int gap = crawlNumber[crawlNumber.length - 1] / 10;
		gap = gap / 100 * 100;
		barChart.setYScaleValueGap(gap);
		chartBox.removeElement(e1);
		chartBox.removeElement(e2);
		e1.clearChartValues();
		e2.clearChartValues();
		List<String> xScaleList=new ArrayList<String>();
		for (int i = 0; i < crawlNumber.length; i++) {
			String xScaleText = "Layer" + (i + 1);
			xScaleList.add(xScaleText);
			barChart.addXScaleText(xScaleText);
			e1.addChartValue(crawlNumber[i]);
			e2.addChartValue(filterNumber[i]);
		}
		barChart.setXScaleTextList(xScaleList);
		chartBox.addElement(e1);
		chartBox.addElement(e2);
	}// chart

	public void setBtnText() {
		String text = FileUtil.readFile(path);
		btn.setText(text);
	}// button

}// end runnable Class
