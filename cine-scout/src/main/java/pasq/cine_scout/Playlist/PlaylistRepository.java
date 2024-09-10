package pasq.cine_scout.Playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.Movie.Movie;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    Optional<Playlist> findByName(String name);

    Optional<Playlist> findByNameAndUser(String name, ApplicationUser user);

    List<Playlist> findAllByUser(ApplicationUser user);

    // Assuming a many-to-many or one-to-many relationship between Playlist and Movie
    void deleteMovieFromPlaylistByMovies(Playlist playlist,Integer movieId);


}
