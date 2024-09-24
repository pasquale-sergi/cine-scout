package pasq.cine_scout.ApplicationUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDto {
    private String username;
    private String email;
    private Integer id;
}
