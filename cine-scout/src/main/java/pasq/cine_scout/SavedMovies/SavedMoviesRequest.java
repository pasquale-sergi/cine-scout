package pasq.cine_scout.SavedMovies;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pasq.cine_scout.Movie.Movie;

@Getter
@Setter
@Data
public class SavedMoviesRequest {

    private String username;
    private Movie movie;
}
