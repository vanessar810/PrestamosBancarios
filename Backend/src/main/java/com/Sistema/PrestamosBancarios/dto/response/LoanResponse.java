package com.Sistema.PrestamosBancarios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private Double amount;
    private Integer termMonths;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String userEmail;
}
