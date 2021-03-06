package com.board.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.board.exception.user.UserExceptionMessage;
import com.board.exception.user.UserExistsException;

public class EmailUtils {

	private static final String USER_NAME = MessageUtils.getMessage("email.setAuthUserName");
	private static final String USER_PWD = MessageUtils.getMessage("email.setAuthUserPwd");
	private static final String EMAIL_FROM = MessageUtils.getMessage("email.setFromEmail");
	
	public static void sendEmail(String userEmail, String certNum) throws EmailException {
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.naver.com");
		email.setSmtpPort(465);
		email.setAuthentication(USER_NAME, USER_PWD);

		email.setSSLOnConnect(true);
		email.setStartTLSEnabled(true);

		try {
			email.setFrom(EMAIL_FROM, "중고거래사이트 관리자", "utf-8");
			email.addTo(userEmail, "회원", "utf-8");
			email.setSubject("비밀번호 재설정을 위한 인증번호 입니다.");
			email.setMsg("[인증번호] " + certNum + " 입니다. \n 인증번호 확인란에 기입해주십시오.");
			email.send();
		} catch (UserExistsException e) {
			throw new UserExistsException(UserExceptionMessage.ALREADY_EXISTS);
		}
	}

	public static String getCertNum() throws NoSuchAlgorithmException {
		Random rand = SecureRandom.getInstanceStrong();
		int rValue = rand.nextInt(888888) + 111111;
		return Integer.toString(rValue);
	}

}
