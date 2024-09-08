package pasq.cine_scout.CurrentlyWatching;

import org.springframework.data.jpa.repository.JpaRepository;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;

import java.util.List;

public interface CurrentlyWatchingRepository extends JpaRepository<CurrentlyWatching, Integer> {

    List<CurrentlyWatching> findByUser(ApplicationUser user);
    CurrentlyWatching findByUserAndMovie(ApplicationUser user, Movie movie);
}
