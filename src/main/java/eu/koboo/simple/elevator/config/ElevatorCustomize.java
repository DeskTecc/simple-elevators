package eu.koboo.simple.elevator.config;

import eu.koboo.simple.elevator.listener.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

import static org.bukkit.Bukkit.getLogger;

public class ElevatorCustomize implements CommandExecutor {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("SimpleElevator");

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (sender instanceof Player player) {
            Object F1X = config.get("settings.elevators.owners." + player.getDisplayName() + ".X1");
            Object F1Y = config.get("settings.elevators.owners." + player.getDisplayName() + ".Y1");
            Object F1Z = config.get("settings.elevators.owners." + player.getDisplayName() + ".Z1");
            Object prev_block_1 = config.get("settings.elevators.owners." + player.getDisplayName() + ".prev-block-1");
            Object F2X = config.get("settings.elevators.owners." + player.getDisplayName() + ".X2");
            Object F2Y = config.get("settings.elevators.owners." + player.getDisplayName() + ".Y2");
            Object F2Z = config.get("settings.elevators.owners." + player.getDisplayName() + ".Z2");
            Object prev_block_2 = config.get("settings.elevators.owners." + player.getDisplayName() + ".prev-block-2");
            if (config.contains("settings.elevators.owners." + player.getDisplayName() + ".X1") && config.contains("settings.elevators.owners." + player.getDisplayName() + ".X2")) {
                config.createSection("settings.elevators."+ args[0]);
                config.createSection("settings.elevators."+args[0]+".owner");
                config.createSection("settings.elevators."+args[0]+".1F");
                config.createSection("settings.elevators."+args[0]+".1F.X");
                config.createSection("settings.elevators."+args[0]+".1F.Y");
                config.createSection("settings.elevators."+args[0]+".1F.Z");
                config.createSection("settings.elevators."+args[0]+".1F.prev-block");
                config.createSection("settings.elevators."+args[0]+".1F.sound");
                config.createSection("settings.elevators."+args[0]+".1F.text");
                config.createSection("settings.elevators."+args[0]+".2F.X");
                config.createSection("settings.elevators."+args[0]+".2F.Y");
                config.createSection("settings.elevators."+args[0]+".2F.Z");
                config.createSection("settings.elevators."+args[0]+".2F.prev-block");
                config.createSection("settings.elevators."+args[0]+".2F.sound");
                config.createSection("settings.elevators."+args[0]+".2F.text");
                config.set("settings.elevators."+args[0]+".owner",player.getDisplayName());
                config.set("settings.elevators."+args[0]+".1F.X",F1X);
                config.set("settings.elevators."+args[0]+".1F.Y",F1Y);
                config.set("settings.elevators."+args[0]+".1F.Z",F1Z);
                config.set("settings.elevators."+args[0]+".1F.prev-block",prev_block_1);
                config.set("settings.elevators."+args[0]+".1F.sound", Sound.ENTITY_BAT_TAKEOFF);
                config.set("settings.elevators."+args[0]+".2F.X", F2X);
                config.set("settings.elevators."+args[0]+".2F.Y", F2Y);
                config.set("settings.elevators."+args[0]+".2F.Z", F2Z);
                config.set("settings.elevators."+args[0]+".2F.prev-block",prev_block_2);
                config.set("settings.elevators."+args[0]+".2F.sound", Sound.ENTITY_BAT_TAKEOFF);
                WandListener.resetBlocks(player, "all");
            }
        }
        else{
            getLogger().info("You must to be a player to do this!");
        }
        return true;
    }
}
