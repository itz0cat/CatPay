package com.itz0cat.catpay.keybind;

import com.itz0cat.catpay.command.AmountParser;
import com.itz0cat.catpay.config.CatPayConfig;
import com.itz0cat.catpay.profile.PaymentProfile;
import com.itz0cat.catpay.profile.ProfileManager;
import com.itz0cat.catpay.util.MessageRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    private static KeyBinding toggleKeyBinding;
    private static KeyBinding receiveKeyBinding;

    public static void init() {
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.catpay.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.catpay"
        ));

        receiveKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.catpay.receive",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.catpay"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKeyBinding.wasPressed()) {
                handleToggle();
            }

            while (receiveKeyBinding.wasPressed()) {
                handleReceive(client);
            }
        });
    }

    private static void handleToggle() {
        CatPayConfig config = CatPayConfig.get();
        config.enabled = !config.enabled;
        config.save();
        // Completely silent toggle - no chat or HUD feedback per user specification
    }

    private static void handleReceive(MinecraftClient client) {
        CatPayConfig config = CatPayConfig.get();
        if (!config.enabled || !config.enableFakeReceive) {
            return;
        }

        String username = config.receiveUsername;
        String rawAmount = config.receiveAmount;

        if (username == null || username.trim().isEmpty()) {
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§cCatPay: No receive username configured in Cloth Config."), false);
            }
            return;
        }

        long amount;
        try {
            amount = AmountParser.parse(rawAmount);
        } catch (IllegalArgumentException e) {
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§cCatPay receive error: " + e.getMessage()), false);
            }
            return;
        }

        String formattedAmount = AmountParser.format(amount);
        PaymentProfile profile = ProfileManager.get().getActiveProfile();
        Text message = MessageRenderer.renderReceived(profile, username, formattedAmount);

        if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
            client.inGameHud.getChatHud().addMessage(message);
        }
    }
}
