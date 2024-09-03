package pasq.cine_scout.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pasq.cine_scout.ApplicationUser.ApplicationUser;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private ApplicationUser user;
    private String jwt;

}
