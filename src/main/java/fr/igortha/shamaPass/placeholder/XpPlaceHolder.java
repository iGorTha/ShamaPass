package fr.igortha.shamaPass.placeholder;

import fr.igortha.shamaPass.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class XpPlaceHolder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "shama";
    }

    @Override
    public @NotNull String getAuthor() {
        return "iGorTha";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String placeholder) {
        if (player != null) {
            if (!Main.getInstance().getPointsDatabase().playerExists(player)) {
                return "0";
            }
            switch (placeholder.toLowerCase()) {
                case "xp":
                    return Integer.toString(Main.getInstance().getPointsDatabase().getPoint(player));
                case "pass_level":
                    return Integer.toString(Main.getInstance().getPointsDatabase().getLevel(player));
            }
        }
        return null;
    }
}
