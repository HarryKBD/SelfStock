package com.harrykbd.selfstock;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class SelfStockManager {

	static SelfStockDBHandler mDataBase;
	static int mCnt = 0;
	
	private static long mLastReqTime = 0;
	private static final long MIN_REQ_DELTA = 6500; //7sec
	private static void checkInterval(){
        long currTime = Calendar.getInstance().getTimeInMillis();
        long diff = currTime - mLastReqTime;

        while(true){
        	if(diff >= MIN_REQ_DELTA){
        		mLastReqTime = currTime;
        		break;
            }
        	//System.out.println("diff only " + diff + " Sleeping " + (MIN_REQ_DELTA - diff));;
    		try {
    			Thread.sleep(MIN_REQ_DELTA - diff);
    		} catch (Exception e) {
 			    System.out.println(e);
    		}
    		currTime = Calendar.getInstance().getTimeInMillis();
        	diff = currTime - mLastReqTime;
        }
	}
	private static String strGetData(String strUrl){
        BufferedReader    oBufReader = null;
        HttpURLConnection httpConn   = null;
        String strBuffer = "";
        String strRslt   = "";
        
        checkInterval();

        try
        {
            //URL oOpenURL = new URL(strUrl);
            
            URL oOpenURL = new URL("http", "b2b.toeic.co.kr/sec", "index.asp");
            
            httpConn =  (HttpURLConnection) oOpenURL.openConnection();       
            httpConn.setRequestMethod("GET");
            httpConn.connect();          
            oBufReader = new BufferedReader(new InputStreamReader(oOpenURL.openStream()));
  
            //Buffer에 있는 내용을 읽어 차례로 화면에 뿌려준다.
            while((strBuffer = oBufReader.readLine()) != null)
            {
                if(strBuffer.length() > 1)
                {
                	for(int j =0; j < strBuffer.length(); j++){
                		//System.out.println(String.format("%02X", strBuffer.charAt(j)));
                	}
                    strRslt += strBuffer;
                    System.out.println(strBuffer);
                }
            }
            oBufReader.close();
            httpConn.disconnect();
          
        } catch( Exception ee) {
          ee.getMessage();
        }
  
        return strRslt;
		
	}
	
	
	public static void main(String[] args) {
		
		strGetData("http://b2b.toeic.co.kr/sec/index.asp");
		/*
		mDataBase = new SelfStockDBHandler();
		mDataBase.connect();
		
		ArrayList<StockProfit> arr = null;
		 arr = collectStockProfitInfo();
		 System.out.println("Collected profit info size is " + arr.size());
	
		 for(StockProfit s : arr){
			 collectProfitInfo(s);
		 }
		 
		 mDataBase.disconnect();
		 */
	}
	
	public static void collectProfitInfo(StockProfit s){
		Stock tradeInfo;
		String buyDate = s.buyDate;
		mCnt++;
		
		//System.out.println("idx: " + mCnt++ + " Trying to get stock Code: " + s.code + " buyDate: " + s.buyDate);
		//after 1 year
		String targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 1) + buyDate.substring(4));
		tradeInfo = mDataBase.getStockPriceAfter(s.code, targetDate);
		s.setAfterOneYearPrice(tradeInfo);
		
		/*
		if(tradeInfo != null) { 
			System.out.println(tradeInfo.toString());
		}else{
			System.out.println("1 year after not found. Code: " + s.code + "  buy date: " + s.buyDate);
			return;
		}*/

		//after 2 year
		targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 2) + buyDate.substring(4));
		targetDate = (targetDate.substring(0, 4) + (String.format("%04d",  Integer.parseInt(buyDate.substring(4)) + 1))) ;
		tradeInfo = mDataBase.getStockPriceAfter(s.code, targetDate);
		s.setAfterTwoYearPrice(tradeInfo);
		
		/*
		if(tradeInfo != null) { 
			System.out.println(tradeInfo.toString());
		}else{
			System.out.println("2 year after not found. Code: " + s.code + "  buy date: " + s.buyDate);
			return;
		}*/
		
		//1year 100% stop
		long expectedPrice;
		
		targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 1) + buyDate.substring(4));
		expectedPrice = s.buyPrice * 2;
		tradeInfo = mDataBase.getStockExpectedPriceBeforeIf(s.code, buyDate, targetDate, expectedPrice ); //if 100% returns
		s.setPer100Price(tradeInfo);
		
		/*
		if(tradeInfo.clsprc >= expectedPrice){
			System.out.println("2 Expected Price: " + expectedPrice + " ==> Found " + tradeInfo.toString());
		}
		else{
			System.out.println("2 Expected Price: " + expectedPrice + " ==> Not Found " + tradeInfo.toString());
		}*/
		
		
		targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 1) + buyDate.substring(4));
		expectedPrice = s.buyPrice * 2 + s.buyPrice/2;
		tradeInfo = mDataBase.getStockExpectedPriceBeforeIf(s.code, buyDate, targetDate, expectedPrice ); //if 100% returns
		s.setPer150Price(tradeInfo);
		
		//1 year 200% stop
		expectedPrice = s.buyPrice * 3;
		tradeInfo = mDataBase.getStockExpectedPriceBeforeIf(s.code, buyDate, targetDate, expectedPrice); //if 200% returns
		s.setPer200Price(tradeInfo);
		
		/*
		if(tradeInfo.clsprc >= expectedPrice){
			System.out.println("3 Expected Price: " + expectedPrice + " ==> Found " + tradeInfo.toString());
		}
		else{
			System.out.println("3 Expected Price: " + expectedPrice + " ==> Not Found " + tradeInfo.toString());
		}
		*/
		
		System.out.println(mCnt + ": " + s.toString());
		return;
	}
	
	
	
	/*
	public test(){
		//first collect stock base information list.
		SelfStockDBHandler db = new SelfStockDBHandler();
		StockInfo aStockInfo = null;
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		ArrayList<Stock> dialyPriceList = null;
		ArrayList<StockInfo> listStock = null;

		if(db.connect() != true){
			System.out.println("DB connect error");
			return;
		}

		listStock = db.getBaseStockInfo("ALL");
		
		for(int i=0; i < listStock.size(); i++){
			aStockInfo = listStock.get(i);
			dialyPriceList = c.getDailyPrice(aStockInfo.isuSrtCd, 1, 2008, 3, 2016);
			for(int j=0; j < dialyPriceList.size(); j++){
				Stock s2 = dialyPriceList.get(j);
			}
			
		}
		//assertTrue(db.clearAllDailyPriceList());
		
		db.disconnect();
	}
	*/
	public static ArrayList<StockProfit> collectStockProfitInfo(){
		ArrayList<StockProfit> arr = new ArrayList<>();
		
		String strFile = "raw_data.txt";
		BufferedReader br=null;

		try{
		    br = new BufferedReader(new FileReader(strFile));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		    	//System.out.println (strLine);
		    	arr.add(new StockProfit(strLine));
		    }
		    //Close the input stream
	    }catch (Exception e){//Catch exception if any
	    	System.err.println("Error: " + e.getMessage());
	    	e.printStackTrace();
	    }
		finally{
	    	if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
		
		//System.out.println("total count is " + arr.size());
		return arr;
	}
}
