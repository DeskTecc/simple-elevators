package eu.koboo.simple.elevator.config;

import eu.koboo.simple.elevator.listener.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


import static org.bukkit.Bukkit.getLogger;

public class ElevatorCustomize implements CommandExecutor {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("SimpleElevator");

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (sender instanceof Player player) {
            Object[] Floor1 = {config.get("settings.elevators.owners." + player.getDisplayName() + ".X1"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".Y1"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".Z1"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".prev-block-1"),
                    Sound.ENTITY_BAT_TAKEOFF,
            "null"};
            Object[] Floor2 = {config.get("settings.elevators.owners." + player.getDisplayName() + ".X2"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".Y2"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".Z2"),
                    config.get("settings.elevators.owners." + player.getDisplayName() + ".prev-block-2"),
                    Sound.ENTITY_BAT_TAKEOFF,
                    "null"};
            if (config.contains("settings.elevators.owners." + player.getDisplayName() + ".X1") && config.contains("settings.elevators.owners." + player.getDisplayName() + ".X2")) {
                String[][] elevator = {{"owner","1F","2F"},{"X","Y","Z","prev-block","sound","text"}};
                for(int firstArray=0; firstArray<elevator[0].length;firstArray++){
                    for(int secondArray=0; secondArray<elevator[1].length;secondArray++){
                        if(firstArray==0){
                            config.createSection("settings.elevators."+ args[0]);
                            config.createSection("settings.elevators."+args[0]+"."+elevator[firstArray][0]);
                            config.set("settings.elevators."+args[0]+"."+elevator[firstArray][0],player.getDisplayName());
                        }
                        else if(firstArray==1){
                            config.createSection("settings.elevators." + args[0] + "." + elevator[0][firstArray]);
                            config.createSection("settings.elevators." + args[0] + "." + elevator[0][firstArray] + "." + elevator[firstArray][secondArray]);
                            config.set("settings.elevators." + args[0] +"."+elevator[0][firstArray]+"."+elevator[firstArray][secondArray], Floor1[secondArray]);
                        }
                        else{
                            config.createSection("settings.elevators." + args[0] + "." + elevator[0][firstArray]);
                            config.createSection("settings.elevators." + args[0] + "." + elevator[0][firstArray] + "." + elevator[firstArray][secondArray]);
                            config.set("settings.elevators." + args[0] +"."+elevator[0][firstArray]+"."+elevator[firstArray][secondArray], Floor2[secondArray]);
                        }

                    }
                }
                WandListener.resetBlocks(player, "all");
            }
        }
        else{
            getLogger().info("You must to be a player to do this!");
        }
        return true;
    }
}
