package Util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerFile {
	public static void appendMethod(String fileName, String content) {
		FileWriter writer = null;
        try {
        	writer = new FileWriter(fileName+"_"+getCurDate()+".txt", true);
            writer.write(getCurTime()+content+"\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	if(writer!=null){
        		try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
        	}
        }
    }
	
	private static String getCurDate(){
		 Date d = new Date(); 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		 String dateNowStr = sdf.format(d); 
		 return dateNowStr;
	 }
	 
	 private static String getCurTime(){
		 Date d = new Date(); 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		 String dateNowStr = sdf.format(d); 
		 return dateNowStr;

	 }
}
