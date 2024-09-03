package pasq.cine_scout.Movie;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "genres")
public class GenresData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // Optional: to link back to a specific movie
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Constructor without the movie parameter
    public GenresData(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Static list of genres, not persisted
    @Transient
    public static final List<GenresData> GENRES = new ArrayList<>(Arrays.asList(
            new GenresData(28, "Action"),
            new GenresData(12, "Adventure"),
            new GenresData(16, "Animation"),
            new GenresData(35, "Comedy"),
            new GenresData(80, "Crime"),
            new GenresData(99, "Documentary"),
            new GenresData(18, "Drama"),
            new GenresData(10751, "Family"),
            new GenresData(14, "Fantasy"),
            new GenresData(36, "History"),
            new GenresData(27, "Horror"),
            new GenresData(10402, "Music"),
            new GenresData(9648, "Mystery"),
            new GenresData(10749, "Romance"),
            new GenresData(878, "Science Fiction"),
            new GenresData(10770, "TV Movie"),
            new GenresData(53, "Thriller"),
            new GenresData(10752, "War"),
            new GenresData(37, "Western")
    ));

    // Optional: Override equals and hashCode for better collection management
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenresData genre = (GenresData) o;
        return id != null && id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}