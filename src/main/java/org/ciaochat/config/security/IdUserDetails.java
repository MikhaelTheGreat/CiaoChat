package org.ciaochat.config.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface IdUserDetails extends UserDetails {
    Long getId();
}
