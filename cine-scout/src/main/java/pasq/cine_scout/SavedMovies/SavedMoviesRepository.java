package pasq.cine_scout.SavedMovies;
import org.springframework.data.jpa.repository.JpaRepository;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;

import java.util.List;
import java.util.Optional;

public interface SavedMoviesRepository extends JpaRepository<SavedMovies, Integer> {
    Optional<SavedMovies> findByUserAndMovie(ApplicationUser user, Movie savedMovie);

    List<SavedMovies> findByUser(Optional<ApplicationUser> user);
}
