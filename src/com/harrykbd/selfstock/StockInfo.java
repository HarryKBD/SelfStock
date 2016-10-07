package com.harrykbd.selfstock;

public class StockInfo {
	public String isuCd;
	public String isuSrtCd;
	public String isuKorNm;
	public String isuKorAbbrv;
	public int market;  //1: Kospi  2: Kosdaq

	public StockInfo(String isuCd, String isuSrtCd, String isuKorNm, String isuKorAbbrv, int market){
		this.isuCd = isuCd;
		this.isuSrtCd = isuSrtCd;
		this.isuKorNm = isuKorNm;
		this.isuKorAbbrv = isuKorAbbrv;
		this.market = market;
	}
}
