package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.lead.LeadRequest;
import kg.ortcrm.dto.lead.LeadResponse;
import kg.ortcrm.dto.lead.LeadStatsResponse;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.LeadStatus;
import kg.ortcrm.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead management endpoints")
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW')")
    @Operation(summary = "Get all leads", description = "Get paginated list of leads with optional filters")
    public ResponseEntity<Page<LeadResponse>> getAll(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) LeadSource source,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leadService.findAll(status, source, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_VIEW')")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<LeadResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_CREATE')")
    @Operation(summary = "Create new lead")
    public ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_UPDATE')")
    @Operation(summary = "Update lead")
    public ResponseEntity<LeadResponse> update(@PathVariable Long id, @Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(leadService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('LEAD_UPDATE')")
    @Operation(summary = "Update lead status")
    public ResponseEntity<LeadResponse> updateStatus(@PathVariable Long id, @RequestParam LeadStatus status) {
        return ResponseEntity.ok(leadService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE')")
    @Operation(summary = "Delete lead")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('LEAD_VIEW')")
    @Operation(summary = "Get lead statistics", description = "Get count of leads by status for sales funnel")
    public ResponseEntity<LeadStatsResponse> getStats() {
        return ResponseEntity.ok(leadService.getStats());
    }
}
