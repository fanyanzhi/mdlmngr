package Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import java.util.Base64;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import BLL.Logger;
public class KCMSKey {
	private static final String KCMSKEY = "752E9A646767DF8C";
	private static final String RC4KEY = "cNkiKCMS1q2w3e4r@www.cnki.com";
	public static String EnCode(String str) throws UnsupportedEncodingException {
		Calendar cl = Calendar.getInstance();
		String strLdt = String.format("%04d%02d%02d%02d%02d%02d",
				cl.get(Calendar.YEAR), cl.get(Calendar.MONTH)+1, cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE), cl.get(Calendar.SECOND));
		
		String str1 = str + "|" + KCMSKEY + "_" + strLdt;
		//Cipher rc4 = Cipher.getInstance("RC4");
		
		//str1 = "kjrb201603220010|752E9A646767DF8C_20160422103422";
		//SecretKeySpec skeySpec = new SecretKeySpec(, "RC4");
		//rc4.init(Cipher.ENCRYPT_MODE, skeySpec);
		rc4_key rkey = new rc4_key();
		prepare_key(RC4KEY.getBytes("utf-8"), rkey);
		//byte[] out = rc4.doFinal(str1.getBytes("utf-8"));
		byte[] out = str1.getBytes("utf-8");
		rc4(out, rkey);
		//String str2 = Base64.getEncoder().encodeToString(out);
		String str2 = new String(Base64.encodeBase64(out));
		
		
		int r = (new Random().nextInt() & 0xFFFF) % 0xFFF;
		//r=16807;
		str2 = reserve_encode(str2, r);
		
		str2 = String.format("%05d%s", r, str2);
		//str2 =  Base64.getEncoder().encodeToString(str2.getBytes("utf-8"));
		str2 = new String(Base64.encodeBase64(str2.getBytes("utf-8")));
		str2 = str2.replace("+", "%2B");
		str2 = str2.replace("&", "%26");
		return str2;
	}
	
	public static String DeCode(String str) throws UnsupportedEncodingException {
		
		String str1 = str.replace("%2B", "+");
		str1 = str1.replace("%26", "&");

		//byte[] out = Base64.getDecoder().decode(str1);
		byte[] out =  Base64.decodeBase64(str1.getBytes());
		
		String s = new String(out, 0, 5);
		String str2 = new String(out, 5, out.length - 5);
		int r = Integer.parseInt(s);
		
		str2 = reserve_decode(str2, r);
		
		//out = Base64.getDecoder().decode(str2);
		
		out = Base64.decodeBase64(str2.getBytes());
		
		rc4_key rkey = new rc4_key();
		prepare_key(RC4KEY.getBytes("utf-8"), rkey);

		rc4(out, rkey);
		
		str2 = new String(out);
		return str2;
	}
	
	static String reserve_encode(String s, int key)
	{
		int i;
	    char [] result = s.toCharArray();
	    
		int pos = key % s.length();

		for(i=0; i< (int)s.length(); i++)
	    {
			int j = (i + pos)%s.length(); 
			result[i] = s.charAt(j);	
	    }
	    return new String(result);
	}
	
	static String reserve_decode(String s, int  key)
	{
		
	    int i;
	    char [] result = s.toCharArray();
		int pos = key % s.length();

	    for(i=0; i< (int)s.length(); i++)
	    {
			int j = i - pos;
			if(j < 0 ){
				j = j + s.length(); 
			}
			result[i] = s.charAt(j);
	    }
	    return new String(result);
	}
	
	static class rc4_key {
		public int x;
		public int y;
		byte [] state = new byte[256];
	};
	
	public static int unsignedToBytes(byte b) {
	    return b & 0xFF;
	  }

	static void prepare_key(byte[] key, rc4_key rkey)
	{
	    int index1;
	    int index2;
	    int counter;     
	    
	    for(counter = 0; counter < 256; counter++)              
			rkey.state[counter] = (byte) counter;  
		
	    rkey.x = 0; 
	    rkey.y = 0;
	    index1 = 0;
	    index2 = 0;
		
	    for(counter = 0; counter < 256; counter++)      
	    {               
	         index2 = ((unsignedToBytes(key[index1]) + unsignedToBytes(rkey.state[counter]) +
	            index2) % 256);
	         swap_byte(rkey.state, counter, index2);

	         index1 = ((index1 + 1) % key.length);  
	    }       
	}

	static void rc4(byte[] buffer, rc4_key rkey)
	{ 
	    int x;
	    int y;
	    int xorIndex;
	    int counter;              
	    
	    x = rkey.x;     
	    y = rkey.y;     
	    
	    for(counter = 0; counter < buffer.length; counter ++)      
	    {               
	         x = ((x + 1) % 256);                      
	         y = ((unsignedToBytes(rkey.state[x]) + y) % 256);               
	         swap_byte(rkey.state, x, y);                        
	              
	         xorIndex = ((unsignedToBytes(rkey.state[x]) + unsignedToBytes(rkey.state[y])) % 256);                 
	              
	         buffer[counter] ^= rkey.state[xorIndex];         
	     }               
	     rkey.x = x;     
	     rkey.y = y;
	}

	static void swap_byte(byte[] data, int idx1, int idx2)
	{
	    byte swapByte;
	    
	    swapByte = data[idx1]; 
	    data[idx1] = data[idx2];
	    data[idx2] = swapByte;
	}
}
