package pasq.cine_scout.Playlist;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.Movie.Movie;
import pasq.cine_scout.Movie.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;

    public Playlist createPlaylist(String username, String name, String description){
        //check if the name is already used for another playlist
        Optional<Playlist> playlistExist = playlistRepository.findByName(name);
        ApplicationUser user = userRepository.findByUsername(username).get();
        if(playlistExist.isEmpty()){
            //we create that one
            Playlist playlist = Playlist.builder()
                    .name(name)
                    .description(description)
                    .user(user)
                    .build();
            playlistRepository.save(playlist);
            return  playlist;
        }else{
            throw new RuntimeException("Playlist already exist with given name.");
        }
    }

    public PlaylistDto getUserPlaylist(String username, String name){
        ApplicationUser user = userRepository.findByUsername(username).get();
        Optional<Playlist> playlist = playlistRepository.findByNameAndUser(name, user);

        if(playlist.isPresent()){
            PlaylistDto dto = new PlaylistDto();
            dto.setId(playlist.get().getId());
            dto.setDescription(playlist.get().getDescription());
            dto.setUserId(playlist.get().getUser().getUserId());
            dto.setMovies(playlist.get().getMovies().stream().collect(Collectors.toList()));
            dto.setName(name);
            return dto;
        }else{
            throw new RuntimeException("Playlist not found with given name.");
        }

    }

    public List<PlaylistDto> getUserPlaylists(String username){
        ApplicationUser user = userRepository.findByUsername(username).get();
        List<Playlist> playlists = playlistRepository.findAllByUser(user);

        if(!playlists.isEmpty()){

            List<PlaylistDto> dtoList = new ArrayList<>();

            for (Playlist p : playlists) {
                // Create a new PlaylistDto object for each playlist
                PlaylistDto dto = new PlaylistDto();
                dto.setId(p.getId());
                dto.setDescription(p.getDescription());
                dto.setName(p.getName());   
                dto.setUserId(p.getUser().getUserId());

                // Set the list of movie IDs in the DTO
                dto.setMovies(p.getMovies().stream()
                        .collect(Collectors.toList()));

                // Add the DTO to the list
                dtoList.add(dto);
            }

            // Return the list of DTOs
            return dtoList;
        }else{
            throw new RuntimeException("The user doesn't have any playlist.");
        }

    }



    public void deletePlaylist(String name, String username){
        ApplicationUser user = userRepository.findByUsername(username).get();
        Playlist playlist = playlistRepository.findByNameAndUser(name, user).get();
        playlistRepository.deleteById(playlist.getId());
    }
    @Transactional
    public Playlist addMovieToPlaylist(String name, String username, Integer movieId){
        System.out.println("received request to add movie with data: "+username+" " +name+" "+movieId);
        ApplicationUser user = userRepository.findByUsername(username).get();
        Playlist playlist = playlistRepository.findByName(name).get();
        if(!(playlist.getUser()).equals(user)){
            throw new RuntimeException("You don't have permissions to modify this playlist.");
        }
        Movie movie = movieRepository.findById(movieId).orElseThrow(()->new RuntimeException("movie not found"));

        //check if the movie is already in the playlist
        boolean movieExistInPlaylist = playlist.getMovies().stream().anyMatch(m->m.getMovieId().equals(movie.getMovieId()));

        if(movieExistInPlaylist){
            throw new RuntimeException("Movie is already in the playlist");
        }
        playlist.getMovies().add(movie);

        return playlist;
    }

    @Transactional
    public Movie deleteMovieFromPlaylist(String username, String name, Integer movieId) {
            ApplicationUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with given username"));

    Playlist playlist = playlistRepository.findByNameAndUser(name, user)
            .orElseThrow(() -> new RuntimeException("Playlist not found with given name"));

    Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found with given id"));

    // Check if the movie exists in the playlist before attempting to delete
    if (playlist.getMovies().contains(movie)) {
        playlist.getMovies().remove(movie); // Remove the movie from the playlist
        playlistRepository.save(playlist);  // Save the updated playlist
    } else {
        throw new RuntimeException("Movie not found in the playlist");
    }

    return movie;
    }
    @Transactional
    public Playlist updateName(String username, String name, String newName){
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = playlistRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        playlist.setName(newName);

        return playlistRepository.save(playlist);
    }

    @Transactional
    public List<Movie> getMoviesInPlaylist(String name, String username){
        ApplicationUser user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("user not found with given username"));
        Playlist playlist = playlistRepository.findByNameAndUser(name, user).orElseThrow(()->new RuntimeException("Playlist not found"));

        List<Movie> movies = playlist.getMovies().stream().toList();
        return movies;
    }

}
