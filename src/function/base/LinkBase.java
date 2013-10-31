package function.base;



public class LinkBase {

	public String srcName="";
	public String desName="";
	public int Pos;
	
	public boolean isTermSet;
	public boolean isESrcDes;
	public String achorText;
	public boolean isAchorEDesName;
	public String deltaText;
	public int logicPos;
	public boolean isInBox;
	public int sequence;
	public double sequence2;
	
	public boolean isFirstOfDes;
	public int repeatNum;
	public int linkMode;
	
	public String toString(){
		String sep=";";
		return srcName +sep 
		+ desName+sep
		+ Pos+sep
		+ isTermSet+sep
		+ isESrcDes+sep
		+ isInBox+sep
		+ achorText+sep
		+ isAchorEDesName+sep
		+ deltaText+sep
		+ logicPos+sep
		+ isFirstOfDes+sep
		+ repeatNum+sep
		+ linkMode;
	}

	
}
