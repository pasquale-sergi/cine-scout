package pasq.cine_scout.ExceptionsHandling;

public class MovieException extends RuntimeException{

    public MovieException(String message){
        super(message);
    }

    public static MovieException movieAlreadySaved(){
        return new MovieException("Movie has already been saved");
    }
}
