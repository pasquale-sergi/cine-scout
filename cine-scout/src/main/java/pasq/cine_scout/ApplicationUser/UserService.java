package pasq.cine_scout.ApplicationUser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found."));
    }

   public UserDetailsDto getUserInfo(String username) {
       ApplicationUser user = userRepository.findByUsername(username).get();
       UserDetailsDto userInfo = new UserDetailsDto();
       userInfo.setId(user.getUserId());
       userInfo.setUsername(user.getUsername());
       userInfo.setEmail(user.getEmail());
       return userInfo;
   }}