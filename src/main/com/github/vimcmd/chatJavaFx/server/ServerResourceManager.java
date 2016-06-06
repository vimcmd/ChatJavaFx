package com.github.vimcmd.chatJavaFx.server;

import java.util.Locale;
import java.util.ResourceBundle;

public class ServerResourceManager {
    private static final ResourceBundle res = ResourceBundle.getBundle("com.github.vimcmd.chatJavaFx.resources.serverResources", Locale
            .getDefault());
    public static final String RECIPIENT_CHARACTER = res.getString("server.commands.recipientCharacter");
    public static final String SERVER_COMMAND_ARGUMENT_SEPARATOR = res.getString("server.commands.argument.separator");
    public static final String SERVER_COMMAND_CHARACTER = res.getString("server.commands.commandCharacter");
    public static final String SERVER_COMMAND_HELP = res.getString("server.commands.help");
    public static final String SERVER_COMMAND_REGISTER = res.getString("server.commands.register");
    public static final String SERVER_COMMAND_TIME = res.getString("server.commands.time");
    public static final String SERVER_COMMAND_UNKNOWN = res.getString("server.commands.unknown");
    public static final String SERVER_COMMAND_UNKNOWN_MESSAGE = res.getString("server.commands.unknown.message");
    public static final String SERVER_STATUS_ADDRESS_RESERVATION_ERROR = res.getString("server.status.error.address.reserveError");
    public static final String SERVER_STATUS_INITIALIZED = res.getString("server.status.initialized");
    public static final String SERVER_STATUS_WAITING_CLIENTS = res.getString("server.status.waiting");
    public static final String SERVER_TIPS_PRIVATE = res.getString("server.tips.privateMessage");
    public static final String SERVER_USER_CONNECTED = res.getString("server.user.status.connected");
    public static final String SERVER_USER_DISCONNECTED = res.getString("server.user.disconnected");
    public static final String SERVER_USER_EXISTS = res.getString("server.user.name.exists");
    public static final String SERVER_USER_MUST_REGISTER = res.getString("server.user.mustRegisterFirst");
    public static final String SERVER_USER_NAME_INCORRECT = res.getString("server.user.name.incorrect");
    public static final String SERVER_USER_NOT_REGISTERED = res.getString("server.user.notRegistered");
    public static final String SERVER_USER_SUCCESSFULLY_REGISTERED = res.getString("server.user.registered");
    public static final String SERVER_USER_WELCOME_MESSAGE = res.getString("server.user.welcomeMessage");

    private ServerResourceManager() {
    }
}
