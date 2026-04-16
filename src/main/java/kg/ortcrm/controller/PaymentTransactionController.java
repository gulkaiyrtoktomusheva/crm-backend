package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.paymenttransaction.PaymentTransactionRequest;
import kg.ortcrm.dto.paymenttransaction.PaymentTransactionResponse;
import kg.ortcrm.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payment-transactions")
@RequiredArgsConstructor
@Tag(name = "Payment Transactions", description = "Recorded payments and allocations")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    @Operation(summary = "Get payment transactions by date range")
    public ResponseEntity<List<PaymentTransactionResponse>> getAllByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(paymentTransactionService.findAllByPaidAtBetween(dateFrom, dateTo));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    @Operation(summary = "Get payment transactions for student")
    public ResponseEntity<List<PaymentTransactionResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentTransactionService.findByStudentId(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Create payment transaction")
    public ResponseEntity<PaymentTransactionResponse> create(@Valid @RequestBody PaymentTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentTransactionService.create(request));
    }
}
