package sae.elearning.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.elearning.domain.repository.CoursRepository;
import sae.elearning.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CoursRepository coursRepository;

    public record GlobalStatistics(long totalUsers, long activeCourses) {}

    public GlobalStatistics getGlobalStatistics() {
        long totalUsers = userRepository.count();
        long activeCourses = coursRepository.countByStatut("PUBLISHED");
        
        return new GlobalStatistics(totalUsers, activeCourses);
    }
}