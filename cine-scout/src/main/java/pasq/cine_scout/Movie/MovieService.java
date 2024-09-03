package pasq.cine_scout.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import pasq.cine_scout.ApplicationUser.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;


    @Value("${api.themoviedb.key}")
    private String apiKey;

    public Movie getRandomMovieWithDetails() throws IOException {
        AsyncHttpClient client = new DefaultAsyncHttpClient();

        try {
            // Step 1: Fetch random movie
            Movie randomMovie = client.prepare("GET", "https://api.themoviedb.org/3/movie/popular?language=en-US&page=20")
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

}