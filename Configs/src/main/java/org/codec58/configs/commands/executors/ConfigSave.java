package org.codec58.configs.commands.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.codec58.configs.registry.UpdatedRegistry;

import java.util.Objects;

public class ConfigSave implements CommandExecutor {
    private final UpdatedRegistry registry;

    public  ConfigSave(UpdatedRegistry registry) {
        this.registry = registry;
    }

    //todo
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission(Objects.requireNonNull(command.getPermission())))
            registry.saveAll();
        return true;
    }
}
