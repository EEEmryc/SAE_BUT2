package sae.learnhub.learnhub.application.Admin_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;




@Service
@RequiredArgsConstructor
public class AdminService {

    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;

    public StatsResponse getGlobalStatistics() {
        long totalUsers = userRepository.count();
        long activeCourses = coursRepository.countByStatut("PUBLIE");
        return new StatsResponse(totalUsers, activeCourses);
    }
}
