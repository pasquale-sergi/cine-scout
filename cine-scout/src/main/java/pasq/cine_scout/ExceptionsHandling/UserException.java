package pasq.cine_scout.ExceptionsHandling;

public class UserException extends RuntimeException{

    public UserException(String message){
        super(message);
    }

    public static UserException userNotFoundWithEmail(){
        return new UserException("Invalid email address");
    }
    public static UserException passwordNotValid(){
        return new UserException("Invalid password");
    }

    //for signup
    public static UserException accountAlreadyExist(){
        return new UserException("This email address is already registered.");
    }
}
