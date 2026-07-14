package com.Sistema.PrestamosBancarios;

import com.Sistema.PrestamosBancarios.dto.request.LoanRequest;
import com.Sistema.PrestamosBancarios.dto.response.LoanResponse;
import com.Sistema.PrestamosBancarios.exception.InvalidLoanStatusException;
import com.Sistema.PrestamosBancarios.exception.ResourceNotFoundException;
import com.Sistema.PrestamosBancarios.mapper.LoanMapper;
import com.Sistema.PrestamosBancarios.model.Loan;
import com.Sistema.PrestamosBancarios.model.Role;
import com.Sistema.PrestamosBancarios.model.Status;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.LoanRepository;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import com.Sistema.PrestamosBancarios.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private User user;
    private Loan loan;
    private LoanRequest loanRequest;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("TestUser")
                .password("encoded")
                .role(Role.USER)
                .build();

        loanRequest = new LoanRequest(new Double("1000.00"), 12);

        loan = Loan.builder()
                .id(1L)
                .amount(new Double("1000.00"))
                .termMonths(12)
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        loanResponse = new LoanResponse(
                1L,
                new Double("1000.00"),
                12,
                "PENDING",
                LocalDateTime.now(),
                null,
                "test@test.com"
        );
    }

    @Test
    void requestLoan_ShouldCreateLoan() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(loanMapper.toEntity(loanRequest, user)).thenReturn(loan);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        LoanResponse response = loanService.requestLoan(loanRequest, "test@test.com");

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void requestLoan_ShouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.requestLoan(loanRequest, "unknown@test.com"));
    }

    @Test
    void getMyLoans_ShouldReturnUserLoans() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(loanRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> loans = loanService.getMyLoans("test@test.com");

        assertEquals(1, loans.size());
        assertEquals("PENDING", loans.get(0).getStatus());
    }

    @Test
    void approveLoan_ShouldApprovePendingLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse response = loanService.approveLoan(1L);

        assertNotNull(response);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void approveLoan_ShouldThrowWhenNotPending() {
        loan.setStatus(Status.APPROVED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(InvalidLoanStatusException.class,
                () -> loanService.approveLoan(1L));
    }

    @Test
    void rejectLoan_ShouldRejectPendingLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse response = loanService.rejectLoan(1L);

        assertNotNull(response);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void rejectLoan_ShouldThrowWhenNotPending() {
        loan.setStatus(Status.REJECTED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(InvalidLoanStatusException.class,
                () -> loanService.rejectLoan(1L));
    }

    @Test
    void getLoanById_ShouldReturnLoanForOwner() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        LoanResponse response = loanService.getLoanById(1L, "test@test.com");

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void getLoanById_ShouldThrowWhenNotOwnerOrAdmin() {
        User otherUser = User.builder()
                .id(2L)
                .email("other@test.com")
                .username("Other")
                .password("encoded")
                .role(Role.USER)
                .build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        assertThrows(com.Sistema.PrestamosBancarios.exception.UnauthorizedOperationException.class,
                () -> loanService.getLoanById(1L, "other@test.com"));
    }
}
