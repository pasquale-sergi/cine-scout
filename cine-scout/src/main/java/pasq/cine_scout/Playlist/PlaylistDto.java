package pasq.cine_scout.Playlist;

import lombok.Getter;
import lombok.Setter;
import pasq.cine_scout.Movie.Movie;

import java.util.List;
@Getter
@Setter
public class PlaylistDto {

    private Integer id;

    private String name;
    private Integer userId;
    private List<Movie> movies;
}
