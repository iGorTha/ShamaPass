package fr.igortha.shamaPass.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class XpPassPlaceHolder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "player_pass_level";
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
