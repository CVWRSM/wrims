package wrimsv2_plugin.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import wrimsv2_plugin.debugger.exception.WPPException;

public class Encryption {
	
	public static String procEncryption(String original){
		
		String password=UUID.randomUUID().toString();
		
		String saltStr=UUID.randomUUID().toString().replaceAll("-", "");
		byte[] salt = saltStr.getBytes();

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        try {
			SecretKeySpec key = createSecretKey(password.toCharArray(),
			        salt, iterationCount, keyLength);
	        String encrypted = encrypt(original, key);
	        return saltStr+password+encrypted;
		} catch (NoSuchAlgorithmException e) {
			WPPException.handleException(e);
		} catch (InvalidKeySpecException e) {
			WPPException.handleException(e);
		} catch (UnsupportedEncodingException e) {
			WPPException.handleException(e);
		} catch (GeneralSecurityException e) {
			WPPException.handleException(e);
		}
        return null;
	}
	
    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }
	
    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
