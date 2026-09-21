package com.itz0cat.catpay.util;

import com.itz0cat.catpay.config.CatPayConfig;
import com.itz0cat.catpay.profile.PaymentProfile;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRenderer {
    // Matches MiniMessage tags (<#RRGGBB>, <color:#RRGGBB>, <tag>), Spigot hex (&#RRGGBB, §#RRGGBB), and classic codes (&a, §a)
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "<#([0-9a-fA-F]{6})>|" +
            "<color:#([0-9a-fA-F]{6})>|" +
            "<([a-zA-Z_]+)>|" +
            "(?:&|§)#([0-9a-fA-F]{6})|" +
            "(?:&|§)([0-9a-fk-orA-FK-OR])"
    );

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

        if (iconReplacement.isEmpty()) {
            processed = processed.replace("  ", " ").trim();
        }

        return parseFormattedText(processed);
    }

    /**
     * Parses formatted text supporting MiniMessage tags, hex colors (&#RRGGBB / <#RRGGBB>),
     * and classic & / § codes into styled Minecraft Text components.
     */
    public static Text parseFormattedText(String text) {
        if (text == null || text.isEmpty()) {
            return Text.empty();
        }

        MutableText root = Text.empty();
        Matcher matcher = TOKEN_PATTERN.matcher(text);

        int lastIndex = 0;
        TextColor currentColor = null;
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

            String mmHex1 = matcher.group(1);
            String mmHex2 = matcher.group(2);
            String mmTag = matcher.group(3);
            String spigotHex = matcher.group(4);
            String classicCode = matcher.group(5);

            if (mmHex1 != null) {
                currentColor = TextColor.fromRgb(Integer.parseInt(mmHex1, 16));
            } else if (mmHex2 != null) {
                currentColor = TextColor.fromRgb(Integer.parseInt(mmHex2, 16));
            } else if (spigotHex != null) {
                currentColor = TextColor.fromRgb(Integer.parseInt(spigotHex, 16));
            } else if (classicCode != null) {
                char code = Character.toLowerCase(classicCode.charAt(0));
                Formatting f = Formatting.byCode(code);
                if (f != null) {
                    if (f.isColor()) {
                        currentColor = TextColor.fromFormatting(f);
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        obfuscated = false;
                    } else if (f == Formatting.RESET) {
                        currentColor = null;
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        obfuscated = false;
                    } else {
                        switch (f) {
                            case BOLD -> bold = true;
                            case ITALIC -> italic = true;
                            case UNDERLINE -> underline = true;
                            case STRIKETHROUGH -> strikethrough = true;
                            case OBFUSCATED -> obfuscated = true;
                        }
                    }
                }
            } else if (mmTag != null) {
                String tag = mmTag.toLowerCase();
                switch (tag) {
                    case "green" -> currentColor = TextColor.fromFormatting(Formatting.GREEN);
                    case "dark_green" -> currentColor = TextColor.fromFormatting(Formatting.DARK_GREEN);
                    case "aqua" -> currentColor = TextColor.fromFormatting(Formatting.AQUA);
                    case "dark_aqua" -> currentColor = TextColor.fromFormatting(Formatting.DARK_AQUA);
                    case "gray", "grey" -> currentColor = TextColor.fromFormatting(Formatting.GRAY);
                    case "dark_gray", "dark_grey" -> currentColor = TextColor.fromFormatting(Formatting.DARK_GRAY);
                    case "white" -> currentColor = TextColor.fromFormatting(Formatting.WHITE);
                    case "yellow" -> currentColor = TextColor.fromFormatting(Formatting.YELLOW);
                    case "gold" -> currentColor = TextColor.fromFormatting(Formatting.GOLD);
                    case "red" -> currentColor = TextColor.fromFormatting(Formatting.RED);
                    case "dark_red" -> currentColor = TextColor.fromFormatting(Formatting.DARK_RED);
                    case "blue" -> currentColor = TextColor.fromFormatting(Formatting.BLUE);
                    case "dark_blue" -> currentColor = TextColor.fromFormatting(Formatting.DARK_BLUE);
                    case "light_purple", "pink" -> currentColor = TextColor.fromFormatting(Formatting.LIGHT_PURPLE);
                    case "dark_purple", "purple" -> currentColor = TextColor.fromFormatting(Formatting.DARK_PURPLE);
                    case "black" -> currentColor = TextColor.fromFormatting(Formatting.BLACK);
                    case "bold", "b" -> bold = true;
                    case "italic", "em", "i" -> italic = true;
                    case "underlined", "u" -> underline = true;
                    case "strikethrough", "st" -> strikethrough = true;
                    case "obfuscated", "obf" -> obfuscated = true;
                    case "reset", "r" -> {
                        currentColor = null;
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        obfuscated = false;
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

    private static MutableText createSegment(String text, TextColor color, boolean bold, boolean italic,
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
