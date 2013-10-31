package function.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		// TODO Auto-generated method stub
		new DownloadFile().downloadFanmod();
	}
	
	public void downloadFanmod(){
		URL fanmodPath=getClass().getResource("/resources/fanmod.exe");
		String desPath="C:\\Program Files\\fanmod.exe";
		try {
			downloadNet(fanmodPath,desPath);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void downloadNet(URL srcUrl,String desPath) throws MalformedURLException {
		// ÏÂÔØÍøÂçÎÄ¼þ
		int bytesum = 0;
		int byteread = 0;

		try {
			URLConnection conn = srcUrl.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(desPath);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
