package com.harrykbd.selfstock.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import com.harrykbd.selfstock.*;

import org.junit.Test;



public class SelfStockTestCase {
	//@Test
	public void testGetAllData(){
		//first collect stock base information list.
		SelfStockDBHandler db = new SelfStockDBHandler();
		StockInfo aStockInfo = null;
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		ArrayList<Stock> dialyPriceList = null;
		ArrayList<StockInfo> listStock = null;
		int hoursLeft = 0;

		if(db.connect() != true){
			System.out.println("DB connect error");
			return;
		}

		listStock = db.getBaseStockInfo("ALL");
		
		//6 years 4 query + 1 query each 6500 ms
		//((6 * 4 + 1)*6500 * (listStock.size() - (i+1)))/1000/60/60 ==> Hours
		
		for(int i=0; i < listStock.size(); i++){
			hoursLeft = ((6 * 4 + 1)*6500 * (listStock.size() - (i+1)))/1000/60/60;
			aStockInfo = listStock.get(i);
			System.out.println("Getting Stock " + aStockInfo.isuSrtCd + " from 2008 to 2016"+ "  (" + (i+1) + "/" + listStock.size() + ")  " + hoursLeft + " Hours Left");
			dialyPriceList = c.getDailyPrice(aStockInfo.isuSrtCd, 1, 2010, 3, 2016);
			for(int j=0; j < dialyPriceList.size(); j++){
				Stock s2 = dialyPriceList.get(j);
				assertTrue(db.insertStock(s2));
			}
			
		}
		//assertTrue(db.clearAllDailyPriceList());
		
		db.disconnect();
		assertTrue(db.isDisconnected());
		
	}
	
	//@Test
	public void testLongPeriod(){
		SelfStockDBHandler db = new SelfStockDBHandler();
		ArrayList<Stock> arr = null;
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		StockInfo aStockInfo = null;
		arr = c.getDailyPrice("000020", 1, 2010, 3, 2016);
		assertTrue(arr!=null);
		assertTrue(db.connect());
		for(int j=0; j < arr.size(); j++){
			Stock s2 = arr.get(j);
			assertTrue(db.insertStock(s2));
		}
//		assertTrue(db.clearAllDailyPriceList());
		System.out.println("Test done");;
		db.disconnect();
		assertTrue(db.isDisconnected());
		
	}

	//@Test
	public void testMultipleStockDailyHistoryCollection(){
		SelfStockDBHandler db = new SelfStockDBHandler();
		StockInfo aStockInfo = null;
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		
		ArrayList<Stock> dialyPriceList = null;
		
		assertTrue(db.connect());
	
		ArrayList<StockInfo> arr = new ArrayList<>();
		arr = db.getBaseStockInfo("000020");
		
		assertTrue(arr != null);
		assertTrue(arr.size() == 1);
		
		arr = db.getBaseStockInfo("ALL");
		assertTrue(arr != null);
		assertTrue(arr.size() > 1);
		
		for(int i=0; i < arr.size(); i++){
			aStockInfo = arr.get(i);
			dialyPriceList = c.getDailyPrice(aStockInfo.isuSrtCd, 2, 2015);
			assertTrue(dialyPriceList!=null);
			for(int j=0; j < dialyPriceList.size(); j++){
				Stock s2 = dialyPriceList.get(j);
				assertTrue(db.insertStock(s2));
			}
			if(i == 100) break;
			
		}
		assertTrue(db.clearAllDailyPriceList());
		
		db.disconnect();
		assertTrue(db.isDisconnected());
		
	}
	
	
	
