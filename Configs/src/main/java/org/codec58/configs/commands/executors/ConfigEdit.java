package org.codec58.configs.commands.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.Convertor;
import org.codec58.configs.config.PluginConfig;
import org.codec58.configs.registry.UpdatedRegistry;
import org.codec58.configs.utils.ConfigUtils;
import org.codec58.configs.utils.reflect.FieldUtils;
import org.codec58.configs.utils.reflect.exception.ReflectionError;
import org.codec58.configs.utils.reflect.exception.ReflectionNoError;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

public class ConfigEdit implements CommandExecutor {
    private final UpdatedRegistry registry;

    public ConfigEdit(UpdatedRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission(Objects.requireNonNull(command.getPermission())))
            return true;

        if (args.length < 4) {
            commandSender.sendMessage(PluginConfig.MORE_ARGUMENTS);
            return true;
        }

        String pluginName = args[0];
        String configName = args[1];
        String variableName = args[2];
        String value = args[3];
        // -/configedit EasyConfigs Chat chat.error.invalid_plugin New_value

        Set<String> pluginNames = registry.getPluginStrings();

        if (!pluginNames.contains(pluginName)) {
            commandSender.sendMessage(PluginConfig.INVALID_PLUGIN.formatted(pluginName));
            return true;
        }

        Set<Class<?>> configs = registry.getCompiledRegistry().get(pluginName).keySet();
        Class<?> configClazz = null;

        for (Class<?> cfg : configs) {
            if (Objects.equals(ConfigUtils.getConfigName(cfg), configName)) {
                configClazz = cfg;
                break;
            }
        }

        if (configClazz == null) {
            commandSender.sendMessage(PluginConfig.INVALID_CONFIG.formatted(configName));
            return true;
        }

        Set<Field> variables = registry.getCompiledRegistry().get(pluginName).get(configClazz);
        Field variableField = null;

        for (Field variable : variables) {
            if (Objects.equals(ConfigUtils.getConfigVariableName(variable), variableName)) {
                variableField = variable;
                break;
            }
        }

        if (variableField == null) {
            commandSender.sendMessage(PluginConfig.INVALID_FIELD.formatted(variableName));
            return true;
        }

        Object valueCompiled;

        if (Convertor.isBoolean(value)) {
            valueCompiled = Boolean.valueOf(value);
        } else if (Convertor.isNumber(value)) {
            valueCompiled = Convertor.double2NumericType(Double.parseDouble(value), variableField.getType());
        } else {
            valueCompiled = value.replace("_", " ");
        }

        try {
            ReflectionError output = FieldUtils.setStatic(variableField, valueCompiled);
            if (output instanceof ReflectionNoError) {
                commandSender.sendMessage(PluginConfig.SAVE_NOTIFY);
            } else {
                commandSender.sendMessage(PluginConfig.SET_ERROR.formatted(valueCompiled, variableName));
                output.printStackTrace(System.err);
            }
        } catch (Throwable err) {
            commandSender.sendMessage(PluginConfig.SET_ERROR.formatted(valueCompiled, variableName));
            err.printStackTrace(System.err);
        }

        return true;
    }
}
