package pasq.cine_scout.ExceptionsHandling;

import org.springframework.security.core.userdetails.User;

public class UserException extends RuntimeException{

    public UserException(String message){
        super(message);
    }

    public static UserException userNotFoundWithEmail(){
        return new UserException("Invalid email address.");
    }
    public static UserException passwordNotValid(){
        return new UserException("Invalid password");
    }

    public static UserException userNotFoundWithUsername(){return new UserException("Invalid username.");}
    //for signup
    public static UserException accountAlreadyExist(){
        return new UserException("This email address is already registered.");
    }
}
