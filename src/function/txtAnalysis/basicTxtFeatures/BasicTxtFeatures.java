package function.txtAnalysis.basicTxtFeatures;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import function.base.SheetBase;
/*
 * 文本文件相关特征。依赖锚文本及增量字符
 * 
 */
public class BasicTxtFeatures extends SheetBase {

	
	MOREConfig mc = null;
	LocalCorpus DataMining=null;
	
	Vector<Integer> ParaRange=new Vector<Integer>();
	public BasicTxtFeatures(MOREConfig mc) {
		this.mc=mc;
		this.DataMining = new LocalCorpus(mc);
	}
	@Override
	public void processing() throws RowsExceededException, WriteException,
			FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		String previous="";
		StringBuffer Buffer=null;
		int pos=0;
		int size = getRows();
		//size=51;
		for (int i = 1; i <size; i++) {

			String srcTerm = getStringValue("sourceURLName", i);
			String archorText = getStringValue("AnchorText", i);
			String delatText = getStringValue("DeltaText", i);
			if (!srcTerm.equals(previous)) {
				ParaRange=new Vector<Integer>();
				Buffer = DataMining.getTextFileBuffer(srcTerm, ParaRange);
				previous = srcTerm;
				pos = 0;
			}

			pos = getTextFeatures(archorText, delatText, pos, Buffer);
			setNumberValue("PosInTxt", i, pos);
			setNumberValue("txtLen", i, Buffer.length());
			setNumberValue("PosInTxt2", i, (double) pos / Buffer.length());

			int paraIndex = getPosInPage(pos, 0, ParaRange);
			System.out.println("para:" + paraIndex);

			setNumberValue("ParaInTxt", i, paraIndex);
			setNumberValue("ParaNum", i, (ParaRange.size() - 1));
			setNumberValue("ParaInTxt2", i,(double) paraIndex / (ParaRange.size() - 1));

			int posInPara = pos - ParaRange.get(paraIndex - 1);
			setNumberValue("posInPara", i, posInPara);
			int paraLen = ParaRange.get(paraIndex)- ParaRange.get(paraIndex - 1);
			setNumberValue("ParaLen", i, paraLen);
			setNumberValue("posInPara2", i, (double) posInPara / paraLen);
		}

	}

	public  void testSingleTxtFile(){
		
		String term="Logistic_regression";
		term="AdaBoost";
		String archorText="Clustering";
		String delatText=" algorithms (";
		
		StringBuffer Buffer = DataMining.getTextFileBuffer(term,ParaRange);
		
		for(int i=1;i<ParaRange.size();i++){
			//System.out.println(i+":"+Buffer.substring(ParaRange.get(i-1), ParaRange.get(i)));

		}
		
		int pos=getTextFeatures(archorText,delatText,0,Buffer);
		int posInPage=getPosInPage(pos,0,ParaRange);
		System.out.println("para:"+posInPage);
		System.out.println("PosInTxt2:"+(double) pos / Buffer.length());
	}
	
	//查找特定锚文本在text文件中的位置
	//返回值如果为1-1，表示未找到
	public int getTextFeatures(String achorText,String deltaText,int startPos, StringBuffer Buffer){
		String text=achorText + deltaText;
		//int temp=Buffer.indexOf(achorText);
		int pos=Buffer.indexOf(text,startPos);
		
		//异常处理1：特殊字符替换
		if(-1 == pos){
			text=text.replace("&amp;", "&");
			pos=Buffer.indexOf(text,startPos);
		}
		//异常处理1：忽略部分特殊字符
		int skip=8;
		if(-1 == pos && deltaText.length()> skip){			
			String newDeltaText = deltaText.substring(skip);
			pos=Buffer.indexOf(achorText,startPos);
			int deltaPos=Buffer.indexOf(newDeltaText,pos+skip);
			if((deltaPos-pos)>(10+skip+achorText.length())) pos=-1;
			
		}
		System.out.println(text);
		return pos;
	}
	
	public int getPosInPage(int pos, int startIndex,Vector<Integer> ParaRange){
		assert(pos >= ParaRange.get(startIndex));
		int i=startIndex+1;
		for(;i<ParaRange.size();i++){
			if(pos<ParaRange.get(i)) break;
		}
		return i;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BasicTxtFeatures btf=new BasicTxtFeatures(new MOREConfig());

		btf.testSingleTxtFile();

	}

}
