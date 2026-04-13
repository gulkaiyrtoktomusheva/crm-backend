package kg.ortcrm.service;

import kg.ortcrm.dto.lead.LeadRequest;
import kg.ortcrm.dto.lead.LeadResponse;
import kg.ortcrm.dto.lead.LeadStatsResponse;
import kg.ortcrm.entity.Lead;
import kg.ortcrm.entity.User;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.LeadStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.LeadMapper;
import kg.ortcrm.repository.LeadRepository;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    public Page<LeadResponse> findAll(LeadStatus status, LeadSource source, Pageable pageable) {
        Page<Lead> leads = leadRepository.findByFilters(status, source, pageable);
        return leads.map(leadMapper::toResponse);
    }

    public LeadResponse findById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return leadMapper.toResponse(lead);
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        Lead lead = leadMapper.toEntity(request);

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            lead.setAssignedTo(assignedTo);
        }

        if (lead.getStatus() == null) {
            lead.setStatus(LeadStatus.NEW);
        }

        Lead savedLead = leadRepository.save(lead);
        return leadMapper.toResponse(savedLead);
    }

    @Transactional
    public LeadResponse update(Long id, LeadRequest request) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        leadMapper.updateEntity(lead, request);

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            lead.setAssignedTo(assignedTo);
        }

        Lead updatedLead = leadRepository.save(lead);
        return leadMapper.toResponse(updatedLead);
    }

    @Transactional
    public LeadResponse updateStatus(Long id, LeadStatus status) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        lead.setStatus(status);
        Lead updatedLead = leadRepository.save(lead);
        return leadMapper.toResponse(updatedLead);
    }

    @Transactional
    public void delete(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    public LeadStatsResponse getStats() {
        return LeadStatsResponse.builder()
                .newCount(leadRepository.countByStatus(LeadStatus.NEW))
                .contactedCount(leadRepository.countByStatus(LeadStatus.CONTACTED))
                .thinkingCount(leadRepository.countByStatus(LeadStatus.THINKING))
                .paidCount(leadRepository.countByStatus(LeadStatus.PAID))
                .rejectedCount(leadRepository.countByStatus(LeadStatus.REJECTED))
                .totalCount(leadRepository.count())
                .build();
    }
}
