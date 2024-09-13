package pasq.cine_scout.Playlist;

import org.asynchttpclient.Response;
import org.hibernate.annotations.OnDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasq.cine_scout.Movie.Movie;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;
    @PostMapping("/create")
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String username, @RequestBody PlaylistAddRequest request){
        Playlist playlist= playlistService.createPlaylist(username, request.getName(), request.getDescription());
        return ResponseEntity.ok().body(playlist);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlaylistDto>> getUserPlaylists(@RequestParam String username){
        List<PlaylistDto> playlists = playlistService.getUserPlaylists(username);
        return ResponseEntity.ok().body(playlists);
    }

    @GetMapping("/get")
    public ResponseEntity<PlaylistDto> getUserPlaylist(@RequestParam String username, @RequestParam String name){
        PlaylistDto playlist = playlistService.getUserPlaylist(username, name);
        return ResponseEntity.ok().body(playlist);
    }

    @PostMapping("/add-movie")
    public ResponseEntity<Playlist> addMovieToPlaylist(@RequestParam String name,@RequestParam String username,@RequestParam Integer movieId){
        Playlist playlistUpdated = playlistService.addMovieToPlaylist(name, username, movieId);
        return ResponseEntity.ok().body(playlistUpdated);
    }

    @DeleteMapping("/delete-movie")
    public ResponseEntity<Movie> deleteMovieFromPlaylist(@RequestParam String username, @RequestParam String name, @RequestParam Integer movieId){
        Movie movie = playlistService.deleteMovieFromPlaylist(username, name, movieId);
        return ResponseEntity.ok().body(movie);
    }

    @PutMapping("/{username}/update-name")
    public ResponseEntity<Playlist> editName(@PathVariable String username,@RequestParam String name, @RequestParam String newName){
        Playlist playlist = playlistService.updateName(username, name, newName);
        return ResponseEntity.ok().body(playlist);
    }



    @DeleteMapping("/{username}/delete")
    public ResponseEntity<String> deletePlaylist(@PathVariable String username, @RequestParam String name){
        playlistService.deletePlaylist(name, username);
        return ResponseEntity.ok().body("Playlist deleted.");
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getPlaylistMovies(@RequestParam String username, @RequestParam String name){
        List<Movie> movies = playlistService.getMoviesInPlaylist(name, username);
        return ResponseEntity.ok().body(movies);
    }
}
