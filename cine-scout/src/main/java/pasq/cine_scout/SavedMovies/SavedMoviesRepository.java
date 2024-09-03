package pasq.cine_scout.SavedMovies;
import org.springframework.data.jpa.repository.JpaRepository;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;
import pasq.cine_scout.Movie.MovieRepository;

import java.util.Optional;

public interface SavedMoviesRepository extends JpaRepository<SavedMovies, Integer> {
    Optional<SavedMovies> findByUserAndMovie(ApplicationUser user, Movie savedMovie);
}
