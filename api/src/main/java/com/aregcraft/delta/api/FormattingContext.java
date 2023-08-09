package com.aregcraft.delta.api;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class FormattingContext {
    public static final FormattingContext DEFAULT = FormattingContext.builder().build();
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(#[a-fA-F0-9]{6}|[a-zA-Z_]+)%");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("@([a-zA-Z0-9_]+)@");
    private static final Pattern UNFORMAT_CHAR_PATTERN =
            Pattern.compile(ChatColor.COLOR_CHAR + "([a-fk-or0-9])");
    private static final Pattern UNFORMAT_HEX_PATTERN =
            Pattern.compile(ChatColor.COLOR_CHAR + "x((" + ChatColor.COLOR_CHAR + "[A-F0-9]){6})");

    private final Map<String, Object> placeholders;
    private final Map<Class<?>, Function<Object, Object>> formatters;
    private final DeltaPlugin plugin;

    private FormattingContext(Map<String, Object> placeholders, Map<Class<?>, Function<Object, Object>> formatters,
                              DeltaPlugin plugin) {
        this.placeholders = placeholders;
        this.formatters = formatters;
        this.plugin = plugin;
    }

    public static FormattingContext withPlugin(DeltaPlugin plugin) {
        return builder().plugin(plugin).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> formatMultiline(String string) {
        return List.of(format(string).split("\n"));
    }

    public String format(String string) {
        return FORMAT_PATTERN.matcher(plugin != null ? localize(string) : string).replaceAll(it -> {
            var group = it.group(1);
            try {
                return ChatColor.of(group).toString();
            } catch (IllegalArgumentException e) {
                return formatPlaceholder(group);
            }
        });
    }

    private String localize(String string) {
        return LANGUAGE_PATTERN.matcher(string).replaceAll(it -> plugin.getLanguage().getLocalized(it.group(1)));
    }

    public String unformat(String string) {
        return unformatChar(unformatHex(string));
    }

    public boolean isDisplayable(String string) {
        var matcher = FORMAT_PATTERN.matcher(string);
        while (matcher.find()) {
            var group = matcher.group(1);
            if (!isColor(group) && !placeholders.containsKey(group)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColor(String string) {
        try {
            ChatColor.of(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private String unformatChar(String string) {
        return UNFORMAT_CHAR_PATTERN.matcher(string)
                .replaceAll(it -> "%" + ChatColor.getByChar(it.group(1).charAt(0)).getName() + "%");
    }

    private String unformatHex(String string) {
        return UNFORMAT_HEX_PATTERN.matcher(string)
                .replaceAll(it -> "%#" + stripColorChars(it.group(1)).toLowerCase() + "%");
    }

    private String stripColorChars(String string) {
        return string.replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "");
    }

    private String formatPlaceholder(String key) {
        var value = placeholders.get(key);
        if (value == null) {
            return "%" + key + "%";
        }
        return format(getFormatter(value.getClass()).apply(value).toString());
    }

    private Function<Object, Object> getFormatter(Class<?> key) {
        return formatters.getOrDefault(key, Function.identity());
    }

    public static class Builder {
        private final Map<String, Object> placeholders = new HashMap<>();
        private final Map<Class<?>, Function<Object, Object>> formatters = new HashMap<>();
        private DeltaPlugin plugin;

        private Builder() {
        }

        public Builder placeholder(String key, List<String> value) {
            if (value != null) {
                return placeholder(key, String.join("\n", value));
            }
            return this;
        }

        public Builder placeholder(String key, Object value) {
            if (value != null) {
                placeholders.put(key, value);
            }
            return this;
        }

        public Builder formatter(Class<?> key, Function<Object, Object> value) {
            formatters.put(key, value);
            return this;
        }

        public Builder plugin(DeltaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public FormattingContext build() {
            return new FormattingContext(placeholders, formatters, plugin);
        }
    }
}
