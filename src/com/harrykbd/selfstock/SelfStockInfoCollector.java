package com.harrykbd.selfstock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SelfStockInfoCollector {

	private static final String strCurrentPriceURL = "https://testbed.koscom.co.kr:443/gateway/v1/market/stocks/price?isuSrtCd=";
	private static final String strDailyHistoryURL = "https://testbed.koscom.co.kr:443/gateway/v1/market/stocks/history?isuSrtCd=";
	private long mLastReqTime = 0;
	private static final long MIN_REQ_DELTA = 6500; //7sec
	
	private void checkInterval(){
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
	
	
	private String strGetData(String strUrl){
        BufferedReader    oBufReader = null;
        HttpURLConnection httpConn   = null;
        String strBuffer = "";
        String strRslt   = "";
        
        checkInterval();

        try
        {
            URL oOpenURL = new URL(strUrl);
          
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
                    //System.out.println(strBuffer);
                }
            }
            oBufReader.close();
            httpConn.disconnect();
          
        } catch( Exception ee) {
          ee.getMessage();
        }
  
        return strRslt;
		
	}
	
	public Stock getCurrentPrice(String code){
		Stock stock = null;
		String req = strCurrentPriceURL + code;
		String strResult = strGetData(req);
		//System.out.println(strResult);
		
		try {
			JSONParser jsonParser = new JSONParser();
	        //JSON데이터를 넣어 JSON Object 로 만들어 준다.
			//System.out.println("Parsing the data");
	        JSONObject jsonObject = (JSONObject) jsonParser.parse("{" + strResult + "}");
	        //books의 배열을 추출
	        long price = (long) jsonObject.get("trdPrc");
	/*        
	        String Code = code;
	        String Name = "TBD";
	        String trdDd = "TBD";
	        long clsprc = (long) jsonObject.get("trdPrc");
	        String time = (String)jsonObject.get("trdTm");
	        String cmpprevddSgn = "+";
	        long cmpprevddAmt = 0;
	        long accTrdvol = (long) jsonObject.get("accTrdvol");
	        long accTrdval = (long) jsonObject.get("accTrdval");
	        long opnprc = (long) jsonObject.get("opnprc");
	        long hgprc = (long) jsonObject.get("hgprc");
	        long lwprc = (long) jsonObject.get("lwprc");
	        long prevddClsprc = 0;
	        long isuTrdvol = 0;
	        double cmpprevddStkprcFluRt = 0.0f;
	        	        	
	        stock = new Stock(code, Name, trdDd, clsprc, cmpprevddSgn, cmpprevddAmt, accTrdvol, accTrdval, opnprc, hgprc, lwprc, prevddClsprc, isuTrdvol, cmpprevddStkprcFluRt);
	       */
	        
	        Calendar c = Calendar.getInstance();
	        String Code = code;
	        String Name = "TBD";
	        String trdDd = String.format("%d%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
	        String time = (String)jsonObject.get("trdTm");
	        stock = new Stock(Code, Name, trdDd, time);
	        System.out.println("Code: " + code + " => Price: " + price + ", Date: " + trdDd + " Time: " + time);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stock;
	}
	
	private boolean isLeapYear(int year){
		if(year%4 != 0){
			return false;
		}else if(year%100 != 0){
			return true;
		}else if(year%400 != 0){
			return false;
		}else{
			return true;
		}
		/*
		if (year is not divisible by 4) then (it is a common year)
		else if (year is not divisible by 100) then (it is a leap year)
		else if (year is not divisible by 400) then (it is a common year)
		else (it is a leap year)
		*/
	}
	
	public ArrayList<Stock> getDailyPrice(String code, int month, int year){
		int lastDayOfMonth = 0;
		
		if(month == 2){
			if(isLeapYear(year)){
				lastDayOfMonth = 29;
			}else{
				lastDayOfMonth = 28;
			}
		}
		else{
			if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
				lastDayOfMonth = 31;
			}
			else{
				lastDayOfMonth = 30;
			}
		}
		
		String from = String.format("%d%02d01", year, month);
		String to = String.format("%d%02d%02d", year, month, lastDayOfMonth);
		return getDailyPrice(code, from, to);
	}
	

	private int getLastDayOfMonth(int year, int month){
		int lastDayOfMonth = 0;
		if(month == 2){
			if(isLeapYear(year)){
				lastDayOfMonth = 29;
			}else{
				lastDayOfMonth = 28;
			}
		}
		else{
			if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
				lastDayOfMonth = 31;
			}
			else{
				lastDayOfMonth = 30;
			}
		}	
		return lastDayOfMonth;
	}
	public ArrayList<Stock> getDailyPrice(String code, int fromMonth, int fromYear, int toMonth, int toYear){
		
		ArrayList<Stock> result = new ArrayList<>();
		ArrayList<Stock> partialResult = null;
		int year;
		int startMonth = fromMonth;
		int month, sMonth, sYear;
		int gap = 0;
		sYear = fromYear;
		month = sMonth = fromMonth;
		
		for(year=fromYear; year<=toYear; year++){
			for(month=startMonth; month <= 12; month++){
				gap++;	
				if(year == toYear && month == toMonth) break;
				if(gap == 4){ //every 4 month query
					String from = String.format("%d%02d01", sYear, sMonth);
					String to = String.format("%d%02d%02d", year, month, getLastDayOfMonth(year, month));
					//System.out.println("4 gap Getting from " + from + " to " + to);
					partialResult = getDailyPrice(code, from, to);
					result.addAll(partialResult);
					gap = 0;
					sMonth = month+1;
					sYear = year;
					if(sMonth == 13){
						sMonth = 1;
						sYear++;
					}
				}

			}
			startMonth = 1;
		}
		
		if(gap > 0){
			String from = String.format("%d%02d01", sYear, sMonth);
			String to = String.format("%d%02d%02d", year-1, month, getLastDayOfMonth(year, month));
			//System.out.println("Getting from " + from + " to " + to);
			partialResult = getDailyPrice(code, from, to);
			result.addAll(partialResult);
		}
		System.out.println("All Total Count was " + result.size());
		return result;
	}
	
	
	public ArrayList<Stock> getDailyPrice(String code, String from, String to){ //the count of days should be less than 100 (4 month)	

		ArrayList<Stock> arr = new ArrayList<Stock>();
		Stock aStock = null;
		String req = strDailyHistoryURL + code + "&trnsmCycleTpCd=D&inqStrtDd=" + from + "&inqEndDd=" + to + "&reqCnt=100";
		System.out.println("URL=> " + req);
		String strResult = strGetData(req);
		//System.out.println(strResult);
		System.out.println("Getting dialy price for " + code + " from: " + from + "  to: " + to);
		
		try {
			JSONParser jsonParser = new JSONParser();
	        //JSON데이터를 넣어 JSON Object 로 만들어 준다.
			//System.out.println("Parsing the data");
	        JSONObject jsonObject = (JSONObject) jsonParser.parse("{" + strResult + "}");
        
	        JSONArray lang= (JSONArray) jsonObject.get("inqLists");
            Iterator i = lang.iterator();

            while (i.hasNext()) {
                JSONObject innerObj = (JSONObject) i.next();
    	        String Code = code;
    	        String Name = "TBD";
    	        String trdDd = (String)innerObj.get("trdDd");
    	        long clsprc = (long) innerObj.get("clsprc");
    	        String cmpprevddSgn = (String)innerObj.get("cmpprevddSgn");;
    	        long cmpprevddAmt = (long) innerObj.get("cmpprevddAmt");
    	        long accTrdvol = (long) innerObj.get("accTrdvol");
    	        long accTrdval = (long) innerObj.get("accTrdval");
    	        long opnprc = (long) innerObj.get("opnprc");
    	        long hgprc = (long) innerObj.get("hgprc");
    	        long lwprc = (long) innerObj.get("lwprc");
    	        long prevddClsprc = (long) innerObj.get("prevddClsprc");
    	        long isuTrdvol = (long) innerObj.get("isuTrdvol");
    	        double cmpprevddStkprcFluRt =(double) innerObj.get("cmpprevddStkprcFlucRt");
    	        //System.out.println("Code: " + code + " => Price: " + clsprc + ", Date: " + trdDd);
    	        aStock = new Stock(Code, Name, trdDd, clsprc, cmpprevddSgn, cmpprevddAmt, accTrdvol, accTrdval, opnprc, hgprc, lwprc, prevddClsprc, isuTrdvol, cmpprevddStkprcFluRt);
    	        arr.add(aStock);
            }
            System.out.println("Getting dialy price Total count was " + arr.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public ArrayList<StockInfo> collectListOfStockInfo(boolean isKospi){
		ArrayList<StockInfo> arr = new ArrayList<StockInfo>();
		String req = "";
		StockInfo aInfo = null;
		int market = 0;
		if(isKospi){
			req = "https://testbed.koscom.co.kr/gateway/v1/market/stocks/lists?infoTpCd=01&mktTpCd=1";
			market = 1;
		}
		else{
			req = "https://testbed.koscom.co.kr/gateway/v1/market/stocks/lists?infoTpCd=01&mktTpCd=2";
			market = 2;
		}
		
		System.out.println("collectListOfStockInfo URL=> " + req);
		String strResult = strGetData(req);
		
		try {
			JSONParser jsonParser = new JSONParser();
	        //JSON데이터를 넣어 JSON Object 로 만들어 준다.
			System.out.println("Parsing the data");
	        JSONObject jsonObject = (JSONObject) jsonParser.parse("{" + strResult + "}");
        
	        JSONArray lang= (JSONArray) jsonObject.get("isuLists");
            Iterator i = lang.iterator();

            while (i.hasNext()) {
                JSONObject innerObj = (JSONObject) i.next();
                
    	        String isuCd = (String)innerObj.get("isuCd");
    	        String isuSrtCd = (String)innerObj.get("isuSrtCd");
    	        String isuKorNm = (String)innerObj.get("isuKorNm");
    	        String isuKorAbbrv = (String)innerObj.get("isuKorAbbrv");
  
    	        aInfo = new StockInfo(isuCd, isuSrtCd, isuKorNm, isuKorAbbrv, market);
    	        arr.add(aInfo);
            }
            System.out.println("Total count was " + arr.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arr;
	}
	
}

/*
package test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
 
public class JsonParserTest {
 
    public static void main(String[] args) {
 
        //JSON 데이터
        String jsonInfo = "{\"books\":[{\"genre\":\"소설\",\"price\":\"100\",\"name\":\"사람은 무엇으로 사는가?\",\"writer\":\"톨스토이\",\"publisher\":\"톨스토이 출판사\"},{\"genre\":\"소설\",\"price\":\"300\",\"name\":\"홍길동전\",\"writer\":\"허균\",\"publisher\":\"허균 출판사\"},{\"genre\":\"소설\",\"price\":\"900\",\"name\":\"레미제라블\",\"writer\":\"빅토르 위고\",\"publisher\":\"빅토르 위고 출판사\"}],\"persons\":[{\"nickname\":\"남궁민수\",\"age\":\"25\",\"name\":\"송강호\",\"gender\":\"남자\"},{\"nickname\":\"예니콜\",\"age\":\"21\",\"name\":\"전지현\",\"gender\":\"여자\"}]}";
 
        
        {
            "books": [
                {
                    "genre": "소설",
                    "price": "100",
                    "name": "사람은 무엇으로 사는가?",
                    "writer": "톨스토이",
                    "publisher": "톨스토이 출판사"
                },
                {
                    "genre": "소설",
                    "price": "300",
                    "name": "홍길동전",
                    "writer": "허균",
                    "publisher": "허균 출판사"
                },
                {
                    "genre": "소설",
                    "price": "900",
                    "name": "레미제라블",
                    "writer": "빅토르 위고",
                    "publisher": "빅토르 위고 출판사"
                }
            ],
            "persons": [
                {
                    "nickname": "남궁민수",
                    "age": "25",
                    "name": "송강호",
                    "gender": "남자"
                },
                {
                    "nickname": "예니콜",
                    "age": "21",
                    "name": "전지현",
                    "gender": "여자"
                }
            ]
        }
         
 
        try {
 
            JSONParser jsonParser = new JSONParser();
            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonInfo);
            //books의 배열을 추출
            JSONArray bookInfoArray = (JSONArray) jsonObject.get("books");
 
            System.out.println("* BOOKS *");
 
            for(int i=0; i<bookInfoArray.size(); i++){
 
                System.out.println("=BOOK_"+i+" ===========================================");
                //배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                JSONObject bookObject = (JSONObject) bookInfoArray.get(i);
                //JSON name으로 추출
                System.out.println("bookInfo: name==>"+bookObject.get("name"));
                System.out.println("bookInfo: writer==>"+bookObject.get("writer"));
                System.out.println("bookInfo: price==>"+bookObject.get("price"));
                System.out.println("bookInfo: genre==>"+bookObject.get("genre"));
                System.out.println("bookInfo: publisher==>"+bookObject.get("publisher"));
 
            }
 
            JSONArray personInfoArray = (JSONArray) jsonObject.get("persons");
 
            System.out.println("\r\n* PERSONS *");
 
            for(int i=0; i<personInfoArray.size(); i++){
 
                System.out.println("=PERSON_"+i+" ===========================================");
                JSONObject personObject = (JSONObject) personInfoArray.get(i);
                System.out.println("personInfo: name==>"+personObject.get("name"));
                System.out.println("personInfo: age==>"+personObject.get("age"));
                System.out.println("personInfo: gender==>"+personObject.get("gender"));
                System.out.println("personInfo: nickname==>"+personObject.get("nickname"));
 
            }
 
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
    }
 
}
*/