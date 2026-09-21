package com.itz0cat.catpay.profile;

import com.google.gson.Gson;
import com.itz0cat.catpay.config.CatPayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteProfileSync {
    private static final Logger LOGGER = LoggerFactory.getLogger("CatPay/RemoteSync");
    private static final Gson GSON = new Gson();
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "CatPay-RemoteProfileSync");
        t.setDaemon(true);
        return t;
    });

    private static final int TIMEOUT_MS = 5000;

    public static CompletableFuture<Boolean> syncProfilesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            CatPayConfig config = CatPayConfig.get();
            String repo = config.gitHubRepo;
            if (repo == null || repo.trim().isEmpty()) {
                LOGGER.warn("GitHub repository not configured, skipping remote profile sync.");
                return false;
            }

            String baseUrl = "https://raw.githubusercontent.com/" + repo.trim() + "/main/profiles/";
            LOGGER.info("Checking for remote profile updates from {}", baseUrl);

            try {
                String indexUrl = baseUrl + "profile-index.json";
                String indexJson = fetchUrl(indexUrl);
                if (indexJson == null) {
                    LOGGER.warn("Failed to fetch remote profile-index.json from GitHub.");
                    return false;
                }

                ProfileIndex index = GSON.fromJson(indexJson, ProfileIndex.class);
                if (index == null || index.getProfiles() == null) {
                    LOGGER.warn("Invalid profile-index.json received from GitHub.");
                    return false;
                }

                Path profilesDir = ProfileManager.getProfilesDir();
                Files.createDirectories(profilesDir);

                // Save index locally
                Files.writeString(profilesDir.resolve("profile-index.json"), indexJson, StandardCharsets.UTF_8);

                int updatedCount = 0;
                for (ProfileIndex.ProfileEntry entry : index.getProfiles()) {
                    if (entry.getFile() == null || entry.getFile().contains("..") || entry.getFile().contains("/")) {
                        LOGGER.warn("Rejecting invalid profile filename: {}", entry.getFile());
                        continue;
                    }

                    String profileUrl = baseUrl + entry.getFile();
                    String profileJson = fetchUrl(profileUrl);
                    if (profileJson != null) {
                        try {
                            PaymentProfile profile = GSON.fromJson(profileJson, PaymentProfile.class);
                            if (profile != null && profile.getId() != null && profile.getPayment() != null) {
                                // Save validated profile to disk cache
                                Path targetPath = profilesDir.resolve(entry.getFile());
                                Files.writeString(targetPath, profileJson, StandardCharsets.UTF_8);

                                // Register in profile manager
                                ProfileManager.get().registerProfile(profile);
                                updatedCount++;
                                LOGGER.info("Synced profile '{}' from GitHub.", profile.getName());
                            } else {
                                LOGGER.warn("Rejecting malformed profile from {}: missing required fields", entry.getFile());
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Failed to parse remote profile {}: {}", entry.getFile(), e.getMessage());
                        }
                    }
                }

                LOGGER.info("Remote profile sync completed. Updated {} profiles.", updatedCount);
                return true;
            } catch (Exception e) {
                LOGGER.warn("Remote profile sync failed (offline or network error): {}. Continuing with local cache.", e.getMessage());
                return false;
            }
        }, EXECUTOR);
    }

    private static String fetchUrl(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(urlString).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "CatPay-Fabric-Mod");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                LOGGER.debug("HTTP GET {} returned status {}", urlString, responseCode);
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, read);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            LOGGER.debug("Error fetching URL {}: {}", urlString, e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
