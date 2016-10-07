package com.harrykbd.selfstock;

public class Stock extends Object{
	public String Code;
	public String Name;
	public String trdDd; 
	public long clsprc; 
	public String cmpprevddSgn; 
	public long cmpprevddAmt; 
	public long accTrdvol; 
	public long accTrdval; 
	public long opnprc; 
	public long hgprc; 
	public long lwprc;
	public long prevddClsprc;
	public long isuTrdvol;
	public double cmpprevddStkprcFluRt;
	public String trdTm; //only used in the real time price mode. (HH:MM:SS)

	public Stock(String Code, String Name, String trdDd, long clsprc, String cmpprevddSgn, long cmpprevddAmt, long accTrdvol, long accTrdval, long opnprc, long hgprc, long lwprc, long prevddClsprc, long isuTrdvol, double cmpprevddStkprcFluRt){
		this.Code = Code;
		this.Name = Name;
		this.trdDd = trdDd;
		this.clsprc = clsprc;
		this.cmpprevddSgn = cmpprevddSgn;
		this.cmpprevddAmt = cmpprevddAmt;
		this.accTrdvol = accTrdvol;
		this.accTrdval = accTrdval;
		this.opnprc = opnprc;
		this.hgprc = hgprc;
		this.lwprc = lwprc;
		this.prevddClsprc = prevddClsprc;
		this.isuTrdvol = isuTrdvol;
		//this.cmpprevddStkprcFluRt = (double)(((long)(cmpprevddStkprcFluRt * 100))/100);
		this.cmpprevddStkprcFluRt = cmpprevddStkprcFluRt;
		this.trdTm = "NA";
	}
	
	public Stock(String Code, String Name, String trdDd, String trdTm){
		this.Code = Code;
		this.Name = Name;
		this.trdDd = trdDd;
		this.clsprc = 0;
		this.cmpprevddSgn = "+";
		this.cmpprevddAmt = 0;
		this.accTrdvol = 0;
		this.accTrdval = 0;
		this.opnprc = 0;
		this.hgprc = 0;
		this.lwprc = 0;
		this.prevddClsprc = 0;
		this.isuTrdvol = 0;
		this.cmpprevddStkprcFluRt = 0.0;
		this.trdTm = trdTm;
	}

	public String toString(){
		return Code + " " + Name + " " + trdDd + " " + clsprc;
	}
	public boolean equals(Object object){
		Stock pair = (Stock)object;
		if(Code.equals(pair.Code) &&
				Name.equals(pair.Name) &&
				trdDd.equals(pair.trdDd) &&
				clsprc == pair.clsprc &&
				cmpprevddSgn.equals(pair.cmpprevddSgn) &&
				cmpprevddAmt == pair.cmpprevddAmt &&
				accTrdvol == pair.accTrdvol &&
				accTrdval == pair.accTrdval &&
				opnprc == pair.opnprc &&
				hgprc == pair.hgprc &&
				lwprc == pair.lwprc &&
				prevddClsprc == pair.prevddClsprc &&
				isuTrdvol == pair.isuTrdvol){
				//cmpprevddStkprcFluRt == pair.cmpprevddStkprcFluRt){
			return true;
		}
		else{
			return false;
		}
	}
};
