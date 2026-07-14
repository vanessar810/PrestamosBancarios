package com.Sistema.PrestamosBancarios.mapper;

import com.Sistema.PrestamosBancarios.dto.request.LoanRequest;
import com.Sistema.PrestamosBancarios.dto.response.LoanResponse;
import com.Sistema.PrestamosBancarios.model.Loan;
import com.Sistema.PrestamosBancarios.model.Status;
import com.Sistema.PrestamosBancarios.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoanMapper {

    public Loan toEntity(LoanRequest request, User user) {
        return Loan.builder()
                .amount(request.getAmount())
                .termMonths(request.getTermMonths())
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getAmount(),
                loan.getTermMonths(),
                loan.getStatus().name(),
                loan.getCreatedAt(),
                loan.getApprovedAt(),
                loan.getUser().getEmail()
        );
    }
}
