package com.ecommerce.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.security.UserPrincipal;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) {
			throw new BadRequestException("Not authenticated");
		}
		return up.getUser();
	}
}
