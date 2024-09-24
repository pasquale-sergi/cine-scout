package pasq.cine_scout.Auth;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pasq.cine_scout.ApplicationUser.ApplicationUser;
import pasq.cine_scout.ApplicationUser.Role;
import pasq.cine_scout.ApplicationUser.RoleRepository;
import pasq.cine_scout.ApplicationUser.UserRepository;
import pasq.cine_scout.ExceptionsHandling.UserException;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private RoleRepository roleRepository;
    private ApplicationUser testUser;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        testUser = new ApplicationUser();
        testUser.setUserId(1);
        testUser.setEmail("john@gmail.com");
        testUser.setPassword("JohnDorman.99");
        testUser.setUsername("John32");
        testUser.setAuthorities(Set.of(new Role(1, "USER")));
    }
    @Test
    void signUpSuccess(){
        //user info to sign up
        String email = "adamo@gmail.com";
        String username = "adamo";
        String password = "AdamoDeGiglio.00";
        Role userRole = new Role(1, "USER");
        ApplicationUser user = new ApplicationUser(1, email, username, "encodedPassword", new HashSet<>());

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(ApplicationUser.class))).thenReturn(user);

        ApplicationUser result = authService.signUp(email, username, password);

        assertNotNull(result);
        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.getUsername(), user.getUsername());
        verify(userRepository).save(any(ApplicationUser.class));
    }

    @Test
    void loginUserSuccess(){
        String username = "John32";
        String password = "JohnDorman99";
        String token = "fakeJwtToken";

        Authentication authentication = mock(Authentication.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateJwt(authentication)).thenReturn(token);

        LoginResponseDto loginResponse = authService.loginUser(username, password);

        assertNotNull(loginResponse);
        assertEquals(username, loginResponse.getUsername());
        assertEquals(token, loginResponse.getJwt());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateJwt(authentication);
    }

    @Test
    void loginUserFailedNotFound(){
        String username = "John";
        String password = "JohnDorman99";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserException.class, ()-> authService.loginUser(username, password));
        verify(userRepository).findByUsername(username);
        verify(authenticationManager, never()).authenticate(any());

    }

    @Test
    void loginUserFailInvalidPassword() {
        // Arrange
        String username = "John32";
        String password = "wrongPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(UserException.passwordNotValid());

        // Act & Assert
        assertThrows(UserException.class, () -> authService.loginUser(username, password));
        verify(userRepository).findByUsername(username);
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void updatePasswordSuccess(){
        String username = "John32";
        String newPassword = "JohnTravolta.99";
        String encodedPassword = "encodedPassword";

        // Mock the behavior of userRepository and passwordEncoder
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // Call the method being tested
        authService.updatePassword(username, newPassword);

        // Verify the password was encoded and saved
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);

        // Assert the password was updated correctly
        assertEquals(encodedPassword, testUser.getPassword());
    }

}