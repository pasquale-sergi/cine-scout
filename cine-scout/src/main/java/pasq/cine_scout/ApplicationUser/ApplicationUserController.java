package pasq.cine_scout.ApplicationUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class ApplicationUserController {
    @Autowired
    private UserService userService;

    @GetMapping("/details")
    public ResponseEntity<ApplicationUser> getUserInfo(@RequestParam String username){
        ApplicationUser user = userService.getUserInfo(username);
        return ResponseEntity.ok().body(user);
    }
}
