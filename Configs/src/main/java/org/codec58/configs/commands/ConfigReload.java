package org.codec58.configs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.registry.Registry;

public class ConfigReload implements CommandExecutor {
    private final Registry registry;

    public ConfigReload(Registry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("Reloading...");
        registry.reload();
        commandSender.sendMessage("Reloaded!");
        return true;
    }
}
