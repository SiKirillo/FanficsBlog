package fanficsBlog.security;

import fanficsBlog.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import fanficsBlog.models.User;
import fanficsBlog.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthProviderImpl implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        User user = userRepository.findByUsername(name);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String password = authentication.getCredentials().toString();
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        Boolean isBanned = user.getActive();
        if (!isBanned) {
            try {
                throw new UserIsBannedException("You have been blocked");
            } catch (UserIsBannedException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoles().contains(Role.ADMIN)) {
            authorities.add(Role.ADMIN);
        }
        if (user.getRoles().contains(Role.USER)) {
            authorities.add(Role.USER);
        }

        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}