package accounts.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		// if (!checkCustomAuthenticationSystem(username, password)) {
		// throw new BadCredentialsException("Bad credentials provided");
		// }
		Authentication auth = null;

		if (username.equals("spring") && password.equals("spring")) {
			auth = new UsernamePasswordAuthenticationToken(
					username, password, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		} else if (username.equals("user") && password.equals("user")) {
			auth = new UsernamePasswordAuthenticationToken(
					username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
		} else if (username.equals("admin") && password.equals("admin")) {
			auth = new UsernamePasswordAuthenticationToken(
					username, password, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
		} else if (username.equals("superadmin") && password.equals("superadmin")) {
			auth = new UsernamePasswordAuthenticationToken(
					username, password,
					AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN"));
		}

		if (auth == null)
			throw new BadCredentialsException("Bad credentials provided");

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(auth);

		return auth;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	// Use custom authentication system for the verification of the
	// passed username and password. (Here we are just faking it.)
	private boolean checkCustomAuthenticationSystem(String username, String password) {
		return username.equals("spring") && password.equals("spring");
	}
}
