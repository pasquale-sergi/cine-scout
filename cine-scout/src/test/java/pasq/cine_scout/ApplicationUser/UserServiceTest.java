package pasq.cine_scout.ApplicationUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private ApplicationUser testUser;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        //create a user to use in the tests
        testUser = new ApplicationUser();
        testUser.setUserId(1);
        testUser.setEmail("john@gmail.com");
        testUser.setPassword("JohnDorman.99");
        testUser.setUsername("John32");
        testUser.setAuthorities(Set.of(new Role(1, "USER")));
    }
    @Test
    void getUserInfoSuccessfully(){
        String username = "adamo";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserDetailsDto userInfo = userService.getUserInfo(username);
        assertNotNull(userInfo);
        assertEquals(userInfo.getId(), testUser.getUserId());
        assertEquals(userInfo.getUsername(), testUser.getUsername());
        assertEquals(userInfo.getEmail(), testUser.getEmail());
        Mockito.verify(userRepository).findByUsername(username);
    }

}