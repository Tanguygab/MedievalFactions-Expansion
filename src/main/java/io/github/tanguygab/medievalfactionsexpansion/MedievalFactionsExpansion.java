package io.github.tanguygab.medievalfactionsexpansion;

import com.dansplugins.factionsystem.MedievalFactions;
import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.faction.MfFactionService;
import com.dansplugins.factionsystem.law.MfLaw;
import com.dansplugins.factionsystem.law.MfLawService;
import com.dansplugins.factionsystem.player.MfPlayerId;
import com.dansplugins.factionsystem.relationship.MfFactionRelationshipService;
import com.dansplugins.factionsystem.service.Services;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Collectors;

public final class MedievalFactionsExpansion extends PlaceholderExpansion {

    private final Services services = MedievalFactions.getPlugin(MedievalFactions.class).services;
    private final MfFactionService factions = services.getFactionService();
    private final MfFactionRelationshipService relations = services.getFactionRelationshipService();
    private final MfLawService laws = services.getLawService();

    @Override
    public @NotNull String getIdentifier() {
        return "medievalfactions+";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Tanguygab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getRequiredPlugin() {
        return "MedievalFactions";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!(player instanceof Player p)) return null;

        MfFaction faction = factions.getFactionByPlayerId(p.getUniqueId().toString());
        if (faction == null) return "";
        String del = ", ";
        if (params.endsWith("_nl")) {
            del = "\n";
            params = params.substring(0,params.length()-3);
        }

        if (params.startsWith("members_with_")) {
            String role = params.substring(13);
            return faction.getMembers().stream()
                    .filter(m->m.getRole() == faction.getRoleByName(role))
                    .map(member -> Bukkit.getServer().getOfflinePlayer(UUID.fromString(member.getPlayerId())).getName())
                    .collect(Collectors.joining(del));
        }


        return switch (params) {
            case "enemies" -> relations.getFactionsAtWarWithByFactionId(faction.getId())
                    .stream()
                    .map(f->factions.getFactionByPlayerId(f.getValue()).getName())
                    .collect(Collectors.joining(del));
            case "allies" -> relations.getAlliesByFactionId(faction.getId())
                    .stream()
                    .map(f->factions.getFactionByPlayerId(f.getValue()).getName())
                    .collect(Collectors.joining(del));
            case "description" -> faction.getDescription();
            case "laws" -> laws.getLawsByFactionId(faction.getId())
                    .stream()
                    .map(MfLaw::getText)
                    .collect(Collectors.joining(", "));
            default -> null;
        };
    }
}
