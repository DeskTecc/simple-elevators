package eu.koboo.simple.elevator.listener;

import eu.koboo.simple.elevator.SimpleElevator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class WandListener implements Listener {

    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("SimpleElevator");
    @EventHandler
    public boolean onPlayerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        assert block != null;
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Maker!")) {
                if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) && event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        onBlockBreak(new BlockBreakEvent(block, player));
                        int F1X = (int) block.getLocation().getX();
                        int F1Y = (int) block.getLocation().getY();
                        int F1Z = (int) block.getLocation().getZ();
                        Location locCheck1 = new Location(player.getWorld(),F1X,F1Y+1,F1Z);
                        Location locCheck2 = new Location(player.getWorld(),F1X,F1Y+2,F1Z);
                        if(locCheck1.getBlock().getType() == Material.AIR && locCheck2.getBlock().getType() == Material.AIR){
                            FileConfiguration config = plugin.getConfig();
                            if(config.contains("settings.elevators.owners."+player.getDisplayName())){
                                        player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+F1X+" "+F1Y+" "+F1Z+" as your 1F.");
                                        config.createSection("settings.elevators.owners."+player.getDisplayName()+".X1");
                                        config.createSection("settings.elevators.owners."+player.getDisplayName()+".Y1");
                                        config.createSection("settings.elevators.owners."+player.getDisplayName()+".Z1");
                                        config.set("settings.elevators.owners."+player.getDisplayName()+".X1", F1X);
                                        config.set("settings.elevators.owners."+player.getDisplayName()+".Y1", F1Y);
                                        config.set("settings.elevators.owners."+player.getDisplayName()+".Z1", F1Z);
                                        if(config.contains("settings.elevators.owners."+player.getDisplayName()+".X2")){
                                            config.set("settings.elevators.owners."+player.getDisplayName()+".X2", null);
                                            config.set("settings.elevators.owners."+player.getDisplayName()+".Y2", null);
                                            config.set("settings.elevators.owners."+player.getDisplayName()+".Z2", null);
                                            player.sendMessage(ChatColor.YELLOW+"You changed the 1F, please select 2F pressing right click.");
                                        }else{
                                            player.sendMessage(ChatColor.YELLOW+"Select 2F pressing right click.");
                                        }
                            }
                            else {
                                player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+F1X+" "+F1Y+" "+F1Z+" as your 1F.");
                                config.createSection("settings.elevators.owners."+player.getDisplayName()+".X1");
                                config.createSection("settings.elevators.owners."+player.getDisplayName()+".Y1");
                                config.createSection("settings.elevators.owners."+player.getDisplayName()+".Z1");
                                config.set("settings.elevators.owners."+player.getDisplayName()+".X1", F1X);
                                config.set("settings.elevators.owners."+player.getDisplayName()+".Y1", F1Y);
                                config.set("settings.elevators.owners."+player.getDisplayName()+".Z1", F1Z);
                            }
                            plugin.saveConfig();
                        }
                        else{
                            player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please check if have blocks above of block you tried to select as 1F.");
                        }
                    }
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
                        int F2X = (int) block.getLocation().getX();
                        int F2Y = (int) block.getLocation().getY();
                        int F2Z = (int) block.getLocation().getZ();
                        Location locCheck1 = new Location(player.getWorld(), F2X, F2Y + 1, F2Z);
                        Location locCheck2 = new Location(player.getWorld(), F2X, F2Y + 2, F2Z);
                        if (locCheck1.getBlock().getType() == Material.AIR && locCheck2.getBlock().getType() == Material.AIR) {
                            FileConfiguration config = plugin.getConfig();
                            if(config.contains("settings.elevators.owners."+player.getDisplayName())){
                                if(config.contains("settings.elevators.owners."+player.getDisplayName()+".X1")){
                                    if(config.getInt("settings.elevators.owners."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Y1") < F2Y && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Z1") == F2Z) {
                                        player.sendMessage(ChatColor.BLUE + "You've selected: " + block.getType() + " on a location: " + F2X + " " + F2Y + " " + F2Z + " as your 2F.");
                                        config.createSection("settings.elevators.owners." + player.getDisplayName() + ".X2");
                                        config.createSection("settings.elevators.owners." + player.getDisplayName() + ".Y2");
                                        config.createSection("settings.elevators.owners." + player.getDisplayName() + ".Z2");
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".X2", F2X);
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".Y2", F2Y);
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".Z2", F2Z);
                                    }
                                    else if(config.getInt("settings.elevators.owners."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Y1") > F2Y && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Z1") == F2Z){
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".X2", F2X);
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".Y2", config.getInt("settings.elevators.owners." + player.getDisplayName() + ".Y1"));
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".Y1", F2Y);
                                        config.set("settings.elevators.owners." + player.getDisplayName() + ".Z2", F2Z);
                                        player.sendMessage(ChatColor.YELLOW + "Your 1F changed to: " + F2X + " " + F2Y + " " + F2Z);
                                    }
                                    else if(config.getInt("settings.elevators.owners."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Y1") == F2Y && config.getInt("settings.elevators.owners."+player.getDisplayName()+".Z1") == F2Z){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you can't select same block as 1F and 2F.");
                                    }
                                    else{
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please select an elevator block above or below of your 1F.");
                                    }
                                }
                                else{
                                    player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                                }
                            }
                            else{
                                player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                            }
                            plugin.saveConfig();
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "An error has ocurred, please check if have blocks above of block you tried to select as 2F.");
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Maker!")) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

}
