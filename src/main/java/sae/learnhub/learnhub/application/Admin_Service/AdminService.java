package sae.learnhub.learnhub.application.Admin_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.StatsResponse;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CoursRepository coursRepository;

    public StatsResponse getGlobalStatistics() {
        long totalUsers = userRepository.count();
        long activeCourses = coursRepository.countByStatut("PUBLISHED");
        return new StatsResponse(totalUsers, activeCourses);
    }
}