package org.codec58.configs.config;

import org.codec58.easyconfigsapi.ConfigVariable;
import org.codec58.easyconfigsapi.Config;

@Config(name = "Chat")
public class PluginConfig {
    @ConfigVariable(name = "chat.error.invalid_plugin")
    public static String INVALID_PLUGIN = "Invalid plugin '%s'";
    @ConfigVariable(name = "chat.error.invalid_field")
    public static String INVALID_FIELD = "Invalid field '%s'";
    @ConfigVariable(name = "chat.error.invalid_field")
    public static String INVALID_CONFIG = "Invalid config '%s'";
    @ConfigVariable(name = "cmd.error.completer_error")
    public static String TAB_COMPLETE_ERROR = "Error while creating tab complete list.";
    @ConfigVariable(name = "chat.error.set_value")
    public static String SET_ERROR = "Error while setting value '%s' to variable '%s'. See console for more information";
    @ConfigVariable(name = "chat.error.more_arguments")
    public static String MORE_ARGUMENTS = "More arguments are needed.";
    @ConfigVariable(name = "chat.error.plugin_deactivated")
    public static String PLUGIN_DEACTIVATED_IN_PROCESS = "Plugin %s deactivated in server process.";

    @ConfigVariable(name = "chat.completer.current_value")
    public static String FIELD_SET_VALUE = "Current value: '%s'";

    @ConfigVariable(name = "chat.runtime.nothing_more")
    public static String NOTHING_MORE = "Nothing more.";

    @ConfigVariable(name = "chat.notify.save_notify")
    public static String SAVE_NOTIFY = "Success! Don't forget to save configs with the /configSave command.";
}
