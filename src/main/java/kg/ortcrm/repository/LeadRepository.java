package kg.ortcrm.repository;

import kg.ortcrm.entity.Lead;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

    Page<Lead> findBySource(LeadSource source, Pageable pageable);

    Page<Lead> findByStatusAndSource(LeadStatus status, LeadSource source, Pageable pageable);

    @Query("SELECT l FROM Lead l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:source IS NULL OR l.source = :source)")
    Page<Lead> findByFilters(@Param("status") LeadStatus status,
                             @Param("source") LeadSource source,
                             Pageable pageable);

    long countByStatus(LeadStatus status);
}
