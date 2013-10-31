package function.netAnalysis.motif;

import java.io.File;
import java.io.IOException;

import function.util.DownloadFile;

public class FanmodCompute {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String
		// task="cmd /C Start "+"f:\\fanmod.exe 3 100000 1 f:\\DM2_Edge.txt 1 0 0 2 no no no 1000 3 3 f:\\DM2_Edge_m3.txt 0 1";
		new FanmodCompute().findMotif3UseFanmod();
	}

	/**
	 * 使用软件计算motif
	 */
	public void findMotif3UseFanmod() {
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);
		curDir = curDir.replaceAll(" ", "\" \"");
		String fanmodPath = "C:\\Program Files\\fanmod.exe";
		File f = new File(fanmodPath);
		if (!f.exists()){
			new DownloadFile().downloadFanmod();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// URL fanmodPath=getClass().getResource("/resources/fanmod.exe");
		// String task="cmd /C Start "+curDir+"\\fanmod.exe";
		fanmodPath=fanmodPath.replaceAll(" ", "\" \"");
		String task = "cmd /C Start " + fanmodPath;
		try {
			Runtime.getRuntime().exec(task);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param edgeFile
	 *            边文件，后缀是_Edge.txt
	 */
	public void findMotif3(String edgeFileName) {

	}

	/**
	 * 
	 * @param edgeFile
	 *            边文件，后缀是_Edge.txt
	 */
	public void findMotif4(String edgeFileName) {

	}

}
