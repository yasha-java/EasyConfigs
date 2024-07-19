package org.codec58.configs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.config.PluginConfig;
import org.codec58.configs.registry.Registry;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class ConfigEdit implements CommandExecutor {
    private final Registry registry;

    public ConfigEdit(Registry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender commandSender,
                             Command command,
                             String s,
                             String[] args) {
        if (!commandSender.hasPermission(Objects.requireNonNull(command.getPermission())))
            return true;

        if (args.length < 2) {
            commandSender.sendMessage(PluginConfig.MORE_ARGUMENTS);
            return true;
        }

        String pluginName = args[0];
        String variableName = args[1];
        String value = args[2];

        ArrayList<String> pluginsString = new ArrayList<>();
        registry.getPlugins().forEach(p -> pluginsString.add(p.getName()));
        Set<String> vars = registry.getCompiledTabComplete().get(args[0]).keySet();

        if (!pluginsString.contains(pluginName)) {
            commandSender.sendMessage(PluginConfig.INVALID_PLUGIN.formatted(pluginName));
            return true;
        }

        if (!vars.contains(variableName)) {
            commandSender.sendMessage(PluginConfig.INVALID_FIELD.formatted(variableName));
            return true;
        }

        Object valueCompiled;

        if (isBoolean(value)) {
            valueCompiled = Boolean.valueOf(value);
        } else if (isDouble(value)) {
            valueCompiled = Double.parseDouble(value);
        } else {
            valueCompiled = value.replace("_", " ");
        }

        try {
            registry.getCompiledTabComplete().get(pluginName).get(variableName).set(null, valueCompiled);
            commandSender.sendMessage(PluginConfig.SAVE_NOTIFY);
        } catch (Throwable err) {
            commandSender.sendMessage(PluginConfig.SET_ERROR.formatted(valueCompiled, variableName));
            err.printStackTrace(System.err);
            return true;
        }

        return true;
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isBoolean(String s) {
        return s.equals("true") || s.equals("false");
    }
}
