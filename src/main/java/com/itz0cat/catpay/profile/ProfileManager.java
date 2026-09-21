package com.itz0cat.catpay.profile;

import com.google.gson.Gson;
import com.itz0cat.catpay.config.CatPayConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class ProfileManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("CatPay/ProfileManager");
    private static final Gson GSON = new Gson();
    private static ProfileManager INSTANCE;

    private final Map<String, PaymentProfile> profiles = new HashMap<>();

    public static ProfileManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ProfileManager();
        }
        return INSTANCE;
    }

    public static Path getProfilesDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("catpay").resolve("profiles");
    }

    public void init() {
        loadLocalProfiles();
    }

    public void loadLocalProfiles() {
        Path dir = getProfilesDir();
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                copyResourceProfile("bananasmp.json");
                copyResourceProfile("default.json");
                copyResourceProfile("profile-index.json");
            }

            File[] files = dir.toFile().listFiles((d, name) -> name.endsWith(".json") && !name.equals("profile-index.json"));
            if (files != null) {
                for (File file : files) {
                    try (FileReader reader = new FileReader(file)) {
                        PaymentProfile profile = GSON.fromJson(reader, PaymentProfile.class);
                        if (profile != null && profile.getId() != null) {
                            profiles.put(profile.getId().toLowerCase(Locale.ROOT), profile);
                            LOGGER.info("Loaded profile: {} ({})", profile.getName(), profile.getId());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to parse profile {}: {}", file.getName(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load local profiles: {}", e.getMessage());
        }

        // Fallback default profiles if none found
        if (!profiles.containsKey("bananasmp")) {
            PaymentProfile bananasmp = new PaymentProfile();
            bananasmp.setId("bananasmp");
            bananasmp.setName("BananaSMP.net");
            bananasmp.setServerMatchers(List.of("bananasmp.net", "*.bananasmp.net"));
            bananasmp.getIcon().setEnabled(true);
            bananasmp.getIcon().setType("unicode");
            bananasmp.getIcon().setGlyph("\uE058");
            bananasmp.getPayment().setSent("<#55FFAA>{icon} ${amount}<gray> has been sent to <white>{user}.");
            bananasmp.getPayment().setReceived("<#55FFAA>{icon} ${amount}<gray> has been received from <white>{user}.");
            profiles.put("bananasmp", bananasmp);
        }

        if (!profiles.containsKey("default")) {
            PaymentProfile def = new PaymentProfile();
            def.setId("default");
            def.setName("Default");
            def.getIcon().setEnabled(false);
            def.getPayment().setSent("${amount} has been sent to {user}.");
            def.getPayment().setReceived("${amount} has been received from {user}.");
            profiles.put("default", def);
        }
    }

    private void copyResourceProfile(String filename) {
        Path target = getProfilesDir().resolve(filename);
        try (InputStream in = getClass().getResourceAsStream("/profiles/" + filename)) {
            if (in != null) {
                Files.copy(in, target);
            }
        } catch (Exception e) {
            LOGGER.warn("Could not copy default resource profile {}: {}", filename, e.getMessage());
        }
    }

    public void registerProfile(PaymentProfile profile) {
        if (profile != null && profile.getId() != null) {
            profiles.put(profile.getId().toLowerCase(Locale.ROOT), profile);
        }
    }

    public PaymentProfile getActiveProfile() {
        String selected = CatPayConfig.get().selectedProfile;
        if (selected != null) {
            PaymentProfile profile = profiles.get(selected.toLowerCase(Locale.ROOT));
            if (profile != null) return profile;
        }

        // Fallback to bananasmp or default
        PaymentProfile bananasmp = profiles.get("bananasmp");
        if (bananasmp != null) return bananasmp;

        PaymentProfile def = profiles.get("default");
        if (def != null) return def;

        return new PaymentProfile();
    }

    public Collection<PaymentProfile> getAllProfiles() {
        return profiles.values();
    }

    public void selectProfileByServer(String serverAddress) {
        if (serverAddress == null || !CatPayConfig.get().autoSelectProfileByServer) {
            return;
        }

        String lowerAddress = serverAddress.toLowerCase(Locale.ROOT);
        for (PaymentProfile profile : profiles.values()) {
            for (String matcher : profile.getServerMatchers()) {
                if (matchesServer(lowerAddress, matcher.toLowerCase(Locale.ROOT))) {
                    CatPayConfig.get().selectedProfile = profile.getId();
                    CatPayConfig.get().save();
                    LOGGER.info("Auto-selected profile '{}' for server '{}'", profile.getName(), serverAddress);
                    return;
                }
            }
        }
    }

    private boolean matchesServer(String address, String pattern) {
        if (pattern.equals(address)) return true;
        if (pattern.startsWith("*.")) {
            String suffix = pattern.substring(2);
            return address.endsWith(suffix) || address.equals(suffix);
        }
        return false;
    }
}
