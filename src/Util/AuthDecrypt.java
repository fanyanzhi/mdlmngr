package Util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AuthDecrypt {
	
	private static Cipher requestDecryptCipher = null;
	//"D:\\证书\\clientcert.jks"
	//keyStorePassword "123456
	//keyAlias "readerex client"
	//keyPassowrd "123456
	public static String DecryptRequest(String request, String keyStoreFile, String keyStorePassword, String keyAlias, String keyPassword) {
		try {
			if (requestDecryptCipher == null) {
				KeyStore ks = KeyStore.getInstance("JKS", "SUN");
				ks.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());
				RSAPrivateCrtKey issuerPrivateKey = (RSAPrivateCrtKey) ks.getKey(keyAlias, keyPassword.toCharArray());
				//System.out.println(issuerPrivateKey.toString());
				RSAPrivateKeySpec spec = new RSAPrivateKeySpec(issuerPrivateKey.getModulus(),
						issuerPrivateKey.getPrivateExponent());
				Key fakePublicKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
				requestDecryptCipher = Cipher.getInstance("RSA");

				requestDecryptCipher.init(Cipher.DECRYPT_MODE, fakePublicKey);
			}
			// String str =
			// "Sqbh4jJjrqNazAWn+/V1DpkjENw0qG1fWjPqy52P1FFMISGgoVvdKFpVkSTfsp8XelBkTQt8nfcWP3+34q+aAZY7fU67ItaazOJtAY9wfJWLzuFK1NQnEyNpq1uFAajSlFnvKnim31vjxIn7Cmz99LrZAbkTyjiMd6husnWwQgU=";
			byte[] encryptedData = Base64.decodeBase64(request.getBytes());
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 128) {
					cache = requestDecryptCipher.doFinal(encryptedData, offSet, 128);
				} else {
					cache = requestDecryptCipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 128;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return new String(decryptedData, 0, decryptedData.length, "UTF8");
			// System.out.println(str1);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static void main(String[] args) {
		String str ="Sqbh4jJjrqNazAWn+/V1DpkjENw0qG1fWjPqy52P1FFMISGgoVvdKFpVkSTfsp8XelBkTQt8nfcWP3+34q+aAZY7fU67ItaazOJtAY9wfJWLzuFK1NQnEyNpq1uFAajSlFnvKnim31vjxIn7Cmz99LrZAbkTyjiMd6husnWwQgU=";
		//String str ="Sqbh4jJjrqNazAWn+/V1DpkjENw0qG1fWjPqy52P1FFMISGgoVvdKFpVkSTfsp8XelBkTQt8nfcWP3+34q+aAZY7fU67ItaazOJtAY9wfJWLzuFK1NQnEyNpq1uFAajSlFnvKnim31vjxIn7Cmz9";
		//String str = "XO7Qq2R48Rcr1GjdRtEK2VNEQK/5OWs7qOWyBICExlwpm4p3iy/iFoJGEq7bNI6WIIED4Luuj60vWtnS1vkw7fx304PBVwesfsuW+svJyCYONYDtNTfBGet5jRLeEWa3dNLnW5Gs1dCgcIOIo3IOHOPcWGJj74fYs8HRGUgUsew=";
		System.out.println(DecryptRequest(str, "/clientcert.jks", "123456", "readerex client", "123456"));
	}
}
