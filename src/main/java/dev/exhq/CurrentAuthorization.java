package dev.exhq;

public record CurrentAuthorization(
        String[] scopes,
        DiscordUser user,
        Application application
) {
}