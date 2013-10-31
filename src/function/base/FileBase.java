package function.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public abstract class FileBase {

	public static String basePath = "";
	// 读单个文件变量
	private FileReader fr = null;
	private BufferedReader br = null;
	private Vector<String> rv = new Vector<String>();
	private HashMap<String, Integer> titleh = new HashMap<String, Integer>();
	// 写单个文件变量
	private FileWriter fw = null;
	private BufferedWriter bw = null;
	private Vector<String> wv = new Vector<String>();
	// 读多个文件变量
	private Vector<FileReader> frv = new Vector<FileReader>();
	private Vector<BufferedReader> brv = new Vector<BufferedReader>();
	private Vector<Vector<String>> drv = new Vector<Vector<String>>();
	private Vector<HashMap<String, Integer>> dtitleh = new Vector<HashMap<String, Integer>>();
	private HashMap<String, Integer> rFileNameh = new HashMap<String, Integer>();
	// 写多个文件变量
	private Vector<FileWriter> fwv = new Vector<FileWriter>();
	private Vector<BufferedWriter> bwv = new Vector<BufferedWriter>();
	private Vector<Vector<String>> dwv = new Vector<Vector<String>>();
	private HashMap<String, Integer> wFileNameh = new HashMap<String, Integer>();

	public abstract void process() throws IOException;
	public abstract void run(FileBase fb);
	
	public void ropen(String FileName) {
		System.out.println("正在将文件" + FileName + "读入内存……");
		try {
			fr = new FileReader(basePath + FileName);
			br = new BufferedReader(fr);
			String temp = br.readLine();
			String title[] = temp.split(",");
			for (int i = 0; i < title.length; i++) {
				titleh.put(title[i], i);
			}
			while (temp != null) {
				rv.add(temp);
				temp = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dropenWithClose(String[] FileName) {
		try {
			for (int i = 0; i < FileName.length; i++) {
				System.out.println("正在将第" + (i+1) + "个文件" + FileName[i] + "读入内存……");
				rFileNameh.put(FileName[i], i);
				frv.add(new FileReader(basePath + FileName[i]));
				brv.add(new BufferedReader(frv.elementAt(i)));
				Vector<String> tempV = new Vector<String>();
				String temp = brv.elementAt(i).readLine();
				HashMap<String, Integer> temptitleh = new HashMap<String, Integer>();
				String title[] = temp.split(",");
				for (int n = 0; n < title.length; n++) {
					temptitleh.put(title[n], n);
				}
				dtitleh.add(temptitleh);
				while (temp != null) {
					tempV.add(temp);
					temp = brv.elementAt(i).readLine();
				}
				drv.add(tempV);
				System.out.println("关闭第" + (i+1) + "个文件" + FileName[i] + "，释放资源……");
				brv.elementAt(i).close();
				frv.elementAt(i).close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rclose() {
		System.out.println("关闭文件，释放资源……");
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void wopen(String FileName) {
		System.out.println("创建写入文件" + FileName + "……");
		try {
			fw = new FileWriter(basePath + FileName);
			bw = new BufferedWriter(fw);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dwopen(String[] FileName) {
		try {
			for (int i = 0; i < FileName.length; i++) {
				wFileNameh.put(FileName[i], i);
				System.out.println("创建写入文件" + FileName[i] + "……");
				fwv.add(new FileWriter(basePath + FileName[i]));
				bwv.add(new BufferedWriter(fwv.elementAt(i)));
				bwv.elementAt(i).flush();
				dwv.add(new Vector<String>());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void wclose() {
		System.out.println("正在写入文件中……");
		try {
			for (int i = 0; i < wv.size(); i++) {
				bw.write(wv.elementAt(i));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
			System.out.println("写入完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void dwclose() {
		try {
			for (int i = 0; i < bwv.size(); i++) {
				String temp;
				System.out.println("正在写入第" + (i+1)+ "个文件中……");
				for (int k = 0; k < dwv.elementAt(i).size(); k++) {
					temp = dwv.elementAt(i).elementAt(k);
					bwv.elementAt(i).write(temp);
					bwv.elementAt(i).newLine();
				}
				System.out.println("写入第" + (i+1)+ "个文件完毕，关闭第" + (i+1) + "个文件流！");
				bwv.elementAt(i).flush();
				bwv.elementAt(i).close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void go(String FileName, char rw) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		if (rw == 'r') {
			ropen(FileName);
			rclose();
			try {
				System.out
						.println("-------------------操作进行中---------------------");
				process();
				System.out
						.println("--------------------操作完毕----------------------");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (rw == 'w') {
			try {
				System.out
						.println("-------------------操作进行中---------------------");
				process();
				System.out
						.println("--------------------操作完毕----------------------");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wopen(FileName);
			wclose();
		}
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}

	public void go(String[] FileName, char rw) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		if (rw == 'r') {
			dropenWithClose(FileName);
			try {
				System.out
						.println("-------------------操作进行中---------------------");
				process();
				System.out
						.println("--------------------操作完毕----------------------");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (rw == 'w') {
			dwopen(FileName);
			try {
				System.out
						.println("-------------------操作进行中---------------------");
				process();
				System.out
						.println("--------------------操作完毕----------------------");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dwclose();
		}
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}

	public void go(String[] drFileName, String[] dwFileName) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		dropenWithClose(drFileName);
		dwopen(dwFileName);
		try {
			System.out.println("-------------------操作进行中---------------------");
			process();
			System.out
					.println("--------------------操作完毕----------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dwclose();
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}
	public void go(String[] drFileName, String wFileName) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		dropenWithClose(drFileName);
		wopen(wFileName);
		try {
			System.out.println("-------------------操作进行中---------------------");
			process();
			System.out
					.println("--------------------操作完毕----------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wclose();
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}
	public void go(String rFileName, String[] dwFileName) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		ropen(rFileName);
		rclose();
		dwopen(dwFileName);
		try {
			System.out.println("-------------------操作进行中---------------------");
			process();
			System.out
					.println("--------------------操作完毕----------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dwclose();
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}
	public void go(String rFileName, String wFileName) {
		System.out.println("开始执行……");
		long  start=System.currentTimeMillis();
		ropen(rFileName);
		rclose();
		try {
			System.out.println("-------------------操作进行中---------------------");
			process();
			System.out
					.println("--------------------操作完毕----------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wopen(wFileName);
		wclose();
		System.out.println("执行完毕！");
		long end=System.currentTimeMillis();
		System.out.println("花费：" + (end - start) + "毫秒");
	}

	public String getStringValue(String ColumnName, int Row) {
		String temp = rv.elementAt(Row);
		String s[] = temp.split(",");
		return s[titleh.get(ColumnName)];
	}

	public String getStringValue(String FileName, String ColumnName, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		String s[] = temp.split(",");
		return s[dtitleh.elementAt(ID).get(ColumnName)];
	}
	public String getStringValue(int Column, int Row) {
		String temp = rv.elementAt(Row);
		String s[] = temp.split(",");
		return s[Column];
	}
	
	public String getStringValue(String FileName, int Column, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		String s[] = temp.split(",");
		return s[Column];
	}
	
	public int getIntegerValue(String ColumnName, int Row) {
		String temp = rv.elementAt(Row);
		String s[] = temp.split(",");
		return Integer.parseInt(s[titleh.get(ColumnName)]);
	}

	public int getIntegerValue(String FileName, String ColumnName, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		String s[] = temp.split(",");
		return Integer.parseInt(s[dtitleh.elementAt(ID).get(ColumnName)]);
	}

	public double getDoubleValue(String ColumnName, int Row) {
		String temp = rv.elementAt(Row);
		String s[] = temp.split(",");
		return Double.parseDouble(s[titleh.get(ColumnName)]);
	}

	public int getIntegerValue(String FileName, int Column, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		String s[] = temp.split(",");
		return Integer.parseInt(s[Column]);
	}
	
	public int getIntegerValue(int Column, int Row) {
		String temp = rv.elementAt(Row);
		String s[] = temp.split(",");
		return Integer.parseInt(s[Column]);
	}
	
	public double getDoubleValue(String FileName, String ColumnName, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		String s[] = temp.split(",");
		return Double.parseDouble(s[dtitleh.elementAt(ID).get(ColumnName)]);
	}

	public String getLineValue(int Row) {
		String temp = rv.elementAt(Row);
		return temp;
	}

	public String getLineValue(String FileName, int Row) {
		int ID = rFileNameh.get(FileName);
		String temp = drv.elementAt(ID).elementAt(Row);
		return temp;
	}
	
	public Vector<String> getAllLine(){
		return rv;
	}
	
	public Vector<String> getAllLine(String FileName){
		int ID = rFileNameh.get(FileName);
		Vector<String> v=drv.elementAt(ID);
		return v;
	}

	public void write(String FileName, String s) {
		int ID = wFileNameh.get(FileName);
		dwv.elementAt(ID).add(s);
	}

	public void write(String s) {
		wv.add(s);
	}

	public int getRows() {
		return rv.size();
	}

	public int getRows(String FileName) {
		int ID = rFileNameh.get(FileName);
		return drv.elementAt(ID).size();
	}
}
