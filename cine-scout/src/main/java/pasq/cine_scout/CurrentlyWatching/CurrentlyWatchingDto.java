package pasq.cine_scout.CurrentlyWatching;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pasq.cine_scout.Movie.GenresData;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CurrentlyWatchingDto {
    private Integer id;
    private Integer movieId;
    private String title;
    private String overview;
    private String release_date;
    private int runtime;
    private double vote_average;
    private String original_language;
    private Double rating;
    private List<GenresData> genres;
    private String poster_path;

    // Static method for conversion
    public CurrentlyWatchingDto from(CurrentlyWatching currentlyWatching) {
        if (currentlyWatching == null || currentlyWatching.getMovie() == null) {
            return null;
        }

        CurrentlyWatchingDto dto = new CurrentlyWatchingDto();
        dto.setId(currentlyWatching.getMovie().getId());
        dto.setTitle(currentlyWatching.getMovie().getTitle());
        dto.setPoster_path(currentlyWatching.getMovie().getPoster_path());
        dto.setMovieId(currentlyWatching.getMovie().getMovieId());
        dto.setOverview(currentlyWatching.getMovie().getOverview());
        dto.setRuntime(currentlyWatching.getMovie().getRuntime());
        dto.setOriginal_language(currentlyWatching.getMovie().getOriginal_language());
        dto.setVote_average(currentlyWatching.getMovie().getVote_average());
        dto.setRelease_date(currentlyWatching.getMovie().getRelease_date());
        dto.setRating(currentlyWatching.getMovie().getRating());
        dto.setGenres(currentlyWatching.getMovie().getGenres());
        return dto;
    }
}
