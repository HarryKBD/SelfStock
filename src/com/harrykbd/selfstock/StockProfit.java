package com.harrykbd.selfstock;

import java.util.ArrayList;

public class StockProfit {
	public String code;
	public String name;
	public long buyPrice;
	public long buyCnt;
	public String buyDate;
	public SellInfo actual;
	public SellInfo oneYear;
	public SellInfo twoYear;
	public SellInfo per100;
	public SellInfo per150;
	public SellInfo per200;

	
	public StockProfit(String str){
		//String strInput = "201002 미창석유	A003650	40438 	80	49537	80";
		int idx = 0;
		int found = 0;
		ArrayList<String> result = new ArrayList<>();
		String tmp;
		
		StringBuffer buf = new StringBuffer();
		
		char ch, prevCh;
		
		prevCh = str.charAt(0);
		for(idx=0; idx < str.length(); idx++){
			ch = str.charAt(idx);
			if(ch == ' ' && prevCh == ' ') continue;
			buf.append(ch);
			prevCh = ch;
		}
		String newStr = buf.toString();

		for(String aaa : newStr.split("\\s")){
			//System.out.println("==> " + aaa);
			result.add(aaa);
		}
		//System.out.println("Stock " + code + "Size is " + result.size());
		/*
		while(idx < str.length()){
			found = str.indexOf(' ', idx);
			if(found == -1){
				result.add(str.substring(idx, str.length()));
				break;
			}
			tmp = str.substring(idx, found);
			result.add(tmp);
			idx = found+1;
		}*/
		
		buyDate = result.get(0) + "20";
		name = result.get(1);
		code = result.get(2);
		buyPrice = Long.parseLong(result.get(3));
		buyCnt = Long.parseLong(result.get(4));
		actual = new SellInfo();
		actual.price = Long.parseLong(result.get(5));
		actual.profit = actual.price - buyPrice;
		//System.out.println(actual.getProfitRate(buyPrice));
		oneYear = new SellInfo();
		twoYear = new SellInfo();
		per100 = new SellInfo();
		per150 = new SellInfo();
		per200 = new SellInfo();
	}
	
	public String toString(){
		String outPut = String.format("%s %s %d %3.2f ==> ", buyDate, code, actual.price, actual.getProfitRate(buyPrice));
		
		//outPut += " " + oneYear.toString() + " " + twoYear.toString() + " " + per100.toString() + " " + per150.toString() + " " + per200.toString();
		//outPut += " " + oneYear.toString() + " " + twoYear.toString();
		outPut += " " + per100.toString() + " " + per150.toString() + " " + per200.toString();
		//201309 Code  Actual Actual% 100수익율 150%수익율 200%수익율  1년뒤수익율  2년뒤수익율
		return outPut;
	}
	
	private void setPrice(SellInfo info, Stock tradeInfo){
		if(tradeInfo == null){
			info.isEmpty = true;
			return;
		}
		
		info.isEmpty = false;
		info.price = tradeInfo.clsprc;
		info.profit = tradeInfo.clsprc - buyPrice;
		info.cnt = buyCnt; //tmp

	}
	
	public void setAfterOneYearPrice(Stock tradeInfo){
		setPrice(oneYear, tradeInfo);
		return;
	}
	
	public void setAfterTwoYearPrice(Stock tradeInfo){
		setPrice(twoYear, tradeInfo);
		return;
	}
	
	public void setPer100Price(Stock tradeInfo){
		setPrice(per100, tradeInfo);
		return;
	}
	
	public void setPer150Price(Stock tradeInfo){
		setPrice(per150, tradeInfo);
		return;
	}
	public void setPer200Price(Stock tradeInfo){
		setPrice(per200, tradeInfo);
		return;
	}
	
	public class SellInfo{
		public boolean isEmpty;
		public long basePrice;
		public long price;
		public long profit;
		public long cnt;
		public SellInfo(){
			isEmpty = true;
		}
		public double getProfitRate(long p){
			basePrice = p;
			return ((double)profit/(double)basePrice) * 100.0;
		}
		
		public String toString(){
			if(isEmpty){
				return "N";
			}
			else{
				//System.out.println("profit : " + profit + "basePrice " + buyPrice);
				return String.format("%3.2f",((double)profit/(double)buyPrice) * 100.0);
			}
		}
	};
}
