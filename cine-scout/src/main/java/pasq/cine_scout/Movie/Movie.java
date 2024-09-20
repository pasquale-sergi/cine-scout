package pasq.cine_scout.Movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="movies")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="movie_id")
    private Integer movieId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String release_date;
    private int runtime;
    private double vote_average;
    private String poster_path;
    private String original_language;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // LAZY fetching for better performance
    @JoinColumn(name = "movie_id")
    @JsonManagedReference
    private List<GenresData> genres;
    private long budget;
    private long revenue;
    private Double rating;

}
