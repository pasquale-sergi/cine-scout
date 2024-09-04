package pasq.cine_scout.ApplicationUser;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByUsername(String username);

    Optional<ApplicationUser> findUserByUserId(Integer userId);
}
