package eu.koboo.simple.elevator.listener;

import eu.koboo.simple.elevator.SimpleElevator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PlayerMoveListener implements Listener {

    SimpleElevator plugin;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        if(to == null) {
            return;
        }
        Player player = event.getPlayer();
        if(player.isFlying()) {
            return;
        }
        if(player.isSwimming()) {
            return;
        }
        Location from = event.getFrom();
        if(to.getY() <= from.getY()) {
            return;
        }
        Block fromBlock = from.getBlock().getRelative(BlockFace.DOWN);
        if(fromBlock.getType() == Material.AIR) {
            fromBlock = fromBlock.getRelative(BlockFace.DOWN);
        }
        if(!plugin.getElevatorConfig().elevatorBlockList().contains(fromBlock.getType())) {
            return;
        }
        // Player wants to go up
        Location elevatorLoc = plugin.findNextElevatorAbove(from);
        if (elevatorLoc == null) {
            if (!player.isOnGround() && Double.compare(player.getVelocity().getY(), 0.42F) == 0) {
                if(to.getBlock().getType() != Material.AIR){
                    player.sendMessage(ChatColor.RED + "An error has ocurred, please check if don't have blocks above the elevator block.");
                }
            }
            return;
        }


        player.teleport(elevatorLoc);
        player.playSound(elevatorLoc, Sound.ENTITY_BAT_TAKEOFF, 2f, 1f);
    }
}
