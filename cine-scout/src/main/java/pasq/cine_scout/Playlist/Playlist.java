package pasq.cine_scout.Playlist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;
import pasq.cine_scout.SavedMovies.SavedMovies;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;
    @ManyToOne
    @JoinColumn(name="user_id")
    private ApplicationUser user;
    @ManyToMany
    @JoinTable(
            name="playlist_movie",
            joinColumns = @JoinColumn(name="playlist_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id")
    )
    private Set<Movie> movies = new HashSet<>();

}
