package com.itz0cat.catpay.util;

import com.itz0cat.catpay.config.CatPayConfig;
import com.itz0cat.catpay.profile.PaymentProfile;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRenderer {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)[&§]([0-9a-fk-or])");

    public static Text renderSent(PaymentProfile profile, String targetUser, String formattedAmount) {
        String template = profile.getPayment().getSent();
        return render(template, profile, targetUser, formattedAmount);
    }

    public static Text renderReceived(PaymentProfile profile, String senderUser, String formattedAmount) {
        String template = profile.getPayment().getReceived();
        return render(template, profile, senderUser, formattedAmount);
    }

    public static Text render(String template, PaymentProfile profile, String user, String formattedAmount) {
        CatPayConfig config = CatPayConfig.get();
        String iconReplacement = "";

        if (config.useCustomIcon && profile.getIcon().isEnabled()) {
            iconReplacement = profile.getIcon().getGlyph();
        }

        String processed = template
                .replace("{icon}", iconReplacement)
                .replace("{user}", user)
                .replace("{amount}", formattedAmount);

        // Clean up double spaces if icon is empty
        if (iconReplacement.isEmpty()) {
            processed = processed.replace("  ", " ").trim();
        }

        return parseFormattedText(processed);
    }

    /**
     * Converts Minecraft & or § color and formatting codes into styled Text components.
     */
    public static Text parseFormattedText(String text) {
        if (text == null || text.isEmpty()) {
            return Text.empty();
        }

        MutableText root = Text.empty();
        Matcher matcher = FORMAT_PATTERN.matcher(text);

        int lastIndex = 0;
        Formatting currentColor = null;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strikethrough = false;
        boolean obfuscated = false;

        while (matcher.find()) {
            int start = matcher.start();
            if (start > lastIndex) {
                String segment = text.substring(lastIndex, start);
                root.append(createSegment(segment, currentColor, bold, italic, underline, strikethrough, obfuscated));
            }

            char code = matcher.group(1).toLowerCase().charAt(0);
            Formatting formatting = Formatting.byCode(code);
            if (formatting != null) {
                if (formatting.isColor()) {
                    currentColor = formatting;
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    obfuscated = false;
                } else if (formatting == Formatting.RESET) {
                    currentColor = null;
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    obfuscated = false;
                } else {
                    switch (formatting) {
                        case BOLD -> bold = true;
                        case ITALIC -> italic = true;
                        case UNDERLINE -> underline = true;
                        case STRIKETHROUGH -> strikethrough = true;
                        case OBFUSCATED -> obfuscated = true;
                    }
                }
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            String segment = text.substring(lastIndex);
            root.append(createSegment(segment, currentColor, bold, italic, underline, strikethrough, obfuscated));
        }

        return root;
    }

    private static MutableText createSegment(String text, Formatting color, boolean bold, boolean italic,
                                             boolean underline, boolean strikethrough, boolean obfuscated) {
        MutableText segment = Text.literal(text);
        segment.styled(style -> {
            if (color != null) style = style.withColor(color);
            if (bold) style = style.withBold(true);
            if (italic) style = style.withItalic(true);
            if (underline) style = style.withUnderline(true);
            if (strikethrough) style = style.withStrikethrough(true);
            if (obfuscated) style = style.withObfuscated(true);
            return style;
        });
        return segment;
    }
}
