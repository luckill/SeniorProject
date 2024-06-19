package com.example.SeniorProject.Service;

import org.springframework.stereotype.*;

import java.util.*;

@Service
public class JwtTokenBlacklistService
{
    private final Map<String, Set<String>> userTokens = new HashMap<>();
    private final Set<String> blacklistedTokens = new HashSet<>();

    public void blacklistToken(String token)
    {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token)
    {
        return blacklistedTokens.contains(token);
    }

    public void addTokenForUser(String username, String token)
    {
        userTokens.computeIfAbsent(username, k -> new HashSet<>()).add(token);
    }

    public void invalidateTokensForUser(String username)
    {
        Set<String> tokens = userTokens.get(username);
        if(tokens != null)
        {
            blacklistedTokens.addAll(tokens);
            userTokens.remove(username);
        }
    }
}
