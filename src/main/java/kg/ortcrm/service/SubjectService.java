package kg.ortcrm.service;

import kg.ortcrm.dto.subject.SubjectRequest;
import kg.ortcrm.dto.subject.SubjectResponse;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.SubjectMapper;
import kg.ortcrm.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public List<SubjectResponse> findAll() {
        return subjectMapper.toResponseList(subjectRepository.findAll());
    }

    public SubjectResponse findById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return subjectMapper.toResponse(subject);
    }

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Subject with name '" + request.getName() + "' already exists");
        }

        Subject subject = subjectMapper.toEntity(request);
        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toResponse(savedSubject);
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
