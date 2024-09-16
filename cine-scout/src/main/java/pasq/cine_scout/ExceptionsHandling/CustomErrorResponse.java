package pasq.cine_scout.ExceptionsHandling;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorResponse {
    private String message;
    private int statusCode;
    private long timestamp;

    public CustomErrorResponse(String message, int statusCode){
        this.message=message;
        this.statusCode= statusCode;
        this.timestamp=System.currentTimeMillis();
    }
}
