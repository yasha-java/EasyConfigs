package org.codec58.configs.config;

import org.codec58.easyconfigsapi.ConfigVariable;
import org.codec58.easyconfigsapi.Config;

@Config(name = "config")
public class PluginConfig {
    @ConfigVariable(name = "cmd.invalid.plugin")
    public static String INVALID_PLUGIN = "Invalid plugin '%s'";
    @ConfigVariable(name = "cmd.invalid.field")
    public static String INVALID_FIELD = "Invalid field '%s'";
    @ConfigVariable(name = "cmd.set.value")
    public static String FIELD_SET_VALUE = "Current value: '%s'";
    @ConfigVariable(name = "cmd.nothing.more")
    public static String NOTHING_MORE = "Nothing more.";
    @ConfigVariable(name = "cmd.tab.complete.error")
    public static String TAB_COMPLETE_ERROR = "Error while creating tab complete list.";
    @ConfigVariable(name = "cmd.set.value.error")
    public static String SET_ERROR = "Error while setting value '%s' to variable '%s'. See console for more information";
    @ConfigVariable(name = "cmd.need.more.arguments")
    public static String MORE_ARGUMENTS = "More arguments are needed.";
    @ConfigVariable(name = "cmd.save.notify")
    public static String SAVE_NOTIFY = "Success! Don't forget to save configs with the /configSave command.";
}
