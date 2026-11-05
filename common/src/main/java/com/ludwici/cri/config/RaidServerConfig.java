package com.ludwici.cri.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.ludwici.cri.CobblemonRaidDenIcons;
import dev.architectury.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RaidServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean requirementEnabled;
    private static ResourceLocation requiredItem;
    private static TagKey<Item> requiredTag;

    private RaidServerConfig() {}

    public static void load() {
        Path path = Platform.getConfigFolder().resolve("cri-server.json");
        String configuredId = "";
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.writeString(path, GSON.toJson(new ConfigData()) + "\n", StandardCharsets.UTF_8);
            }
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null && data.requiredItemId != null) {
                    configuredId = data.requiredItemId.trim();
                }
            }
        } catch (IOException | JsonParseException e) {
            CobblemonRaidDenIcons.LOGGER.error("Failed to load server raid icon configuration", e);
            configuredId = "cri:invalid_config";
        }

        requirementEnabled = !configuredId.isBlank() && !"-".equals(configuredId);
        requiredItem = null;
        requiredTag = null;
        if (!requirementEnabled) {
            return;
        }

        boolean isTag = configuredId.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(isTag ? configuredId.substring(1) : configuredId);
        if (id == null) {
            CobblemonRaidDenIcons.LOGGER.warn("Invalid requiredItemId '{}' in cri-server.json; raid icons will stay disabled", configuredId);
            return;
        }
        if (isTag) {
            requiredTag = TagKey.create(Registries.ITEM, id);
            return;
        }
        if (BuiltInRegistries.ITEM.getOptional(id).isEmpty()) {
            CobblemonRaidDenIcons.LOGGER.warn("Unknown item '{}' in cri-server.json; raid icons will stay disabled", configuredId);
            return;
        }
        requiredItem = id;
    }

    public static boolean allowed(ServerPlayer player) {
        if (!requirementEnabled) {
            return true;
        }
        if (requiredItem == null && requiredTag == null) {
            return false;
        }
        Item item = requiredItem == null ? null : BuiltInRegistries.ITEM.getOptional(requiredItem).orElse(null);
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && ((item != null && stack.is(item)) || (requiredTag != null && stack.is(requiredTag)))) {
                return true;
            }
        }
        return false;
    }

    private static final class ConfigData {
        private String requiredItemId = "";
    }
}
