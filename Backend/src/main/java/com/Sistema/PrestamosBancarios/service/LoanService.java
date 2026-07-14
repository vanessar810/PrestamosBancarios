package com.Sistema.PrestamosBancarios.service;

import com.Sistema.PrestamosBancarios.dto.request.LoanRequest;
import com.Sistema.PrestamosBancarios.dto.response.LoanResponse;
import com.Sistema.PrestamosBancarios.exception.InvalidLoanStatusException;
import com.Sistema.PrestamosBancarios.exception.ResourceNotFoundException;
import com.Sistema.PrestamosBancarios.exception.UnauthorizedOperationException;
import com.Sistema.PrestamosBancarios.mapper.LoanMapper;
import com.Sistema.PrestamosBancarios.model.Loan;
import com.Sistema.PrestamosBancarios.model.Status;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.LoanRepository;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    public LoanResponse requestLoan(LoanRequest request, String userEmail) {
        log.info("Loan request from user: {} | Amount: {} | Term: {} months",
                userEmail, request.getAmount(), request.getTermMonths());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Loan loan = loanMapper.toEntity(request, user);
        Loan saved = loanRepository.save(loan);
        log.info("Loan created successfully | ID: {} | Status: PENDING", saved.getId());
        return loanMapper.toResponse(saved);
    }

    public List<LoanResponse> getMyLoans(String userEmail) {
        log.debug("Fetching loans for user: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<LoanResponse> loans = loanRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(loanMapper::toResponse)
                .toList();
        log.debug("Found {} loans for user: {}", loans.size(), userEmail);
        return loans;
    }

    @Cacheable(value = "loanCache", key = "#id")
    public LoanResponse getLoanById(Long id, String userEmail) {
        log.debug("Fetching loan by ID: {} | Requested by: {}", id, userEmail);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(loan.getUser().getEmail()) &&
                user.getRole() != com.Sistema.PrestamosBancarios.model.Role.ADMIN) {
            log.warn("Unauthorized access attempt to loan {} by user {}", id, userEmail);
            throw new UnauthorizedOperationException("You do not have access to this loan");
        }

        return loanMapper.toResponse(loan);
    }

    public List<LoanResponse> getAllLoans() {
        log.debug("Fetching all loans");
        List<LoanResponse> loans = loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
        log.debug("Found {} total loans", loans.size());
        return loans;
    }

    public List<LoanResponse> getLoansByStatus(Status status) {
        log.debug("Fetching loans with status: {}", status);
        List<LoanResponse> loans = loanRepository.findByStatus(status)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
        log.debug("Found {} loans with status: {}", loans.size(), status);
        return loans;
    }

    @Transactional
    @CacheEvict(value = "loanCache", key = "#id")
    public LoanResponse approveLoan(Long id) {
        log.info("Approving loan ID: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        if (loan.getStatus() != Status.PENDING) {
            log.warn("Cannot approve loan {} - current status: {}", id, loan.getStatus());
            throw new InvalidLoanStatusException("Cannot approve loan with status: " + loan.getStatus());
        }

        loan.setStatus(Status.APPROVED);
        loan.setApprovedAt(LocalDateTime.now());
        Loan updated = loanRepository.save(loan);
        log.info("Loan {} approved successfully", id);
        return loanMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "loanCache", key = "#id")
    public LoanResponse rejectLoan(Long id) {
        log.info("Rejecting loan ID: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        if (loan.getStatus() != Status.PENDING) {
            log.warn("Cannot reject loan {} - current status: {}", id, loan.getStatus());
            throw new InvalidLoanStatusException("Cannot reject loan with status: " + loan.getStatus());
        }

        loan.setStatus(Status.REJECTED);
        loan.setApprovedAt(LocalDateTime.now());
        Loan updated = loanRepository.save(loan);
        log.info("Loan {} rejected successfully", id);
        return loanMapper.toResponse(updated);
    }
}
