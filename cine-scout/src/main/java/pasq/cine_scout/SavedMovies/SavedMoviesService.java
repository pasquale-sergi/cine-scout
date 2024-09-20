package pasq.cine_scout.SavedMovies;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.ExceptionsHandling.MovieException;
import pasq.cine_scout.Movie.GenresData;
import pasq.cine_scout.Movie.Movie;
import pasq.cine_scout.Movie.MovieRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                .map(existingMovie -> {
                    // If the movie exists, update its details with new values
                    existingMovie.setRating(movie.getRating());

                    return movieRepository.save(existingMovie); // Save the updated movie
                })
                .orElseGet(() -> {
                    // If the movie doesn't exist, save it as new
                    return createAndSaveNewMovie(movie);
                });


        // Check if the user has already saved this movie
        Optional<SavedMovies> existingSavedMovie = savedMovieRepository.findByUserAndMovie(user, savedMovie);
        if (existingSavedMovie.isPresent()) {
            throw MovieException.movieAlreadySaved();
        }

        // Create a new SavedMovie entry
        SavedMovies newSavedMovies = new SavedMovies();
        newSavedMovies.setUser(user);
        newSavedMovies.setMovie(savedMovie);
        newSavedMovies.setSavedDate(new Date());

        // Save the SavedMovie entry
        return savedMovieRepository.save(newSavedMovies);
    }

    private Movie createAndSaveNewMovie(Movie movieData) {
        Movie newMovie = new Movie();
        newMovie.setMovieId(movieData.getMovieId());
        newMovie.setTitle(movieData.getTitle());
        newMovie.setRating(movieData.getRating());
        newMovie.setOverview(movieData.getOverview());
        newMovie.setPoster_path(movieData.getPoster_path());
        newMovie.setVote_average(movieData.getVote_average());
        newMovie.setOriginal_language(movieData.getOriginal_language());
        newMovie.setRuntime(movieData.getRuntime());
        newMovie.setRelease_date(movieData.getRelease_date());

        newMovie = movieRepository.save(newMovie);

        // Handle genres for new movie
        if (movieData.getGenres() != null) {
            List<GenresData> genres = new ArrayList<>();
            for (GenresData genreData : movieData.getGenres()) {
                GenresData newGenre = new GenresData();
                newGenre.setName(genreData.getName());
                newGenre.setGenreId(genreData.getGenreId());
                newGenre.setMovie(newMovie);
                genres.add(newGenre);
            }
            newMovie.setGenres(genres);
        }
        return movieRepository.save(newMovie);

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
