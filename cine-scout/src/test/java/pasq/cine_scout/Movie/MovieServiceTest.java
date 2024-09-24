package pasq.cine_scout.Movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatching;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatchingRepository;
import pasq.cine_scout.SavedMovies.SavedMovies;
import pasq.cine_scout.SavedMovies.SavedMoviesRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SavedMoviesRepository savedMoviesRepository;
    @Mock
    private CurrentlyWatchingRepository currentlyWatchingRepository;

    private Movie dummyMovie;
    private ObjectMapper objectMapper;
    private String dummyMovieListResponse;
    private String dummyMovieDetailsResponse;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        dummyMovie = new Movie(); // Initialize with relevant data
        dummyMovie.setId(1);
        dummyMovie.setTitle("Dummy Movie");
        dummyMovie.setOverview("This is a dummy movie overview.");
        dummyMovie.setGenres(List.of(new GenresData(1, "Action"), new GenresData(2, "Comedy")));

        dummyMovieDetailsResponse = "{\"id\":1,\"title\":\"Dummy Movie\",\"overview\":\"This is a dummy movie overview.\"}";

        dummyMovieListResponse = "{ \"results\": [" +
                "{\"id\": 1, \"title\": \"Dummy Movie 1\", \"genre_ids\": [28]}," +
                "{\"id\": 2, \"title\": \"Dummy Movie 2\", \"genre_ids\": [12]}]" +
                "}";
    }

    @Test
    void testFindGenreId(){
        GenresData genresData = new GenresData();
        movieService = new MovieService();
        //Given an integer
        Integer genreId = movieService.findGenreId("Action");
        Integer genreIdWrong = movieService.findGenreId("Elephant");
        //Assert
        assertNotNull(genreId, "Genre ID should not be null for 'Action'");
        assertNull(genreIdWrong);
    }

    @Test
    void testAddMovieToWatchingSession() {
        // Given
        String username = "testUser";
        Movie movie = new Movie();
        movie.setMovieId(123);
        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(movieRepository.findMovieByMovieId(movie.getMovieId())).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // When
        CurrentlyWatching currentMovie = movieService.addMovieToWatchingSession(username, movie);

        // Then
        assertNotNull(currentMovie, "Currently watching movie should not be null");
        assertEquals(movie, currentMovie.getMovie(), "Movie should match the saved movie");
        Mockito.verify(currentlyWatchingRepository, times(1)).save(any(CurrentlyWatching.class));
    }


    @Test
    void testDeleteMovieFromCurrentlyWatching() {
        // Given
        String username = "testUser";
        Integer movieId = 123;
        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);
        Movie movie = new Movie();
        movie.setId(movieId);
        CurrentlyWatching currentlyWatching = new CurrentlyWatching();
        currentlyWatching.setMovie(movie);
        currentlyWatching.setUser(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(currentlyWatchingRepository.findByUserAndMovie(user, movie)).thenReturn(currentlyWatching);

        // When
        movieService.deleteMovieFromCurrentlyWatching(username, movieId);

        // Then
        Mockito.verify(currentlyWatchingRepository, times(1)).deleteById(currentlyWatching.getId());
    }

    @Test
    void testGetUserMovies() {
        // Given
        String username = "testUser";
        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        Movie movie1 = new Movie();
        movie1.setId(1);
        movie1.setTitle("Movie 1");

        Movie movie2 = new Movie();
        movie2.setId(2);
        movie2.setTitle("Movie 2");

        SavedMovies savedMovie1 = new SavedMovies();
        savedMovie1.setMovie(movie1);
        savedMovie1.setUser(user);

        SavedMovies savedMovie2 = new SavedMovies();
        savedMovie2.setMovie(movie2);
        savedMovie2.setUser(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(savedMoviesRepository.findByUser(Optional.of(user))).thenReturn(List.of(savedMovie1, savedMovie2));

        // When
        List<Movie> userMovies = movieService.getUserMovies(username);

        // Then
        assertNotNull(userMovies, "User movies should not be null");
        assertEquals(2, userMovies.size(), "User should have 2 saved movies");
        assertEquals("Movie 1", userMovies.get(0).getTitle(), "First movie title should match");
        assertEquals("Movie 2", userMovies.get(1).getTitle(), "Second movie title should match");
    }
}