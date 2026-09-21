package com.itz0cat.catpay.util;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerResolver {

    public static List<String> getKnownPlayerNames() {
        List<String> names = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler != null) {
            Collection<PlayerListEntry> entries = handler.getPlayerList();
            for (PlayerListEntry entry : entries) {
                if (entry.getProfile() != null && entry.getProfile().name() != null) {
                    names.add(entry.getProfile().name());
                }
            }
        }
        return names;
    }

    public static SuggestionProvider<FabricClientCommandSource> getSuggestionProvider() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            for (String name : getKnownPlayerNames()) {
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
            return builder.buildFuture();
        };
    }
}
