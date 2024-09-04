package pasq.cine_scout.SavedMovies;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.Movie.Movie;
import pasq.cine_scout.Movie.MovieRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class SavedMoviesService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SavedMoviesRepository savedMovieRepository;

    @Transactional
    public SavedMovies saveMovie(String username, Movie movie) {
        // Find the user
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Check if the movie already exists in the database
        Movie savedMovie = movieRepository.findByTitle(movie.getTitle())
                .orElseGet(() -> {
                    // If the movie doesn't exist, save it
                    Movie newMovie = Movie.builder()
                            .movieId(movie.getMovieId())
                            .title(movie.getTitle())
                            .genres(movie.getGenres())
                            .rating(movie.getRating())
                            .overview(movie.getOverview())
                            .poster_path(movie.getPoster_path())
                            .vote_average(movie.getVote_average())
                            .original_language(movie.getOriginal_language())
                            .runtime(movie.getRuntime())
                            .release_date(movie.getRelease_date())
                            .build();
                    return movieRepository.save(newMovie);
                });

        // Check if the user has already saved this movie
        Optional<SavedMovies> existingSavedMovie = savedMovieRepository.findByUserAndMovie(user, savedMovie);
        if (existingSavedMovie.isPresent()) {
            throw new RuntimeException("Movie already saved by this user");
        }

        // Create a new SavedMovie entry
        SavedMovies newSavedMovies = new SavedMovies();
        newSavedMovies.setUser(user);
        newSavedMovies.setMovie(savedMovie);
        newSavedMovies.setSavedDate(new Date());

        // Save the SavedMovie entry
        return savedMovieRepository.save(newSavedMovies);
    }

    @Transactional
    public void deleteMovie(Movie movie, String username) {
        Optional<ApplicationUser> user = userRepository.findByUsername(username);
        Optional<SavedMovies> movieToDelete = savedMovieRepository.findByUserAndMovie(user.get(), movie);

        if (movieToDelete.isEmpty()) {
            throw new RuntimeException("Movie or User not found with given ids");
        }

        savedMovieRepository.delete(movieToDelete.get());
    }

}
