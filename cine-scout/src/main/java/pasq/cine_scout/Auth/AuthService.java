package pasq.cine_scout.Auth;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.Role;
import pasq.cine_scout.ApplicationUser.RoleRepository;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.ExceptionsHandling.UserException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    public ApplicationUser signUp(String email, String username, String password){

        //check for user already registered
        Optional<ApplicationUser> userByEmail = userRepository.findByEmail(email);
        if(userByEmail.isPresent()){
            throw UserException.accountAlreadyExist();
        }

        Optional<ApplicationUser> userByUsername = userRepository.findByUsername(username);
        if(userByUsername.isPresent()){
            throw UserException.usernameAlreadyUsed();
        }
        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(0,email, username, encodedPassword, authorities ));
    }

    public LoginResponseDto loginUser(String username, String password){

        //check if username valid
        Optional<ApplicationUser> usernameCheck = userRepository.findByUsername(username);
        if(usernameCheck.isEmpty()) throw UserException.userNotFoundWithUsername();
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);
            ApplicationUser user = userRepository.findByUsername(username).get();

            return new LoginResponseDto(user.getUserId(),user.getEmail(), user.getUsername(), token);
        }catch (AuthenticationException e){
            throw UserException.passwordNotValid();
        }
    }

    public Boolean checkPassword(String username, String password){
        ApplicationUser user = userRepository.findByUsername(username).orElseThrow(UserException::userNotFoundWithUsername);

        return passwordEncoder.matches(password, user.getPassword());
    }

    public void updatePassword(String username, String newPassword){
        ApplicationUser user = userRepository.findByUsername(username).orElseThrow(UserException::userNotFoundWithUsername);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
