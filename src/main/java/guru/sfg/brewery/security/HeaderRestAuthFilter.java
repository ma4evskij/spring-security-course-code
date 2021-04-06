/*
 * Copyright 2020 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
public class HeaderRestAuthFilter extends AbstractRestAuthFilter {

    public HeaderRestAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    protected String getPassword(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader("Api-Secret"))
                .orElse("");
    }

    protected String getUsername(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader("Api-Key"))
                .orElse("");
    }
}
