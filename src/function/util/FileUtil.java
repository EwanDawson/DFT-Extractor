package function.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

/**
 * @author MJ
 * @description 1).复制文件夹或文件夹    2).读取文件成字符串    3).删除文件
 */
public class FileUtil {

    public static void main(String args[]) throws IOException {
        // 源文件夹
        String url1 = "F:/layer3.txt";
        // 目标文件夹
        String url2 = "F:/layer3-copy.txt";
        // 创建目标文件夹
        copyFile(new File(url1),new File(url2));
    }

    /**
	 * 获取dir目录下的文件名称集合，去除了后缀名
	 * @param dir
	 * @return
	 */
	public static Vector<String> getDirFileSet(String dir){
		Vector<String> vFile=new Vector<String>();
		File f=new File(dir);
		File childs[]=f.listFiles();
		for(int i=0;i<childs.length;i++){
			String fileName=childs[i].getName();
			String fileNamePrefix=fileName;
			if(fileName.contains("."))
			 fileNamePrefix=fileName.substring(0, fileName.lastIndexOf("."));
			vFile.add(fileNamePrefix);
		}
		return vFile;
	}
    
    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                System.out.println("copy:"+(i+1)+"/"+file.length);
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 
     * @param srcFileName
     * @param destFileName
     * @param srcCoding
     * @param destCoding
     * @throws IOException
     */
    public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
            char[] cbuf = new char[1024 * 5];
            int len = cbuf.length;
            int off = 0;
            int ret = 0;
            while ((ret = br.read(cbuf, off, len)) > 0) {
                off += ret;
                len -= ret;
            }
            bw.write(cbuf, 0, off);
            bw.flush();
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
        }
    }

    /**
     * 
     * @param filepath
     * @throws IOException
     */
    public static void del(String filepath) throws IOException {
        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                f.delete();
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
            }
        }
        else f.delete();
    }
    
    /**
     * 
     * @param filePath 要读取的文件路径
     * @return 文件字符串
     */
    public static String readFile(String filePath){
    	FileReader fr;
    	StringBuffer sb=new StringBuffer();
    	File f=new File(filePath);
    	if(f.exists()){
    		try {
    			fr = new FileReader(filePath);
    			BufferedReader br=new BufferedReader(fr);
    			String temp=br.readLine();
    			while(temp!=null){
    				sb=sb.append(temp);
    				temp=br.readLine();
    			}
    			br.close();
    			fr.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	String s=sb.toString();
    	sb.delete(0, sb.length());
		return s;
    }
    
    /**
     * 将s写到filePath里面
     * @param s
     * @param filePath
     */
    public static void writeStringFile(String s,String filePath){
    	try{
    		FileWriter fw=new FileWriter(filePath);
        	BufferedWriter bw=new BufferedWriter(fw);
        	bw.write(s);
        	bw.close();
        	fw.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void appendStringFile(String s,String filePath){
    	try{
    		FileWriter fw=new FileWriter(filePath,true);
        	BufferedWriter bw=new BufferedWriter(fw);
        	bw.newLine();
        	bw.write(s);
        	bw.close();
        	fw.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}

