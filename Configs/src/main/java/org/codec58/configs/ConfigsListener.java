package org.codec58.configs;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.codec58.configs.config.DebugConfig;

import java.util.Objects;

public class ConfigsListener implements Listener {
    @EventHandler
    public void onPlugmanReload(PlayerCommandPreprocessEvent evt) {
        if (!DebugConfig.ACCESS_RELOAD) return;

        String cmd = evt.getMessage();
        if (cmd.contains("/plugman") && cmd.contains("Configs") && (cmd.contains("unload") || cmd.contains("reload"))) {
            String perm = Objects.requireNonNull(Bukkit.getPluginCommand("plugman")).getPermission();
            if (perm == null) {
                evt.setCancelled(true); //cry
            } else {
                if (evt.getPlayer().hasPermission(perm) || evt.getPlayer().isOp()) {
                    evt.getPlayer().sendMessage("You can't reload this plugin. This is fatal error by you");
                    evt.setCancelled(true);
                }
            }
        }
    }
}
