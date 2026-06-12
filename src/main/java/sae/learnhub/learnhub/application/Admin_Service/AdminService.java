package sae.learnhub.learnhub.application.Admin_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sae.learnhub.learnhub.domain.model.CoursStatut;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;




@Service
@RequiredArgsConstructor
public class AdminService {

    private final IUserRepository userRepository;
    private final ICoursRepository coursRepository;

    public GlobalStatistics getGlobalStatistics() {
        long totalUsers = userRepository.count();
        long activeCourses = coursRepository.countByStatut(CoursStatut.PUBLISHED.name());
        return new GlobalStatistics(totalUsers, activeCourses);
    }

    public record GlobalStatistics(long totalUsers, long activeCourses) {}
}
