package com.harrykbd.selfstock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SelfStockDBHandler {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/stock_test";
	static final String USERNAME = "root";
	static final String PASSWORD = "root";

	Connection mConn = null;
	
	public boolean connect(){
		try{
			Class.forName(JDBC_DRIVER);
			mConn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
			System.out.println("\n- MySQL Connection");
		}catch(SQLException se1){
			se1.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(mConn != null){
			return true;
		}else{
			return false;
		}
	}
	private String createInsertString(Stock s){
		return String.format("insert into stock_history_daily values ('%s', '%s', '%s', "
				+ "%d, "
				+ "'%s', "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%d, "
				+ "%2.2f);",
				s.Code, s.Name, s.trdDd, s.clsprc, s.cmpprevddSgn, s.cmpprevddAmt, s.accTrdvol, s.accTrdval, s.opnprc, s.hgprc,
				s.lwprc, s.prevddClsprc, s.isuTrdvol, s.cmpprevddStkprcFluRt);
	}
	
	private boolean executeUpdate(String sql){
		Statement stmt = null;
		boolean result = true;
		//System.out.println("Execute sql: " + sql);
		try{
			stmt = mConn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch(SQLException se){
			se.printStackTrace();
			result = false;
		}catch(Exception e){
			e.printStackTrace();
			result = false;
		}finally{

		}
		//System.out.println("execute Update done");
		return result;
	}
	
	
	public ArrayList<StockInfo> getBaseStockInfo(String code){
		String sql = "";
		Statement stmt;
		ArrayList<StockInfo> arr = null;
		ResultSet rs = null;

		System.out.println("getBaseStockInfo for " + code);
		if(code == "ALL"){
			sql = "select * from stock_base_information;";
		}
		else{
			sql = "select * from stock_base_information where isuSrtCd='" + code +"';";
		}
		
		try{
			stmt = mConn.createStatement();
			rs = stmt.executeQuery(sql);
			arr = new ArrayList<>();
			while(rs.next()){
				String isuCd = rs.getString("isuCd");
				String isuSrtCd = rs.getString("isuSrtCd");
				String isuKorNm = rs.getString("isuKorNm");
				String isuKorAbbrv = rs.getString("isuKorAbbrv");
				long market = rs.getLong("market");
	
    	        StockInfo aInfo = new StockInfo(isuCd, isuSrtCd, isuKorNm, isuKorAbbrv, (int)market);
				arr.add(aInfo);
			}
			stmt.close();
			rs.close();
		}
		catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
		   e.printStackTrace();
		}finally{
		}
		
		System.out.println("execute collectBaseStockInfo done count: " + arr.size());
		return arr;
	}
	
	public Stock getStockExpectedPriceBeforeIf(String code, String startDate, String targetDate, long expectedPrice){
		
		//String sql = String.format("Select * from stock_history_daily where code='%s' and trdDd between '%s' and '%s'", code, startDate, targetDate);
		
		String sql = String.format("Select * from stock_history_daily where code='%s' and clsprc >= %d and trdDd between '%s' and '%s'", code, expectedPrice, startDate, targetDate);
		//select * from stock_history_daily WHERE Code='003650' and trdDd between 20100221 and 20110221;
		
		if(sql == null) return null;
		
		ArrayList <Stock> arr = executeSelect(sql);
		if(arr == null || arr.size() < 1 ){
			//not found
			return null;//getStockPriceAfter(code, targetDate);
		}
		else{
			return arr.get(0);		
		}
	}
	
	public Stock getStockPriceAfter(String code, String targetDate){
		Stock s = null;
		int retry = 0;
		
		s = getStock(code, targetDate);
		//System.out.println("trying to get stock date " + targetDate);

		while(s == null){
			//System.out.println("trying to get stock code " + code + "  date " + targetDate);
			targetDate = (targetDate.substring(0, 4) + (String.format("%04d",  Integer.parseInt(targetDate.substring(4)) + 1))) ;
			s = getStock(code, targetDate);
			if(retry++ > 10) break;  //if we can't find stock info after 20 tries, just return null
		}
		
		return s;
	}
	

	private ArrayList<Stock> executeSelect(String sql){
		Statement stmt;
		ArrayList<Stock> arr = null;
		ResultSet rs = null;
		try{
			stmt = mConn.createStatement();
			rs = stmt.executeQuery(sql);
			arr = new ArrayList<>();
			while(rs.next()){
				String Code = rs.getString("Code");
				String Name = rs.getString("Name");
				String trdDd = rs.getString("trdDd");
				long clsprc = rs.getLong("clsprc");
				String cmpprevddSgn = rs.getString("cmpprevddSgn");
				long cmpprevddAmt = rs.getLong("cmpprevddAmt");
				long accTrdvol = rs.getLong("accTrdvol");
				long accTrdval = rs.getLong("accTrdval");
				long opnprc = rs.getLong("opnprc");
				long hgprc = rs.getLong("hgprc");
				long lwprc= rs.getLong("lwprc");
				long prevddClsprc = rs.getLong("prevddClsprc");
				long isuTrdvol = rs.getLong("isuTrdvol");
				double cmpprevddStkprcFluRt = (double)rs.getFloat("cmpprevddStkprcFluRt");
				Stock aStock = new Stock(Code, Name, trdDd, clsprc, cmpprevddSgn, cmpprevddAmt, accTrdvol, accTrdval, 
						opnprc, hgprc, lwprc, prevddClsprc, isuTrdvol, cmpprevddStkprcFluRt);				
				arr.add(aStock);
			}
			stmt.close();
			rs.close();
		}
		catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
		   e.printStackTrace();
		}finally{
		}
		//	System.out.println("execute Select done");
		return arr;
	}
	
	public boolean insertStock(Stock s){
		String sql = createInsertString(s);
		if(sql == null){
			return false;
		}
		
		return executeUpdate(sql);
	}
	
	public boolean insertStockInfo(StockInfo s){
		
		String sql = String.format("insert into stock_base_information values ('%s', '%s', '%s', '%s', %d)", s.isuCd, s.isuSrtCd, s.isuKorNm, s.isuKorAbbrv, s.market);
		if(sql == null){
			return false;
		}
		
		return executeUpdate(sql);
	}

	public Stock getStock(String code, String date){
		String sql = String.format("Select * from stock_history_daily where code='%s' and trdDd='%s'", code, date);
		if(sql == null) return null;
		
		ArrayList <Stock> arr = executeSelect(sql);
		if(arr == null || arr.size() < 1 ){
			//System.out.println("error");
			return null;
		}
		
		return arr.get(0);
	}
	
	public boolean deleteStock(Stock s){
		return deleteStock(s.Code, s.trdDd);
	}
	
	public boolean deleteStock(String code, String date){
		String sql = String.format("delete from stock_history_daily where Code='%s' and trdDd='%s'", code, date);
		return executeUpdate(sql);		
	}
	
	public boolean clearAllDailyPriceList(){
		String sql = "delete from stock_history_daily;";
		return executeUpdate(sql);	
	}
	
	public boolean disconnect(){
		try{
			mConn.close();
		}
		catch(Exception e){
		   e.printStackTrace();
		}finally{
			mConn = null;
		}
		return true;
	}
	
	public boolean isDisconnected(){
		if(mConn == null){
			return true;
		}
		else{
			return false;
		}
	}
	
}