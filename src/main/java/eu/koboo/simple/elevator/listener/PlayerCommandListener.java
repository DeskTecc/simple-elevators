package eu.koboo.simple.elevator.listener;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class PlayerCommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
            for(ItemStack item : player.getInventory().getContents()) {
                if (!(item == null)) {
                    if (item.getItemMeta().hasDisplayName()) {
                        if (item.getItemMeta().getDisplayName().equals(ChatColor.AQUA+"Elevator Maker!")) {
                            item.setAmount(0);
                            player.sendMessage(ChatColor.RED+"The Elevator Maker was removed from your inventory");
                            return true;
                        }
                    }
                }
            }
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            assert meta != null;
            meta.setDisplayName(ChatColor.AQUA+"Elevator Maker!");
            meta.addEnchant(Enchantment.LUCK,1,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            lore.add(0,ChatColor.YELLOW+"Left Click: "+ChatColor.WHITE+"Set one elevator");
            lore.add(1,ChatColor.GOLD+"Right Click: "+ChatColor.WHITE+"Set second elevator");
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN+"The Elevator Maker was added to your hand");
        }
        else{
            getLogger().info("You must to be a player to do this!");
        }

        return true;
    }
}
