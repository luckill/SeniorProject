package com.example.SeniorProject.Configuration;

import com.example.SeniorProject.Service.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.lang.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;
import org.springframework.web.servlet.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;

    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService, UserDetailsService userDetailsService, JwtTokenBlacklistService jwtTokenBlacklistService)
    {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
    {
        final String header = request.getHeader("Authorization");
        try
        {
            if(header == null || !header.startsWith("Bearer "))
            {
                filterChain.doFilter(request, response);
                return;
            }
            final String jwt  = header.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(userEmail != null && authentication == null)
            {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails) && !jwtTokenBlacklistService.isTokenBlacklisted(jwt))
                {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
