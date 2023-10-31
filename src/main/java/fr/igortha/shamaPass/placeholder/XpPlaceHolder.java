package fr.igortha.shamaPass.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class XpPlaceHolder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "player_xp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "igortha";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}
