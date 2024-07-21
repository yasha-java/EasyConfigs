package org.codec58.configs.commands.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.registry.UpdatedRegistry;

public class ConfigReload implements CommandExecutor {
    private final UpdatedRegistry registry;

    public ConfigReload(UpdatedRegistry registry) {
        this.registry = registry;
    }

    //todo
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("Reloading...");
        registry.reloadAll();
        commandSender.sendMessage("Reloaded!");
        return true;
    }
}
