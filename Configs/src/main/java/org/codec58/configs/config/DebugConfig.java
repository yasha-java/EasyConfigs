package org.codec58.configs.config;

import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;

@Config(name = "Debug")
public class DebugConfig {
    @ConfigVariable(name = "plugin.plugman_x.access_reload")
    public static Boolean ACCESS_RELOAD = true;
}
