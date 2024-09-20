package pasq.cine_scout.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasq.cine_scout.ApplicationUser.ApplicationUser;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApplicationUser> signUp(@RequestBody RegisterDto request){
        ApplicationUser userRegistered = authService.signUp(request.getEmail(), request.getUsername(), request.getPassword() );
        return ResponseEntity.ok().body(userRegistered);
    }

    @PostMapping("/login")
    public LoginResponseDto loginUser(@RequestBody LoginDto request){
        return authService.loginUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/check-password")
    public Boolean checkPassword(@RequestBody LoginDto request){
        return authService.checkPassword(request.getUsername(), request.getPassword());
    }

    @PutMapping("/update-password")
    public void updatePassword(@RequestBody LoginDto request){
        authService.updatePassword(request.getUsername(), request.getPassword());
    }
}
