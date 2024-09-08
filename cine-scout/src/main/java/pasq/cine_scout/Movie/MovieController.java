package pasq.cine_scout.Movie;

import org.hibernate.annotations.OnDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatching;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatchingDto;
import pasq.cine_scout.SavedMovies.SavedMovies;
import pasq.cine_scout.SavedMovies.SavedMoviesRepository;
import pasq.cine_scout.SavedMovies.SavedMoviesRequest;
import pasq.cine_scout.SavedMovies.SavedMoviesService;


import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private SavedMoviesService savedMoviesService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/random")
    public ResponseEntity<Movie> getRandomMovie() throws IOException {
        Movie movie = movieService.getRandomMovieWithDetails();
        return ResponseEntity.ok().body(movie);
    }

    @GetMapping("/{movieId}/streaming-platforms")
    public ResponseEntity<List<StreamingPlatforms>> getStreamingPlatforms(@PathVariable Integer movieId) throws IOException {
        List<StreamingPlatforms> response = movieService.getStreamingPlatforms(movieId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<SavedMovies> saveMovie(@RequestBody SavedMoviesRequest request) {
        SavedMovies savedMovie = savedMoviesService.saveMovie(request.getUsername(), request.getMovie());
        return ResponseEntity.ok().body(savedMovie);
    }

    @GetMapping("/saved-movies")
    public ResponseEntity<List<Movie>> getUserMovies(@RequestParam String username){
        List<Movie> movies = movieService.getUserMovies(username);
        return ResponseEntity.ok().body(movies);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMovie(@RequestParam Integer movieId, @RequestParam String username) {
        Movie movie = movieRepository.findMovieByMovieId(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        savedMoviesService.deleteMovie(movie, username);

        return ResponseEntity.noContent().build(); // Returning 204 No Content
    }

    @PostMapping("/random/genre")
    public ResponseEntity<Movie> getMovieWithGenre(@RequestParam String genre) throws IOException {
        Movie movie = movieService.getMovieBasedOnGenre(genre);
        return ResponseEntity.ok().body(movie);
    }

    @PostMapping("/currently-watching")
    public ResponseEntity<CurrentlyWatchingDto> setMovieToCurrentlyWatching(@RequestBody SavedMoviesRequest request){
        CurrentlyWatching currentMovie = movieService.addMovieToWatchingSession(request.getUsername(), request.getMovie());
        CurrentlyWatchingDto dto = new CurrentlyWatchingDto();
        return ResponseEntity.ok().body(dto.from(currentMovie));
    }
    @GetMapping("/currently-watching")
    public ResponseEntity<List<CurrentlyWatchingDto>> getUserCurrentlyWatching(@RequestParam String username){
        List<CurrentlyWatchingDto> movies = movieService.getCurrentlyWatching(username);
        return ResponseEntity.ok().body(movies);
    }

    @DeleteMapping("/currently-watching/delete")
    public void deleteFromCurrentlyWatching(@RequestParam Integer movieId, @RequestParam String username){
        movieService.deleteMusicFromCurrentlyWatching(username, movieId);
    }
}