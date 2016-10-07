package com.harrykbd.selfstock.test;

import com.harrykbd.selfstock.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



public class StockResultTest {
	//@Test
	public void fileReadTest(){
		String strInput = "201002 미창석유 A003650 40438 80 49537 80";
		
		StockProfit sp = new StockProfit(strInput);
		assertTrue(sp.code.equals("A003650"));
		//assertTrue(sp.name, "TBD");
		assertTrue(sp.buyDate.equals("20100220"));
		assertTrue(sp.buyCnt == 80);
		assertTrue(sp.buyPrice == 40438);
		assertTrue(sp.actual.price == 49537);
		//assertEquals(sp.actual.profit, 0);
		//assertEquals(sp.actual.profitRate, 0.0f)
		
	}
	@Test
	public void testOne(){
		
		//oneYear
		String buyDate = "20100520";
		String code = "006650";
		int curPrice = 41950;
		
		SelfStockDBHandler db = new SelfStockDBHandler();
		if(db.connect() != true){
			System.out.println("DB connect error");
			return;
		}
		String targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 1) + buyDate.substring(4));
		//assertEquals("20110420", targetDate);
		
		targetDate = (targetDate.substring(0, 4) + (String.format("%04d",  Integer.parseInt(buyDate.substring(4)) + 1))) ;
		//assertEquals("20110421", targetDate);
		Stock s = null;
		s = db.getStockPriceAfter(code, targetDate);
		System.out.println(s.toString());
		assertTrue(s != null);

		targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 2) + buyDate.substring(4));
		//assertEquals("20120420", targetDate);
		targetDate = (targetDate.substring(0, 4) + (String.format("%04d",  Integer.parseInt(buyDate.substring(4)) + 1))) ;
		//assertEquals("20120421", targetDate);
		s = db.getStockPriceAfter(code, targetDate);
		System.out.println(s.toString());
		assertTrue(s != null);
		
		int expectedPrice;
		
		targetDate = (String.format("%d",  Integer.parseInt(buyDate.substring(0, 4)) + 1) + buyDate.substring(4));
		//assertEquals("20110420", targetDate);
		
		expectedPrice = curPrice * 2;
		s = db.getStockExpectedPriceBeforeIf(code, buyDate, targetDate, expectedPrice ); //if 100% returns
		
		if(s.clsprc >= expectedPrice){
			System.out.println("Expected Price: " + expectedPrice + " ==> Found " + s.toString());
		}
		else{
			System.out.println("Expected Price: " + expectedPrice + " ==> Not Found " + s.toString());
		}
		
		expectedPrice = curPrice * 3;
		s = db.getStockExpectedPriceBeforeIf(code, buyDate, targetDate, expectedPrice); //if 200% returns
		if(s.clsprc >= expectedPrice){
			System.out.println("Expected Price: " + expectedPrice + " ==> Found " + s.toString());
		}
		else{
			System.out.println("Expected Price: " + expectedPrice + " ==> Not Found " + s.toString());
		}
		
		db.disconnect();

	}
	
	//@Test
	public void testReadFile(){
		String strFile = "raw_data.txt";
		BufferedReader br=null;
		ArrayList<StockProfit> arr = new ArrayList<>();
		
		try{
		    br = new BufferedReader(new FileReader(strFile));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		      // Print the content on the console
		    	System.out.println (strLine);
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
	
		assertTrue(arr.size() > 10);
		System.out.println("total count is " + arr.size());
		for(StockProfit p : arr){
			System.out.println(p.toString());
		}
		
		
		//StockResultManager new manager;
		//OpenFile();
		//ReadoneLine();
		//set1tostrResult();
	}
}

/*
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
*/