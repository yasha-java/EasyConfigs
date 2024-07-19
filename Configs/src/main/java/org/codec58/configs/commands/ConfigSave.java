package org.codec58.configs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.registry.Registry;

import java.util.Objects;

public class ConfigSave implements CommandExecutor {
    private final Registry registry;

    public ConfigSave(Registry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission(Objects.requireNonNull(command.getPermission())))
            registry.save();
        return true;
    }
}
