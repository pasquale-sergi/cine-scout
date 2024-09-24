package pasq.cine_scout.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.ApplicationUser.UserService;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatching;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatchingDto;
import pasq.cine_scout.CurrentlyWatching.CurrentlyWatchingRepository;
import pasq.cine_scout.SavedMovies.SavedMovies;
import pasq.cine_scout.SavedMovies.SavedMoviesRepository;

import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CurrentlyWatchingRepository currentlyWatchingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SavedMoviesRepository savedMoviesRepository;

    private final GenresData genresData = new GenresData();

    @Value("${api.themoviedb.key}")
    private String apiKey;

    /**
     * Helper method to handle API requests.
     */
    String makeApiRequest(String url) throws IOException {
        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
            return client.prepareGet(url)
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", apiKey)
                    .execute()
                    .toCompletableFuture()
                    .join()
                    .getResponseBody();
        } catch (Exception e) {
            throw new RuntimeException("API request failed for URL: " + url, e);
        }
    }

    public Integer findGenreId(String genre) {
        return genresData.getGenreId(genre);
    }

    public Movie getRandomMovieWithDetails() throws IOException {
        int randPage = new Random().nextInt(200);

        String movieListResponse = makeApiRequest(
                String.format("https://api.themoviedb.org/3/movie/popular?language=en-US&page=%d", randPage));

        Movie randomMovie = parseMovieResponse(movieListResponse);
        return getMovieDetails(randomMovie.getId());
    }

    private Movie parseMovieResponse(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode == null || !resultsNode.isArray() || resultsNode.size() == 0) {
                throw new RuntimeException("No movie results found");
            }

            int randomIndex = new Random().nextInt(resultsNode.size());
            JsonNode movieNode = resultsNode.get(randomIndex);
            return mapper.treeToValue(movieNode, Movie.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing movie response", e);
        }
    }

    private Movie getMovieDetails(int movieId) throws IOException {
        String movieDetailsResponse = makeApiRequest(
                String.format("https://api.themoviedb.org/3/movie/%d?language=en-US", movieId));

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(movieDetailsResponse, Movie.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing movie details", e);
        }
    }

    public List<StreamingPlatforms> getStreamingPlatforms(Integer movieId) throws IOException {
        String streamingResponse = makeApiRequest(
                String.format("https://api.themoviedb.org/3/movie/%d/watch/providers", movieId));

        return parseStreamingPlatformsResponse(streamingResponse);
    }

    private List<StreamingPlatforms> parseStreamingPlatformsResponse(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode flatrateNode = rootNode.path("results").path("US").path("flatrate");

            if (flatrateNode.isMissingNode()) {
                throw new RuntimeException("Streaming providers not found.");
            }

            return objectMapper.readValue(flatrateNode.toString(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing streaming platforms response", e);
        }
    }

    public List<Movie> getUserMovies(String username) {
        ApplicationUser user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<SavedMovies> savedMovies = savedMoviesRepository.findByUser(Optional.of(user));

        if (savedMovies.isEmpty()) {
            throw new RuntimeException("No movies saved for this user");
        }

        return savedMovies.stream()
                .map(SavedMovies::getMovie)
                .collect(Collectors.toList());
    }

    public Movie getMovieBasedOnGenre(String genre) throws IOException {
        int randPage = new Random().nextInt(200);

        String movieListResponse = makeApiRequest(
                String.format("https://api.themoviedb.org/3/movie/popular?language=en-US&page=%d", randPage));

        Movie randomMovie = searchMovieByGenre(movieListResponse, genre);
        return getMovieDetails(randomMovie.getId());
    }

    private Movie searchMovieByGenre(String responseBody, String genre) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.get("results");
            if (resultsNode == null || !resultsNode.isArray() || resultsNode.size() == 0) {
                throw new RuntimeException("No movie results found");
            }

            int genreId = findGenreId(genre);
            for (int i = 0; i < 10; i++) {
                int randomIndex = new Random().nextInt(resultsNode.size());
                JsonNode movieNode = resultsNode.get(randomIndex);
                if (movieHasGenre(movieNode, genreId)) {
                    return mapper.treeToValue(movieNode, Movie.class);
                }
            }

            throw new RuntimeException("Couldn't find a movie with the specified genre.");
        } catch (IOException e) {
            throw new RuntimeException("Error parsing movie genre response", e);
        }
    }

    private boolean movieHasGenre(JsonNode movieNode, int genreId) {
        for (JsonNode genre : movieNode.get("genre_ids")) {
            if (genre.asInt() == genreId) {
                return true;
            }
        }
        return false;
    }

    public CurrentlyWatching addMovieToWatchingSession(String username, Movie movie) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Movie savedMovie = movieRepository.findMovieByMovieId(movie.getMovieId())
                .orElseGet(() -> movieRepository.save(Movie.builder()
                        .title(movie.getTitle())
                        .movieId(movie.getMovieId())
                        .overview(movie.getOverview())
                        .release_date(movie.getRelease_date())
                        .poster_path(movie.getPoster_path())
                        .runtime(movie.getRuntime())
                        .vote_average(movie.getVote_average())
                        .original_language(movie.getOriginal_language())
                        .build()));

        if (movie.getGenres() != null) {
            List<GenresData> genres = new ArrayList<>();
            for (GenresData genreData : movie.getGenres()) {
                GenresData newGenre = new GenresData();
                newGenre.setName(genreData.getName());
                newGenre.setGenreId(genreData.getGenreId());
                newGenre.setMovie(savedMovie);
                genres.add(newGenre);
            }
            savedMovie.setGenres(genres);
        }

        movieRepository.save(savedMovie);

        CurrentlyWatching currentMovie = new CurrentlyWatching();
        currentMovie.setUser(user);
        currentMovie.setMovie(savedMovie);
        currentMovie.setDate(new Timestamp(System.currentTimeMillis()));

        currentlyWatchingRepository.save(currentMovie);
        return currentMovie;
    }

    public List<CurrentlyWatchingDto> getCurrentlyWatching(String username) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<CurrentlyWatching> currentlyWatching = currentlyWatchingRepository.findByUser(user);
        CurrentlyWatchingDto dto = new CurrentlyWatchingDto();

        return currentlyWatching.stream()
                .map(dto::from)
                .collect(Collectors.toList());
    }

    public void deleteMovieFromCurrentlyWatching(String username, Integer movieId) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        CurrentlyWatching currentMovie = currentlyWatchingRepository.findByUserAndMovie(user, movie);
        currentlyWatchingRepository.deleteById(currentMovie.getId());
    }
}
