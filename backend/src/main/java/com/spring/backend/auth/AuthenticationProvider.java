package com.spring.backend.auth;

import com.spring.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    UserRepository userRepository;

    @Value("${private.session-timeout}")
    private int sessionTimeout;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }


    @Override
    protected UserDetails retrieveUser(String userName,
                                       UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
                                       throws AuthenticationException {

        Object token = usernamePasswordAuthenticationToken.getCredentials();
        Optional<com.spring.backend.models.User> uu = userRepository.findByToken(String.valueOf(token));
        if (uu.isEmpty())
            throw new UsernameNotFoundException("user is not found");
        com.spring.backend.models.User u = uu.get();

        boolean timeout = true;
        LocalDateTime dt  = LocalDateTime.now();
        if (u.getActivity() != null) {
            LocalDateTime nt = u.getActivity().plusMinutes(10);
            if (dt.isBefore(nt))
                timeout = false;
        }
        if (timeout) {
            u.setToken(null);
            userRepository.save(u);
            throw new NonceExpiredException("session is expired");
        }
        else {
            u.setActivity(dt);
            userRepository.save(u);
        }

        return new User(u.getLogin(), u.getPassword(),
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList("USER"));
    }
}
