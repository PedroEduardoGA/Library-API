package com.api.LibraryAPI.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.api.LibraryAPI.models.Book;
import com.api.LibraryAPI.models.BookDto;
import com.api.LibraryAPI.models.Loan;
import com.api.LibraryAPI.models.LoanDto;
import com.api.LibraryAPI.models.LoanFilterDto;
import com.api.LibraryAPI.models.ReturnedLoanDto;
import com.api.LibraryAPI.service.BookService;
import com.api.LibraryAPI.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api("Loan API")
public class LoanController {

	private final BookService bookService;
	private final LoanService loanService;
	private final ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Create a loan")
	public Long create(@RequestBody LoanDto dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow( 
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Livro não encontrado para o isbn informado!"));
		Loan entity = Loan.builder()
					.customer(dto.getCustomer())
					.book(book)
					.date(LocalDate.now()).build();
		
		entity = loanService.save(entity);
		return entity.getId();
	}
	
	@PatchMapping("{id}")
	@ApiOperation("Update a loan returning the book loaned")
	public void returnBook(@PathVariable Long id,@RequestBody ReturnedLoanDto dto) {
		Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		loan.setReturned(dto.isReturned());
		loanService.update(loan);
	}
	
	@GetMapping
	@ApiOperation("Obtains all loans")
	public Page<LoanDto> find(LoanFilterDto dto, Pageable pageRequest){
		Page<Loan> result = loanService.find(dto, pageRequest);
		List<LoanDto> loans = result
				.getContent()
				.stream()
				.map( entity -> {
					Book book = entity.getBook();
					BookDto bookDto = modelMapper.map(book, BookDto.class);
					LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
					loanDto.setBook(bookDto);
					return loanDto;
				}).collect(Collectors.toList());
		
		return new PageImpl<LoanDto>(loans,pageRequest,result.getTotalElements());
	}
}
