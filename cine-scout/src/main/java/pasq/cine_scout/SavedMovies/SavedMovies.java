package pasq.cine_scout.SavedMovies;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;

import java.util.Date;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_movies")
public class SavedMovies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private Date savedDate;


    @Override
    public String toString() {
        return "SavedMovies{id=" + id + ", movie=" + movie.getTitle() + ", savedDate=" + savedDate + "}";
    }

}