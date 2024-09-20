package pasq.cine_scout.ExceptionsHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pasq.cine_scout.Playlist.Playlist;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(PlaylistException.class)
    public ResponseEntity<CustomErrorResponse> handlePlaylistAlreadyExist(PlaylistException ex){
        CustomErrorResponse error = new CustomErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<CustomErrorResponse> handleUserException(UserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

}
