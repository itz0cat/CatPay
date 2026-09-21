package com.itz0cat.catpay.command;

import com.itz0cat.catpay.config.CatPayConfig;
import com.itz0cat.catpay.profile.PaymentProfile;
import com.itz0cat.catpay.profile.ProfileManager;
import com.itz0cat.catpay.util.MessageRenderer;
import com.itz0cat.catpay.util.PlayerResolver;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public class PayCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("pay")
                .executes(context -> {
                    CatPayConfig config = CatPayConfig.get();
                    if (!config.enabled || !config.enableFakePay) {
                        forwardToServer("pay");
                        return 1;
                    }
                    context.getSource().sendFeedback(Text.literal("§cUsage: /pay <player> <amount>"));
                    return 1;
                })
                .then(ClientCommandManager.argument("target", StringArgumentType.word())
                        .suggests(PlayerResolver.getSuggestionProvider())
                        .executes(context -> {
                            CatPayConfig config = CatPayConfig.get();
                            String target = StringArgumentType.getString(context, "target");
                            if (!config.enabled || !config.enableFakePay) {
                                forwardToServer("pay " + target);
                                return 1;
                            }
                            context.getSource().sendFeedback(Text.literal("§cUsage: /pay <player> <amount>"));
                            return 1;
                        })
                        .then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String target = StringArgumentType.getString(context, "target");
                                    String rawAmount = StringArgumentType.getString(context, "amount");
                                    return executePay(context.getSource(), target, rawAmount);
                                })
                        )
                )
        );
    }

    private static int executePay(FabricClientCommandSource source, String target, String rawAmount) {
        CatPayConfig config = CatPayConfig.get();

        // If CatPay or Fake /pay is disabled, forward transparently to the server
        if (!config.enabled || !config.enableFakePay) {
            forwardToServer("pay " + target + " " + rawAmount);
            return 1;
        }

        // Validate and parse amount
        long amount;
        try {
            amount = AmountParser.parse(rawAmount);
        } catch (IllegalArgumentException e) {
            source.sendFeedback(Text.literal("§cCatPay: " + e.getMessage()));
            return 0;
        }

        // Check configured maximum limit
        if (amount > config.maximumFakePayment) {
            source.sendFeedback(Text.literal("§cCatPay: Amount exceeds your configured maximum ($"
                    + AmountParser.format(config.maximumFakePayment) + ")."));
            return 0;
        }

        String formattedAmount = AmountParser.format(amount);
        PaymentProfile profile = ProfileManager.get().getActiveProfile();
        Text message = MessageRenderer.renderSent(profile, target, formattedAmount);

        // Display locally in client chat HUD
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
            client.inGameHud.getChatHud().addMessage(message);
        }

        return 1;
    }

    private static void forwardToServer(String commandLine) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler != null) {
            handler.sendChatCommand(commandLine);
        }
    }
}
