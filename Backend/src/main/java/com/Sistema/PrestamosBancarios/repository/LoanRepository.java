package com.Sistema.PrestamosBancarios.repository;

import com.Sistema.PrestamosBancarios.model.Loan;
import com.Sistema.PrestamosBancarios.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatus(Status status);

    List<Loan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
