package pasq.cine_scout.Movie;

import lombok.Getter;
import lombok.Setter;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
@Getter
@Setter
public class MovieToDeleteRequest {

    private ApplicationUser user;
    private Movie movie;
}
