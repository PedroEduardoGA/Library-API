package com.api.LibraryAPI.models;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Loan {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 120)
	private String customer;
	
	@Column(name = "customer_email")
	private String CustomerEmail;
	
	@JoinColumn(name = "id_book")
	@ManyToOne
	private Book book;
	
	@Column
	private LocalDate date;
	
	@Column
	private Boolean returned;
	
}
