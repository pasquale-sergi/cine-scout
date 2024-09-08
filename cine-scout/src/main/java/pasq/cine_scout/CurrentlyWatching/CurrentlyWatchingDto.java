package pasq.cine_scout.CurrentlyWatching;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CurrentlyWatchingDto {
    private Integer id;
    private Integer movieId;
    private String title;

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
        return dto;
    }
}
