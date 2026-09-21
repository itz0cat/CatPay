package com.itz0cat.catpay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CatPayConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("CatPay/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static CatPayConfig INSTANCE;

    public boolean enabled = false;
    public boolean enableFakePay = true;
    public boolean enableFakeReceive = true;
    public String selectedProfile = "bananasmp";
    public long maximumFakePayment = 1_000_000_000_000L; // 1 Trillion
    public boolean useCustomIcon = true;
    public String receiveUsername = "JustGhastlyyy";
    public String receiveAmount = "500,000,000";
    public boolean autoUpdateProfiles = true;
    public boolean autoSelectProfileByServer = true;
    public String gitHubRepo = "itz0cat/CatPay";

    public static CatPayConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("catpay").resolve("config.json");
    }

    public static CatPayConfig load() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                CatPayConfig config = GSON.fromJson(reader, CatPayConfig.class);
                if (config != null) {
                    INSTANCE = config;
                    return config;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load CatPay config, reverting to defaults: {}", e.getMessage());
            }
        }
        CatPayConfig newConfig = new CatPayConfig();
        newConfig.save();
        INSTANCE = newConfig;
        return newConfig;
    }

    public void save() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save CatPay config: {}", e.getMessage());
        }
    }
}
