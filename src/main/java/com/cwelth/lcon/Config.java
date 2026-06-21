package com.cwelth.lcon;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String CATEGORY_MAIN = "main";
    public static ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static ForgeConfigSpec.IntValue PORT;
    public static ForgeConfigSpec.ConfigValue<String> TOKEN;
    public static ForgeConfigSpec.IntValue COMMAND_PERMISSION_LEVEL;

    // Message configuration
    public static ForgeConfigSpec.BooleanValue ENABLE_MESSAGE_EMOJI;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_UNAUTHORIZED;
    public static ForgeConfigSpec.ConfigValue<String> MSG_UNAUTHORIZED;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_WELCOME;
    public static ForgeConfigSpec.ConfigValue<String> MSG_WELCOME;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_PREFIXES;
    public static ForgeConfigSpec.ConfigValue<String> MSG_PREFIXES;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_CHAT;
    public static ForgeConfigSpec.ConfigValue<String> MSG_CHAT;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_MESSAGE;
    public static ForgeConfigSpec.ConfigValue<String> MSG_MESSAGE;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_SYSTEM;
    public static ForgeConfigSpec.ConfigValue<String> MSG_SYSTEM;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_CLIENT;
    public static ForgeConfigSpec.ConfigValue<String> MSG_CLIENT;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_SERVER;
    public static ForgeConfigSpec.ConfigValue<String> MSG_SERVER;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_READY;
    public static ForgeConfigSpec.ConfigValue<String> MSG_READY;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_ERROR_PREFIX;
    public static ForgeConfigSpec.ConfigValue<String> MSG_ERROR_PREFIX;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_ERROR_INTERNAL;
    public static ForgeConfigSpec.ConfigValue<String> MSG_ERROR_INTERNAL;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_LOG_START;
    public static ForgeConfigSpec.ConfigValue<String> MSG_LOG_START;

    public static ForgeConfigSpec.ConfigValue<String> EMOJI_LOG_ERROR;
    public static ForgeConfigSpec.ConfigValue<String> MSG_LOG_ERROR;

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CLIENT_CONFIG;

    static {

        // Main Config
        CLIENT_BUILDER.comment("Main config").push(CATEGORY_MAIN);
        ENABLE_MOD = CLIENT_BUILDER.comment("Is the mod available at all?").define("enable_mod", true);
        PORT = CLIENT_BUILDER.comment("Port to listen on. Default 58115.").defineInRange("port", 58115, 1024, 65535);
        TOKEN = CLIENT_BUILDER.comment("Authentication token. Clients must pass ?token=xxx when connecting.").define("token", "your_secret_token");
        COMMAND_PERMISSION_LEVEL = CLIENT_BUILDER.comment("OP level for [server] commands (0-4). Default 4 = full access without enabling cheats.").defineInRange("command_permission_level", 4, 0, 4);
        CLIENT_BUILDER.pop();

        // Message Config
        CLIENT_BUILDER.comment("Message configuration. Set emoji to empty to disable emoji per-message.").push("messages");
        ENABLE_MESSAGE_EMOJI = CLIENT_BUILDER.comment("Master switch for message emoji. If false, no emoji are prepended regardless of per-message emoji settings.").define("enable_message_emoji", true);

        CLIENT_BUILDER.comment("Token / Auth messages").push("auth");
        EMOJI_UNAUTHORIZED = CLIENT_BUILDER.define("emoji_unauthorized", "🔒");
        MSG_UNAUTHORIZED = CLIENT_BUILDER.define("msg_unauthorized", "401:Unauthorized - invalid token.");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Welcome messages on connect").push("welcome");
        EMOJI_WELCOME = CLIENT_BUILDER.define("emoji_welcome", "🎉");
        MSG_WELCOME = CLIENT_BUILDER.define("msg_welcome", "200:Welcome to LCon! Have fun! Don't forget to use prefixes with every message you send to me.");
        EMOJI_PREFIXES = CLIENT_BUILDER.define("emoji_prefixes", "📋");
        MSG_PREFIXES = CLIENT_BUILDER.define("msg_prefixes", "200:Valid prefixes:");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Prefix help lines").push("prefixes");
        EMOJI_CHAT = CLIENT_BUILDER.define("emoji_chat", "💬");
        MSG_CHAT = CLIENT_BUILDER.define("msg_chat", "200:[chat] - send message to Minecraft chat.");
        EMOJI_MESSAGE = CLIENT_BUILDER.define("emoji_message", "📩");
        MSG_MESSAGE = CLIENT_BUILDER.define("msg_message", "200:[message] - display message for player only.");
        EMOJI_SYSTEM = CLIENT_BUILDER.define("emoji_system", "🔔");
        MSG_SYSTEM = CLIENT_BUILDER.define("msg_system", "200:[system] - display system message in chat (for player only).");
        EMOJI_CLIENT = CLIENT_BUILDER.define("emoji_client", "🖥️");
        MSG_CLIENT = CLIENT_BUILDER.define("msg_client", "200:[client] - execute client-side command.");
        EMOJI_SERVER = CLIENT_BUILDER.define("emoji_server", "🖧");
        MSG_SERVER = CLIENT_BUILDER.define("msg_server", "200:[server] - execute server-side command.");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Ready and error messages").push("status");
        EMOJI_READY = CLIENT_BUILDER.define("emoji_ready", "✅");
        MSG_READY = CLIENT_BUILDER.define("msg_ready", "201:ready.");
        EMOJI_ERROR_PREFIX = CLIENT_BUILDER.define("emoji_error_prefix", "❌");
        MSG_ERROR_PREFIX = CLIENT_BUILDER.define("msg_error_prefix", "400:Error! Send message prefix first! [chat], [message], [system], [client], [server] are valid prefixes.");
        EMOJI_ERROR_INTERNAL = CLIENT_BUILDER.define("emoji_error_internal", "💥");
        MSG_ERROR_INTERNAL = CLIENT_BUILDER.define("msg_error_internal", "500:Internal server error - ");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Log messages").push("log");
        EMOJI_LOG_START = CLIENT_BUILDER.define("emoji_log_start", "🚀");
        MSG_LOG_START = CLIENT_BUILDER.define("msg_log_start", "LCon WebSocket server listening on port ");
        EMOJI_LOG_ERROR = CLIENT_BUILDER.define("emoji_log_error", "🔥");
        MSG_LOG_ERROR = CLIENT_BUILDER.define("msg_log_error", "LCon WebSocket error: ");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.pop(); // messages
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .preserveInsertionOrder()
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
