package dev.exhq;

public record DiscordUser(
        String id,
        String username,
        String discriminator
) {
}