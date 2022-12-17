package eu.koboo.simple.elevator.listener;


import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class PlayerCommandListener implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            ItemStack item = new ItemStack(Material.STICK, 1);
            ItemMeta meta = item.getItemMeta();
            if (player.getInventory().contains(Material.STICK, 1)) {
                player.getInventory().remove(Material.STICK);

            } else {
                assert meta != null;
                meta.setDisplayName("§l§bElevator Maker!");
                item.setItemMeta(meta);
                player.getInventory().addItem(item);

            }
        }


        else{
            getLogger().info("You need to be a player to do this!");
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
