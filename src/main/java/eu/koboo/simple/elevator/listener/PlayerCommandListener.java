package eu.koboo.simple.elevator.listener;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static eu.koboo.simple.elevator.listener.WandListener.plugin;
import static eu.koboo.simple.elevator.listener.WandListener.resetBlocks;
import static org.bukkit.Bukkit.getLogger;

public class PlayerCommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if(Objects.equals(args[0], "wand")) {
                Player player = (Player) sender;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (!(item == null)) {
                        if (item.getItemMeta().hasDisplayName()) {
                            if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Wand!")) {
                                item.setAmount(0);
                                player.sendMessage(ChatColor.RED + "The Elevator Wand was removed from your inventory");
                                return true;
                            }
                        }
                    }
                }
                ItemStack item = new ItemStack(Material.STICK);
                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                assert meta != null;
                meta.setDisplayName(ChatColor.AQUA + "Elevator Wand!");
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(0, ChatColor.YELLOW + "Left Click: " + ChatColor.WHITE + "Set the 1F");
                lore.add(1, ChatColor.GOLD + "Right Click: " + ChatColor.WHITE + "Set the 2F");
                meta.setLore(lore);
                item.setItemMeta(meta);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GREEN + "The Elevator Wand was added to your hand");
            }
            if(Objects.equals(args[0], "cancel")) {
                FileConfiguration config = plugin.getConfig();
                Player player = (Player) sender;
                if (config.contains("settings.elevators." + player.getDisplayName() + ".X1") && config.contains("settings.elevators." + player.getDisplayName() + ".X2")) {
                    resetBlocks(player, "all");
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BIT,2f,1f);
                    player.sendMessage(ChatColor.GREEN + "Done!");
                } else if (config.contains("settings.elevators." + player.getDisplayName() + ".X1") && !config.contains("settings.elevators." + player.getDisplayName() + ".X2")) {
                    resetBlocks(player, "only1");
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BIT,2f,1f);
                    player.sendMessage(ChatColor.GREEN + "Done!");
                } else if (!config.contains("settings.elevators." + player.getDisplayName() + ".X1") && config.contains("settings.elevators." + player.getDisplayName() + ".X2")) {
                    resetBlocks(player, "only2");
                    player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BIT,2f,1f);
                    player.sendMessage(ChatColor.GREEN + "Done!");
                } else {
                    player.sendMessage(ChatColor.RED + "You dont have any elevator!");
                }
                return true;
            }
            if(Objects.equals(args[0], "delete")) {
                FileConfiguration config = plugin.getConfig();
                Player player = (Player) sender;
                ConfigurationSection names = config.getConfigurationSection("settings.elevators");
                if (names == null){
                    player.sendMessage(ChatColor.RED + "An error has ocurred, no elevators found in this world.");
                    return true;
                }
                for (String elevators : names.getKeys(false)) {
                    if(config.get("settings.elevators."+elevators)!= null && !Objects.equals(config.get("settings.elevators." + elevators), player.getDisplayName())){
                        if(Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())) {
                            if(config.getBoolean("settings.elevators."+elevators+".deleting")) {
                                Location old_block1 = new Location(player.getWorld(),
                                        config.getInt("settings.elevators." + elevators + ".1F.X"),
                                        config.getInt("settings.elevators." + elevators + ".1F.Y"),
                                        config.getInt("settings.elevators." + elevators + ".1F.Z"));
                                Location old_block2 = new Location(player.getWorld(),
                                        config.getInt("settings.elevators." + elevators + ".2F.X"),
                                        config.getInt("settings.elevators." + elevators + ".2F.Y"),
                                        config.getInt("settings.elevators." + elevators + ".2F.Z"));
                                old_block1.getBlock().setType(Material.valueOf(config.getString("settings.elevators." + elevators + ".1F.prev-block")));
                                old_block2.getBlock().setType(Material.valueOf(config.getString("settings.elevators." + elevators + ".2F.prev-block")));
                                config.set("settings.elevators." + elevators, null);
                                plugin.saveConfig();
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 1f);
                                player.sendMessage(ChatColor.GREEN + "Done!");
                                return true;
                            }
                        }
                    }
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
                player.sendMessage(ChatColor.RED + "You have not selected any elevator!");
                return true;
            }
            if (args.length > 1 && Objects.equals(args[0], "name")) {
                FileConfiguration config = plugin.getConfig();
                Player player = (Player) sender;
                String[][] create_save = {{"1F","2F"},{"X","Y","Z","prev-block"}};
                int[] coords = {config.getInt("settings.elevators." + player.getDisplayName() + ".X1"),
                                config.getInt("settings.elevators." + player.getDisplayName() + ".Y1"),
                                config.getInt("settings.elevators." + player.getDisplayName() + ".Z1"),
                                config.getInt("settings.elevators." + player.getDisplayName() + ".X2"),
                                config.getInt("settings.elevators." + player.getDisplayName() + ".Y2"),
                                config.getInt("settings.elevators." + player.getDisplayName() + ".Z2")};
                Object[] prev_block = {config.get("settings.elevators." + player.getDisplayName() + ".prev-block-1"),config.get("settings.elevators." + player.getDisplayName() + ".prev-block-2")};
                if (config.contains("settings.elevators." + player.getDisplayName() + ".X1") && config.contains("settings.elevators." + player.getDisplayName() + ".X2")) {
                    config.createSection("settings.elevators."+ args[1]);
                    config.createSection("settings.elevators."+args[1]+".owner");
                    config.set("settings.elevators."+args[1]+".owner",player.getDisplayName());
                    for(int c=0;c<2;c++) {
                        for (int i = 0; i <4; i++) {
                            if (!config.contains("settings.elevators." + args[1] + "." + create_save[0][c])) {
                                config.createSection("settings.elevators." + args[1] + "." + create_save[0][c]);
                            }
                                config.createSection("settings.elevators." + args[1] + "." + create_save[0][c] + "." + create_save[1][i]);
                                 if(i==3){
                                    config.set("settings.elevators." + args[1] + "." + create_save[0][c] + "." + create_save[1][i], prev_block[c]);
                                }else if(Objects.equals(create_save[0][c], "1F")){
                                     config.set("settings.elevators." + args[1] + "." + create_save[0][c] + "." + create_save[1][i], coords[i]);
                                 }
                                 else if(Objects.equals(create_save[0][c], "2F")){
                                     config.set("settings.elevators." + args[1] + "." + create_save[0][c] + "." + create_save[1][i], coords[i+3]);
                                 }
                        }
                    }
                    config.createSection("settings.elevators."+args[1]+".1F.sound");
                    config.createSection("settings.elevators."+args[1]+".1F.text");
                    config.createSection("settings.elevators."+args[1]+".2F.sound");
                    config.createSection("settings.elevators."+args[1]+".2F.text");
                    config.set("settings.elevators."+args[1]+".1F.sound", "ENTITY_BAT_TAKEOFF");
                    config.set("settings.elevators."+args[1]+".2F.sound", "ENTITY_BAT_TAKEOFF");
                    WandListener.resetBlocks(player, "all");
                    Block block1F = new Location(player.getWorld(), coords[0],coords[1],coords[2]).getBlock();
                    Block block2F = new Location(player.getWorld(), coords[3],coords[4],coords[5]).getBlock();
                    List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                    block1F.setType(Material.valueOf((firstElevatorBlock.get(0))));
                    block2F.setType(Material.valueOf((firstElevatorBlock.get(0))));
                    player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,2f,1f);
                    player.sendMessage(ChatColor.GREEN + "Done! Your elevator was saved with name: "+args[1]+"!");
                }
                else{
                    player.sendMessage(ChatColor.RED + "You need to select 1F and 2F with Elevator Wand!");
                }
            }
            else if(!(args.length > 1)){
                Player player = (Player) sender;
                player.sendMessage(ChatColor.RED + "You need to specify a name!\n"+ChatColor.YELLOW+"Use /se name <elevator-name>");
            }
            if(Objects.equals(args[0], "list")) {
                FileConfiguration config = plugin.getConfig();
                Player player = (Player) sender;
                player.sendMessage(ChatColor.YELLOW + "Your active elevators:\n");
                ConfigurationSection names = config.getConfigurationSection("settings.elevators");
                if (names == null){
                    player.sendMessage(ChatColor.RED + "An error has ocurred, no elevators found in this world.");
                    return true;
                }

                for (String elevators : names.getKeys(false)) {
                    if(config.get("settings.elevators."+elevators)!= null && !Objects.equals(config.get("settings.elevators." + elevators), player.getDisplayName())){
                        if(Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())) {
                                player.sendMessage(ChatColor.GREEN + elevators);
                                List<String> list = new ArrayList<String>();
                                config.getConfigurationSection("settings.elevators."+elevators+".1F").getKeys(false).forEach(key -> list.add(config.getString("settings.elevators."+elevators+".1F." + key)));
                                player.sendMessage(ChatColor.WHITE + "Data: "+ ChatColor.AQUA+ list.toString()+"\n");
                        }
                    }
                }
                return true;
            }

            if (Objects.equals(args[0], "") || Objects.equals(args[0], "help")) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.GOLD + "================="+ChatColor.AQUA+ "SimpleElevator"+ ChatColor.GOLD+"===================\n"+ ChatColor.DARK_GREEN +"/se wand - to get a Elevator Wand!\n/se delete - to delete your elevator.\n/se name - to save and give a name to your elevator."+ChatColor.GOLD + "\n====================================================="+ ChatColor.RESET);
            }
        }
        else{
            getLogger().info("You must to be a player to do this!");
        }
        return true;
    }
}
