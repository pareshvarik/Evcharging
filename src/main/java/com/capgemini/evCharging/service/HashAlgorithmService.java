package com.capgemini.evCharging.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//


//This service hashes the password using MD5 algorithm
public class HashAlgorithmService {
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static String hashedPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
		String algorithm = "MD5";
		return generateHash(password, algorithm, salt);
	}
	
	private static String generateHash(String password, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.reset();
		digest.update(salt);
		byte[] hash = digest.digest(password.getBytes());
		return bytesToHexString(hash);
	}
	
	public static byte[] createSalt() {
		byte[] bytes = new byte[20];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bytes);
		return bytes;
	}
	
	private static String bytesToHexString(byte[] hash) {
		char[] hexChars = new char[hash.length * 2];
		for (int i = 0; i < hash.length; i++) {
			int k = hash[i] & 0xFF;
			hexChars[i * 2] = hexArray[k >>> 4];
			hexChars[i * 2 + 1] = hexArray[k & 0x0f];
		}
		return new String(hexChars);
	}

	

}
