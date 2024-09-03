package pasq.cine_scout.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasq.cine_scout.SavedMovies.SavedMovies;
import pasq.cine_scout.SavedMovies.SavedMoviesRepository;
import pasq.cine_scout.SavedMovies.SavedMoviesRequest;
import pasq.cine_scout.SavedMovies.SavedMoviesService;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private SavedMoviesService savedMoviesService;

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


}