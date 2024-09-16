package pasq.cine_scout.ExceptionsHandling;

public class PlaylistException extends RuntimeException {

    public PlaylistException(String message){
        super(message);
    }

    public static PlaylistException playlistAlreadyExist(){
        return new PlaylistException("Playlist already exist with given name.");
    }

    public static PlaylistException playlistNotFound(){
        return new PlaylistException("Playlist not found");
    }


}
