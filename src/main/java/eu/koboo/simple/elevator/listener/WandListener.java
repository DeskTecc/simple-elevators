package eu.koboo.simple.elevator.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public class WandListener implements Listener {
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
                            player.sendMessage(ChatColor.BLUE+"You've selected: " + block.getType()+" on a location: "+F1X+" "+F1Y+" "+F1Z+" as your 1F.");
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
                            player.sendMessage(ChatColor.BLUE + "You've selected: " + block.getType() + " on a location: " + F2X + " " + F2Y + " " + F2Z + " as your 2F.");
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
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Maker!")) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

}
