package com.dwb.security.util;

import com.dwb.exception.custom.BadRequestException;
import com.dwb.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    // Returns the email of the currently logged-in user from the JWT token.
    // Use this in any service that needs to know who is making the request.
    // The service then fetches the full User from DB using this email.
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user.getEmail();
        }

        throw new BadRequestException("Not authenticated");
    }
}
