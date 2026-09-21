package com.itz0cat.catpay;

import com.itz0cat.catpay.command.PayCommand;
import com.itz0cat.catpay.config.CatPayConfig;
import com.itz0cat.catpay.keybind.KeybindManager;
import com.itz0cat.catpay.profile.ProfileManager;
import com.itz0cat.catpay.profile.RemoteProfileSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatPayClient implements ClientModInitializer {
    public static final String MOD_ID = "catpay";
    public static final Logger LOGGER = LoggerFactory.getLogger("CatPay");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing CatPay client mod...");

        // 1. Initialize configuration
        CatPayConfig.get();

        // 2. Initialize local profiles
        ProfileManager.get().init();

        // 3. Register keybindings
        KeybindManager.init();

        // 4. Register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            PayCommand.register(dispatcher);
        });

        // 5. Hook server join for profile auto-selection
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerInfo serverEntry = client.getCurrentServerEntry();
            if (serverEntry != null) {
                ProfileManager.get().selectProfileByServer(serverEntry.address);
            }
        });

        // 6. Asynchronously check for remote profile updates if enabled
        if (CatPayConfig.get().autoUpdateProfiles) {
            RemoteProfileSync.syncProfilesAsync();
        }

        LOGGER.info("CatPay client mod initialized successfully.");
    }
}
