package com.api.LibraryAPI.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Value("${mail.from}")
	private String remetent;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Override
	public void sendMails(List<String> mailsList, String mailMessage) {
		String[] adresses = mailsList.toArray( new String[mailsList.size()]);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(remetent);
		message.setSubject("Empréstimo com atraso");
		message.setText(mailMessage);
		message.setTo(adresses);
		
		javaMailSender.send(message);
	}

}
