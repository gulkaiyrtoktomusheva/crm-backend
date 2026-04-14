package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementRequest;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementResponse;
import kg.ortcrm.service.PaymentAgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-agreements")
@RequiredArgsConstructor
@Tag(name = "Payment Agreements", description = "Payment agreements and generated schedules")
public class PaymentAgreementController {

    private final PaymentAgreementService paymentAgreementService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    @Operation(summary = "Get payment agreement by ID")
    public ResponseEntity<PaymentAgreementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentAgreementService.findById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    @Operation(summary = "Get payment agreements for student")
    public ResponseEntity<List<PaymentAgreementResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentAgreementService.findByStudentId(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Create payment agreement")
    public ResponseEntity<PaymentAgreementResponse> create(@Valid @RequestBody PaymentAgreementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentAgreementService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    @Operation(summary = "Update payment agreement")
    public ResponseEntity<PaymentAgreementResponse> update(@PathVariable Long id, @Valid @RequestBody PaymentAgreementRequest request) {
        return ResponseEntity.ok(paymentAgreementService.update(id, request));
    }

    @PostMapping("/{id}/refresh-status")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    @Operation(summary = "Refresh payment agreement status")
    public ResponseEntity<PaymentAgreementResponse> refreshStatus(@PathVariable Long id) {
        return ResponseEntity.ok(paymentAgreementService.refreshStatus(id));
    }
}
