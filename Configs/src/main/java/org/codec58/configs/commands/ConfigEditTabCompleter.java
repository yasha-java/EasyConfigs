package org.codec58.configs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.codec58.configs.config.PluginConfig;
import org.codec58.configs.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigEditTabCompleter implements TabCompleter {
    private final Registry registry;

    public ConfigEditTabCompleter(Registry registry) {
        this.registry = registry;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String ignored, String[] args) {
        ArrayList<String> pluginsString = new ArrayList<>();
        registry.getPlugins().forEach(p -> pluginsString.add(p.getName()));

        if (args.length == 1) {
            return pluginsString;
        }

        if (args.length == 2) {
            if (pluginsString.contains(args[0])) {
                return registry.getCompiledTabComplete().get(args[0])
                        .keySet()
                        .stream()
                        .toList();
            } else {
                return List.of(PluginConfig.INVALID_PLUGIN.formatted(args[0]));
            }
        }

        if (args.length == 3) {
            if (pluginsString.contains(args[0])) {
                Set<String> vars = registry.getCompiledTabComplete().get(args[0]).keySet();
                if (vars.contains(args[1])) {
                    try {
                        String currentValue = registry.getCompiledTabComplete().get(args[0]).get(args[1]).get(null).toString();
                        return List.of(PluginConfig.FIELD_SET_VALUE.formatted(currentValue));
                    } catch (IllegalAccessException e) {
                        return List.of(PluginConfig.TAB_COMPLETE_ERROR);
                    }
                } else {
                    return List.of(PluginConfig.INVALID_FIELD.formatted(args[1]));
                }
            } else {
                return List.of(PluginConfig.INVALID_PLUGIN.formatted(args[0]));
            }
        }

        return List.of(PluginConfig.NOTHING_MORE);
    }
}
