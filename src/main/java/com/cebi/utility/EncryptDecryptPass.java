package com.cebi.utility;

import org.apache.log4j.Logger;

public class EncryptDecryptPass {
	
	private static final Logger logger = Logger.getLogger(EncryptDecryptPass.class);
	public static void main(String[] args) {
		
		try {
			logger.info("decrypted pass1=" + AES.encrypt("PAYAGGADMINNEW"));
			System.out.println("*****************************");
			System.out.println(AES.encrypt("admin"));
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println(AES.encrypt("root"));
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println(AES.encrypt("101"));
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println(AES.encrypt("poweredge"));
			System.out.println("*****************************");
			
			logger.info("decrypted pass1=" + AES.decrypt("J/2UNSCd/krNc9qnuLdKjw=="));

			logger.info("getMD5EncryptedValue pass2=" + AES.getMD5EncryptedValue("winquery"));

		} catch (Exception e) {
			logger.info("bug" + e.getMessage());
		}
	}

}


