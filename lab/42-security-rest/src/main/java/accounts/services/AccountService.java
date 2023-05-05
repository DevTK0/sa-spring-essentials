package accounts.services;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasRole('ADMIN') && #username == principal")
    public List<String> getAuthoritiesForUser(String username) {

        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities(); // Modify this line

        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
