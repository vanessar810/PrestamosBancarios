package com.Sistema.PrestamosBancarios.controller;

import com.Sistema.PrestamosBancarios.dto.request.LoanRequest;
import com.Sistema.PrestamosBancarios.dto.response.LoanResponse;
import com.Sistema.PrestamosBancarios.model.Status;
import com.Sistema.PrestamosBancarios.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management endpoints")
@Slf4j
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Request a loan", description = "Submit a new loan request with amount and term in months",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Loan request created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<LoanResponse> requestLoan(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {
        log.info("POST /api/loans - User: {} | Amount: {} | Term: {}",
                authentication.getName(), request.getAmount(), request.getTermMonths());
        LoanResponse response = loanService.requestLoan(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-loans")
    @Operation(summary = "Get my loans", description = "Returns all loans for the authenticated user")
    public ResponseEntity<List<LoanResponse>> getMyLoans(Authentication authentication) {
        log.info("GET /api/loans/my-loans - User: {}", authentication.getName());
        List<LoanResponse> loans = loanService.getMyLoans(authentication.getName());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID", description = "Returns a specific loan. Accessible by the owner or admins.")
    public ResponseEntity<LoanResponse> getLoanById(
            @Parameter(description = "Loan ID") @PathVariable Long id,
            Authentication authentication) {
        log.info("GET /api/loans/{} - User: {}", id, authentication.getName());
        LoanResponse response = loanService.getLoanById(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all loans (Admin)", description = "Returns all loans. Optionally filter by status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of loans"),
                    @ApiResponse(responseCode = "403", description = "Admin role required")
            })
    public ResponseEntity<List<LoanResponse>> getAllLoans(
            @Parameter(description = "Filter by status: PENDING, APPROVED, REJECTED")
            @RequestParam(required = false) Status status) {
        log.info("GET /api/loans - Filter: {}", status);
        if (status != null) {
            return ResponseEntity.ok(loanService.getLoansByStatus(status));
        }
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve loan (Admin)", description = "Approve a pending loan request",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan approved"),
                    @ApiResponse(responseCode = "400", description = "Loan is not in PENDING status"),
                    @ApiResponse(responseCode = "403", description = "Admin role required")
            })
    public ResponseEntity<LoanResponse> approveLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("PUT /api/loans/{}/approve", id);
        LoanResponse response = loanService.approveLoan(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject loan (Admin)", description = "Reject a pending loan request",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan rejected"),
                    @ApiResponse(responseCode = "400", description = "Loan is not in PENDING status"),
                    @ApiResponse(responseCode = "403", description = "Admin role required")
            })
    public ResponseEntity<LoanResponse> rejectLoan(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        log.info("PUT /api/loans/{}/reject", id);
        LoanResponse response = loanService.rejectLoan(id);
        return ResponseEntity.ok(response);
    }
}