	//@Test
	public void collectStockCode(){
		String str = "한글";
		System.out.println(str);
		
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		SelfStockDBHandler db = new SelfStockDBHandler();
		
		ArrayList<StockInfo> sInfo1 = c.collectListOfStockInfo(true); //Kospi
		ArrayList<StockInfo> sInfo2 = c.collectListOfStockInfo(false); //Kosdaq
		assertTrue(sInfo1 != null);
		assertTrue(sInfo2 != null);

		assertTrue(db.connect());
		for(int i=0; i < sInfo1.size(); i++){
			StockInfo s2 = sInfo1.get(i);
			System.out.println("Inserting StockInfo " + s2.isuCd + "  Name: [" + s2.isuKorNm + "]     .......");
			assertTrue(db.insertStockInfo(s2));
		}
		
		for(int i=0; i < sInfo2.size(); i++){
			StockInfo s2 = sInfo2.get(i);
			System.out.println("Inserting StockInfo " + s2.isuCd + "  Name" + s2.isuKorNm + " .......");
			assertTrue(db.insertStockInfo(s2));
		}
		
		db.disconnect();
	}
	@Test
	public void testDBConnection() {
		//assertEqual
		//assertTrue;
		//assertFalse;
		SelfStockDBHandler db = new SelfStockDBHandler();
		Stock s = new Stock("005930", "삼성전자", "20150130", 1365000, "+", 64791, 322200, 440683, 1360000, 1377000, 1360000, 1360000, 0, 0.37);
		
		assertTrue(db.connect());
		assertTrue(db.insertStock(s));
		//assertFalse(db.insertStock(s));
		Stock s2 = db.getStock("005930", "20150130");
		assertTrue(s2.equals(s));
		assertTrue(db.deleteStock(s));
		db.disconnect();
		assertTrue(db.isDisconnected());
	}
	
	//@Test
	public void testCollector(){
		ArrayList<Stock> arr = null;
		ArrayList<Stock> arr2 = null;
		SelfStockInfoCollector c = new SelfStockInfoCollector();
		Stock s = c.getCurrentPrice("005930");
		assertTrue(s != null);
		arr = c.getDailyPrice("005930", "20160201", "20160229"); //the count of days should be less than 60 (2 month)
		for(int i=0; i < arr.size(); i++){
			Stock s2 = arr.get(i);
			assertTrue(s2.Name != null);
			assertTrue(s2.Code != null);
			assertTrue(s2.trdDd != null);
		}
		arr2 = c.getDailyPrice("005930", 2, 2016);
		assertTrue(arr.size() == arr2.size());
		
		arr = c.getDailyPrice("005930", "20150201", "20150229"); //the count of days should be less than 60 (2 month)
		for(int i=0; i < arr.size(); i++){
			Stock s2 = arr.get(i);
			assertTrue(s2.Name != null);
			assertTrue(s2.Code != null);
			assertTrue(s2.trdDd != null);
		}
		arr2 = c.getDailyPrice("005930", 2, 2015);
		assertTrue(arr.size() == arr2.size());
		
		SelfStockDBHandler db = new SelfStockDBHandler();
		assertTrue(db.connect());
		for(int i=0; i < arr.size(); i++){
			Stock s2 = arr.get(i);
			//assertTrue(db.insertStock(s2));
		}
		db.disconnect();
		assertTrue(db.isDisconnected());
		
		
		/*
		ArrayList<StockInfo> sInfo1 = c.collectListOfStockInfo(true); //Kospi
		ArrayList<StockInfo> sInfo2 = c.collectListOfStockInfo(false); //Kosdaq
		assertTrue(sInfo1 != null);
		assertTrue(sInfo2 != null);
	
		assertTrue(db.connect());
		for(int i=0; i < sInfo1.size(); i++){
			StockInfo s2 = sInfo1.get(i);
			System.out.println("Inserting StockInfo " + s2.isuCd + "  Name: [" + s2.isuKorNm + "]     .......");
			assertTrue(db.insertStockInfo(s2));
		}
		
		for(int i=0; i < sInfo2.size(); i++){
			StockInfo s2 = sInfo2.get(i);
			System.out.println("Inserting StockInfo " + s2.isuCd + "  Name" + s2.isuKorNm + " .......");
			assertTrue(db.insertStockInfo(s2));
		}
		
		db.disconnect();
		*/
	}
	
	

}