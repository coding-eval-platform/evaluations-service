package ar.edu.itba.cep.evaluations_service.security.authentication;

import com.bellotapps.webapps_commons.exceptions.UnauthenticatedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

/**
 * A helper class that aids with authentication tasks.
 */
public final class AuthenticationHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private AuthenticationHelper() {
    }


    /**
     * Retrieves the username of the currently authenticated user from the
     * {@link org.springframework.security.core.context.SecurityContext}.
     *
     * @return The username of the currently authenticated user.
     * @throws UnauthenticatedException If there is no authenticated used.
     * @throws IllegalStateException    If the principal in the {@link org.springframework.security.core.Authentication}
     *                                  retrieved from the
     *                                  {@link org.springframework.security.core.context.SecurityContext}
     *                                  is not a {@link String}.
     */
    public static String currentUserUsername() throws UnauthenticatedException, IllegalStateException {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // No user is authenticated
            throw new UnauthenticatedException("Tried to retrieve an username with no authenticated user");
        }
        final var principal = authentication.getPrincipal();
        if (ClassUtils.isAssignable(String.class, principal.getClass())) {
            return (String) principal;
        }
        throw new IllegalStateException("The authentication principal must be a String!");
    }
}
