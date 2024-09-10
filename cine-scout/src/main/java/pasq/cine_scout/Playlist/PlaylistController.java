package pasq.cine_scout.Playlist;

import org.asynchttpclient.Response;
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
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String username, @RequestParam String name){
        Playlist playlist= playlistService.createPlaylist(username, name);
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


}
