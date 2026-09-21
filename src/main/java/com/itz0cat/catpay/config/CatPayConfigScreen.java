package com.itz0cat.catpay.config;

import com.itz0cat.catpay.profile.PaymentProfile;
import com.itz0cat.catpay.profile.ProfileManager;
import com.itz0cat.catpay.profile.RemoteProfileSync;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class CatPayConfigScreen {

    public static Screen create(Screen parent) {
        CatPayConfig config = CatPayConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.catpay.config"));

        ConfigEntryBuilder entryBuilder = builder.getEntryBuilder();

        // 1. General Category
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.catpay.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.enabled"), config.enabled)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.catpay.enabled"))
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.enable_fake_pay"), config.enableFakePay)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableFakePay = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.enable_fake_receive"), config.enableFakeReceive)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableFakeReceive = newValue)
                .build());

        List<String> profileIds = ProfileManager.get().getAllProfiles().stream()
                .map(PaymentProfile::getId)
                .toList();

        general.addEntry(entryBuilder.startStringDropdownMenu(Text.translatable("option.catpay.selected_profile"), config.selectedProfile)
                .setDefaultValue("bananasmp")
                .setSelections(profileIds)
                .setSaveConsumer(newValue -> config.selectedProfile = newValue)
                .build());

        // 2. Payment Category
        ConfigCategory payment = builder.getOrCreateCategory(Text.translatable("category.catpay.payment"));

        payment.addEntry(entryBuilder.startLongField(Text.translatable("option.catpay.maximum_fake_payment"), config.maximumFakePayment)
                .setDefaultValue(1_000_000_000_000L)
                .setMin(1L)
                .setSaveConsumer(newValue -> config.maximumFakePayment = newValue)
                .build());

        payment.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.use_custom_icon"), config.useCustomIcon)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.catpay.use_custom_icon"))
                .setSaveConsumer(newValue -> config.useCustomIcon = newValue)
                .build());

        // 3. Receive Category
        ConfigCategory receive = builder.getOrCreateCategory(Text.translatable("category.catpay.receive"));

        receive.addEntry(entryBuilder.startStrField(Text.translatable("option.catpay.receive_username"), config.receiveUsername)
                .setDefaultValue("JustGhastlyyy")
                .setSaveConsumer(newValue -> config.receiveUsername = newValue)
                .build());

        receive.addEntry(entryBuilder.startStrField(Text.translatable("option.catpay.receive_amount"), config.receiveAmount)
                .setDefaultValue("500,000,000")
                .setSaveConsumer(newValue -> config.receiveAmount = newValue)
                .build());

        // 4. Profiles Category
        ConfigCategory profiles = builder.getOrCreateCategory(Text.translatable("category.catpay.profiles"));

        profiles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.auto_update_profiles"), config.autoUpdateProfiles)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.autoUpdateProfiles = newValue)
                .build());

        profiles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.catpay.auto_select_profile_by_server"), config.autoSelectProfileByServer)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.autoSelectProfileByServer = newValue)
                .build());

        profiles.addEntry(entryBuilder.startStrField(Text.translatable("option.catpay.github_repo"), config.gitHubRepo)
                .setDefaultValue("itz0cat/CatPay")
                .setSaveConsumer(newValue -> config.gitHubRepo = newValue)
                .build());

        builder.setSavingRunnable(config::save);
        return builder.build();
    }
}
