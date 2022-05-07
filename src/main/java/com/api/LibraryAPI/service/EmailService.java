package com.api.LibraryAPI.service;

import java.util.List;

public interface EmailService {

	void sendMails(List<String> mailsList, String mailMessage);

}
