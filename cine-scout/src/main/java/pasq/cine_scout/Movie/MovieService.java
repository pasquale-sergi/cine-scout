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

    private GenresData genresData = new GenresData();

    public Integer findGenreId(String genre){
        return genresData.getGenreId(genre);
    }


    @Value("${api.themoviedb.key}")
    private String apiKey;

    public Movie getRandomMovieWithDetails() throws IOException {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Random random = new Random();
        int randNumber = random.nextInt(200);

        try {
            // Step 1: Fetch random movie
            Movie randomMovie = client.prepare("GET", String.format("https://api.themoviedb.org/3/movie/popular?language=en-US&page=%d", randNumber))
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", apiKey)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(resp -> {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = null;
                        try {
                            rootNode = mapper.readTree(resp.getResponseBody());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        JsonNode resultsNode = rootNode.get("results");

                        if (resultsNode == null || !resultsNode.isArray() || resultsNode.size() == 0) {
                            throw new RuntimeException("No movie results found");
                        }

                        int randomIndex = new Random().nextInt(resultsNode.size());
                        JsonNode movieNode = resultsNode.get(randomIndex);

                        Movie movie = null;
                        try {
                            movie = mapper.treeToValue(movieNode, Movie.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Movie in the first call: "+movie);
                        return movie;
                    }).join();

            // Step 2: Fetch detailed movie info using movie ID from step 1
            return client.prepare("GET", String.format("https://api.themoviedb.org/3/movie/%d?language=en-US", randomMovie.getId()))
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", apiKey)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(resp -> {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            return mapper.readValue(resp.getResponseBody(), Movie.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).join();

        } finally {
            client.close();
        }
    }

    public List<StreamingPlatforms> getStreamingPlatforms(Integer movieId) throws IOException {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        List<StreamingPlatforms> response = client.prepare("GET", String.format("https://api.themoviedb.org/3/movie/%d/watch/providers", movieId))
                .setHeader("accept", "application/json")
                .setHeader("Authorization", apiKey)
                .execute()
                .toCompletableFuture()
                .thenApply(resp -> {
                    ObjectMapper objectMapper = new ObjectMapper();

                    JsonNode rootNode;
                    try {
                        rootNode = objectMapper.readTree(resp.getResponseBody());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    JsonNode countryNode = rootNode.path("results").path("US").path("flatrate");
                    if (countryNode.isMissingNode()) {
                        throw new RuntimeException("Country code or flatrate not found.");
                    }

                    try {
                        // Deserialize the JSON array into a List of StreamingPlatforms
                        return objectMapper.readValue(countryNode.toString(), new TypeReference<List<StreamingPlatforms>>() {});
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .join();

        client.close();
        return response;
    }

    public List<Movie> getUserMovies(String username){
        String trimmedUsername = username.trim();
        System.out.println("Searching for username: '" + trimmedUsername + "'");

        Optional<ApplicationUser> user = userRepository.findByUsername(trimmedUsername);
        if (user.isEmpty()) throw new RuntimeException("user not found with given username: " + trimmedUsername);

        System.out.println("user info: " + user);

        List<SavedMovies> savedMovies = savedMoviesRepository.findByUser(Optional.of(user.get()));
        if (savedMovies.isEmpty()) {
            throw new RuntimeException("No movies saved for this user");
        }

        System.out.println("savedmovies: " + savedMovies);

        return savedMovies.stream()
                .map(SavedMovies::getMovie)
                .collect(Collectors.toList());
    }

    //grab random movie by preferences, genre for now
    public Movie getMovieBasedOnGenre(String genre) throws IOException {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Random random = new Random();
        int randNumber = random.nextInt(200);

        try {
            // Step 1: Fetch random movie
            Movie randomMovie = client.prepare("GET", String.format("https://api.themoviedb.org/3/movie/popular?language=en-US&page=%d", randNumber))
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", apiKey)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(resp -> {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = null;
                        try {
                            rootNode = mapper.readTree(resp.getResponseBody());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        JsonNode resultsNode = rootNode.get("results");

                        if (resultsNode == null || !resultsNode.isArray() || resultsNode.size() == 0) {
                            throw new RuntimeException("No movie results found");
                        }
                        JsonNode movieNode = null;
                        int i = 0;
                        boolean genresMatches = false;
                        while (i < 10 && !genresMatches) {
                            System.out.println("SEARCH " + i + " *************");
                            int randomIndex = new Random().nextInt(resultsNode.size());
                            movieNode = resultsNode.get(randomIndex);
                            System.out.println("movie node : " + movieNode);
                            JsonNode genres = movieNode.get("genre_ids");
                            System.out.println("genre node: " + genres);
                            //i need to retrieve the genre id
                            Integer genreId = findGenreId(genre);
                            if (genreId == null) {
                                throw new RuntimeException("Invalid Genre: '" + genre + "'");
                            }
                            //we know the passed genre name is valid
                            //now we look for a movie with the genre
                            for (JsonNode g : genres) {
                                System.out.println("comparing" + g + "  with: " + genreId);
                                if (g.asInt() == genreId) {
                                    // Genre matches
                                    genresMatches = true;
                                    break;
                                }
                            }
                            i++;
                        }

                        if (!genresMatches) {
                            throw new RuntimeException("Couldn't find a movie with the genre you want. Please try again or change genre.");
                        }
                        Movie movie = null;
                        try {
                            movie = mapper.treeToValue(movieNode, Movie.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Movie in the first call: " + movie);
                        return movie;
                    }).join();

            // Step 2: Fetch detailed movie info using movie ID from step 1
            return client.prepare("GET", String.format("https://api.themoviedb.org/3/movie/%d?language=en-US", randomMovie.getId()))
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", apiKey)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(resp -> {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            return mapper.readValue(resp.getResponseBody(), Movie.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).join();

        } finally {
            client.close();
        }
    }

    public CurrentlyWatching addMovieToWatchingSession(String username, Movie movie){
        ApplicationUser user = userRepository.findByUsername(username).get();
        //I know the user exist cause its logged in but check anyway
        if(user.getUserId()==null) throw new RuntimeException("User not found with given username");

        //check if the movie is already in db or save it, then add to the currently watching table
        Integer movieId = movie.getMovieId();
        Optional<Movie> existMovie = movieRepository.findMovieByMovieId(movieId);
        Movie savedMovie;
        if(existMovie.isEmpty()) {
            savedMovie = movieRepository.save(
            Movie.builder()
                    .title(movie.getTitle())
                    .movieId(movie.getMovieId())
                    .overview(movie.getOverview())
                    .release_date(movie.getRelease_date())
                    .genres(movie.getGenres())
                    .poster_path(movie.getPoster_path())
                    .runtime(movie.getRuntime())
                    .vote_average(movie.getVote_average())
                    .original_language(movie.getOriginal_language())
                    .build());
        }else{
            savedMovie=existMovie.get();
        }
        //now we can save the movie in the section
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        CurrentlyWatching currentMovie = new CurrentlyWatching();
        currentMovie.setUser(user);
        currentMovie.setMovie(savedMovie);
        currentMovie.setDate(timestamp);
        currentlyWatchingRepository.save(currentMovie);

        return currentMovie;


    }

    public List<CurrentlyWatchingDto> getCurrentlyWatching(String username){
        ApplicationUser user = userRepository.findByUsername(username).get();

        List<CurrentlyWatching> currentlyWatching = currentlyWatchingRepository.findByUser(user);
        //List<Movie> movies =  currentlyWatchings.stream().map(CurrentlyWatching::getMovie).collect(Collectors.toList());
        List<CurrentlyWatchingDto> movies = new ArrayList<>();
        CurrentlyWatchingDto dto =  new CurrentlyWatchingDto();
        for(CurrentlyWatching movie : currentlyWatching){
            movies.add(dto.from(movie));
        }
        return movies;
    }

    public void deleteMusicFromCurrentlyWatching(String username, Integer movieId){
        ApplicationUser user = userRepository.findByUsername(username).get();
        Movie movie = movieRepository.findById(movieId).get();
        CurrentlyWatching currentMovie = currentlyWatchingRepository.findByUserAndMovie(user,movie);
        currentlyWatchingRepository.deleteById(currentMovie.getId());
    }
}
