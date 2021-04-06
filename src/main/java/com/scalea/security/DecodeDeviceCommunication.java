package com.scalea.security;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DecodeDeviceCommunication {
	public static final String key = "qplkefmkvbyhdhyurpojnxkmazgl8ape"; // 32 chars long
	private static byte[] key_Array = new byte[32];

	public static String encrypt(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

			// Initialization vector.
			// It could be any value or generated using a random number generator.
			byte[] iv = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			Key secretKey = new SecretKeySpec(key_Array, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

			return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
		} catch (Exception e) {
			System.out.println("[Exception]:" + e.getMessage());
		}
		return null;
	}

	public static String decrypt(String encryptedMessage) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

			// Initialization vector.
			// It could be any value or generated using a random number generator.
			byte[] iv = { 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			Key secretKey = new SecretKeySpec(key_Array, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

			byte decodedMessage[] = Base64.getDecoder().decode(encryptedMessage);
			return new String(cipher.doFinal(decodedMessage));

		} catch (Exception e) {
			System.out.println("[Exception]:" + e.getMessage());

		}
		return null;
	}
	
	public static void main(String[] args) {
		String message = "Njehere ne klab me thane 'ca bon'?";
		String encryptedMessage = DecodeDeviceCommunication.encrypt(message);
		String decryptedMessage = DecodeDeviceCommunication.decrypt(encryptedMessage);
		
		System.out.println("Encypted message: " + encryptedMessage);
		System.out.println("Decrypted message: " + decryptedMessage);
	}
}
