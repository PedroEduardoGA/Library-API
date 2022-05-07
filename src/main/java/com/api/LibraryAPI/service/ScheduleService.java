package com.api.LibraryAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.LibraryAPI.models.Loan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	@Value("${mail.lateloans.message}")
	private String mailMessage;
	
	private final LoanService loanService;
	private final EmailService emailService;
	
	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLateLoans() {
		List<Loan> lateLoans = loanService.getlAllLateLoans();
		List<String> mailsList = lateLoans.stream().map(
				loan -> loan.getCustomerEmail() )
				.collect(Collectors.toList());
		
		emailService.sendMails(mailsList,mailMessage);
	}
}
