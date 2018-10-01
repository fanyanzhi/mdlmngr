package Util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;


public class EmailUtil {

	public static void main(String[] args) {
		sendEmail("1028925519@qq.com","xxxx");
//		sendEmail("wyyiuxiang@163.com","xxxx");
	}
	static Logger logger = Logger.getLogger(EmailUtil.class);
	static String from = "";
	static String passwd = "";
	static String host = "";
	static {
		PropertiesUtils.loadFile("webconfig.properties");
		from = PropertiesUtils.getPropertyValue("EmailName");
		passwd = PropertiesUtils.getPropertyValue("EmailPWD");
		host = PropertiesUtils.getPropertyValue("Host");
	}
	public static void sendEmail(String to,String pwd){   
	      
	      // 1.创建一个程序与邮件服务器会话对象 Session
	      Properties properties = new Properties();
	      properties.setProperty("mail.transport.protocol", "SMTP");
	      properties.setProperty("mail.smtp.host", host);
	      properties.setProperty("mail.smtp.port", "25");	     
	      properties.setProperty("mail.smtp.timeout","1000");
	      
	      properties.setProperty("mail.smtp.auth", "true"); // 指定验证
	      // Session session = Session.getDefaultInstance(properties);
	      
	      // 获取默认session对象
	      Authenticator auth = new Authenticator() {
	    	  public PasswordAuthentication getPasswordAuthentication(){
	    		  return new PasswordAuthentication(from, passwd); //发件人邮件用户名、密码
	    	             }
	      };
	      Session session = Session.getInstance(properties, auth);
	      try{
	         // 创建默认的 MimeMessage 对象
	         MimeMessage message = new MimeMessage(session);	 
	         // Set From: 头部头字段
	         //设置自定义发件人昵称    
	         String nick="";    
	         try {    
	             nick=javax.mail.internet.MimeUtility.encodeText("中国知网");    
	         } catch (UnsupportedEncodingException e) {    
	             e.printStackTrace();    
	         }
	         message.setFrom(new InternetAddress(nick+"<"+from+">"));
	 
	         // Set To: 头部头字段
	         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	 
	         // Set Subject: 头部头字段
	         message.setSubject("重置密码");
	 
	         // 设置消息体
	         message.setText("您的密码已重置为："+pwd+",请登录后自行修改");
	 
	         // 发送消息
	         Transport.send(message);
	         logger.info("Sent mail successfully...("+pwd+")...to "+to);
	         //System.out.println("Sent mail successfully...("+pwd+")...to "+to);
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	   }
}
