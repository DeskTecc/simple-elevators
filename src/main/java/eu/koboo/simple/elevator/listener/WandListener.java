package eu.koboo.simple.elevator.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.bukkit.Bukkit.getLogger;

public class WandListener implements Listener {
    @EventHandler
    public boolean onPlayerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        World world = player.getWorld();
        assert block != null;
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Maker!")) {
                if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) && event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        Material clickedBlock = block.getType();
                        Location blockLocation = block.getLocation();
                        getLogger().info("Bloco que você clicou com o botão esquerdo: " + clickedBlock);
                        Bukkit.broadcastMessage("Player" + " " + player.getDisplayName() + " " + "clicou com o botão esquerdo no bloco: " + clickedBlock+" na localização: "+ blockLocation);
                        blockLocation.getBlock().setType(clickedBlock, false);
                    }
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        getLogger().info("Bloco que você clicou com o botão direito: " + block.getType());
                        Bukkit.broadcastMessage("Player" + " " + player.getDisplayName() + " " + "clicou com o botão direito no bloco: " + block.getType());

                    }
                }
            }
        }
        return true;
    }
}
