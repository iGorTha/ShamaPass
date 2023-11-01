package fr.igortha.shamaPass.placeholder;

import fr.igortha.shamaPass.Main;
import fr.igortha.shamaPass.database.PointsDatabase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class XpPlaceHolder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "shama_player";
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
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        PointsDatabase pointsDatabase = Main.getInstance().getPointsDatabase();
        return switch (identifier) {
            case "xp" -> Integer.toString(pointsDatabase.getPoint(player, player));
            case "pass_level" -> Integer.toString(pointsDatabase.getLevel(player, player));
            default -> null;
        };
    }
}
