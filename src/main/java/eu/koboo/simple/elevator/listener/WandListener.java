package eu.koboo.simple.elevator.listener;

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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class WandListener implements Listener {

    private int taskID;
    static Plugin plugin = getServer().getPluginManager().getPlugin("SimpleElevator");

    @EventHandler
    public boolean onPlayerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        assert block != null;
        int[] Floor = {(int) block.getLocation().getX(), (int) block.getLocation().getY(), (int) block.getLocation().getZ()};
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Wand!")) {
                if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) && !player.isSneaking())) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        if(check(player,Floor[0],Floor[1],Floor[2])){
                            FileConfiguration config = plugin.getConfig();
                            ConfigurationSection names = config.getConfigurationSection("settings.elevators");
                            for (String elevators : names.getKeys(false)) {
                                if (config.get("settings.elevators." + elevators) != null) {
                                        if (config.getInt("settings.elevators." + elevators + ".2F.X") == Floor[0] && config.getInt("settings.elevators." + elevators + ".2F.Y") == Floor[1] && config.getInt("settings.elevators." + elevators + ".2F.Z") == Floor[2]) {
                                            if (Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())) {
                                                player.sendMessage(ChatColor.YELLOW + "If you want expand to next floor, use /se expand"); //next update: ChatColor.YELLOW+"if you want to expand to 3F, select 2F with left click.");
                                                return true;
                                            }
                                        }
                                    }
                                }
                            return true;
                        }
                        onBlockBreak(new BlockBreakEvent(block, player));
                        Location locCheck1 = new Location(player.getWorld(),Floor[0],Floor[1]+1,Floor[2]);
                        Location locCheck2 = new Location(player.getWorld(),Floor[0],Floor[1]+2,Floor[2]);
                        BukkitScheduler scheduler = getServer().getScheduler();
                        int particle_1F = 0;
                        int finalParticle_1F = particle_1F;
                        particle_1F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                FileConfiguration config = plugin.getConfig();
                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == Floor[2]) {
                                    Material checking = new Location(player.getWorld(), Floor[0],Floor[1],Floor[2]).getBlock().getType();
                                    if(checking != Material.AIR) {
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 1.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] - 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 1.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] - 0.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] + 1.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] - 0.5, 1);
                                    }
                                    else{
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, one of elevator blocks has been destroyed");
                                        Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                    }
                                    if(locCheck1.getBlock().getType() != Material.AIR && locCheck2.getBlock().getType() != Material.AIR){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, a block has been placed above of block you tried to select as 1F.");
                                        Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                    }
                                }
                                else{
                                    Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                }}}, 0L, 20L);

                        if(locCheck1.getBlock().getType() == Material.AIR && locCheck2.getBlock().getType() == Material.AIR){
                            FileConfiguration config = plugin.getConfig();
                            if(config.contains("settings.elevators."+player.getDisplayName())){
                                if(config.contains("settings.elevators."+player.getDisplayName()+".X1") && !config.contains("settings.elevators."+player.getDisplayName()+".X2")){
                                    resetBlocks(player, "only1");
                                }
                                else if(config.contains("settings.elevators."+player.getDisplayName()+".X1") && config.contains("settings.elevators."+player.getDisplayName()+".X2")){
                                    resetBlocks(player, "all");
                                }
                                String[] firstConfig = {"X1","Y1","Z1","prev-block-1"};
                                int[] secondConfig = {Floor[0], Floor[1], Floor[2]};
                                player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+Floor[0]+" "+Floor[1]+" "+Floor[2]+" as your 1F.");
                                for(int i=0;i<4;i++){
                                    config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                    if(i!=3) {
                                        config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                    }
                                    else{
                                        config.set("settings.elevators."+player.getDisplayName()+"."+firstConfig[i], block.getType().name());
                                    }
                                }
                                List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                                block.setType(Material.valueOf((firstElevatorBlock.get(0))));
                                /* if(config.contains("settings.elevators."+player.getDisplayName()+".X2")){
                                    config.set("settings.elevators."+player.getDisplayName()+".X2", null);
                                    config.set("settings.elevators."+player.getDisplayName()+".Y2", null);
                                    config.set("settings.elevators."+player.getDisplayName()+".Z2", null);
                                    player.sendMessage(ChatColor.YELLOW+"You changed the 1F, please select 2F pressing right click.");
                                }else{
                                    player.sendMessage(ChatColor.YELLOW+"Select 2F pressing right click.");
                                } */
                            }
                            else {
                                player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+Floor[0]+" "+Floor[1]+" "+Floor[2]+" as your 1F.");
                                String[] firstConfig = {"X1","Y1","Z1","prev-block-1"};
                                int[] secondConfig = {Floor[0], Floor[1], Floor[2]};
                                for(int i=0;i<4;i++){
                                    config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                    if(i!=3) {
                                        config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                    }
                                    else{
                                        config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i],block.getType().name());
                                    }
                                }
                                player.sendMessage(ChatColor.YELLOW+"Select 2F pressing right click.");
                                List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                                block.setType(Material.valueOf((firstElevatorBlock.get(0))));
                            }
                            plugin.saveConfig();

                        }
                        else{
                            player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please check if have blocks above of block you tried to select as 1F.");
                        }
                    }
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !player.isSneaking() && Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
                        if(check(player, Floor[0], Floor[1], Floor[2])){
                            return true;
                        }
                        Location locCheck1 = new Location(player.getWorld(), Floor[0], Floor[1] + 1, Floor[2]);
                        Location locCheck2 = new Location(player.getWorld(), Floor[0], Floor[1] + 2, Floor[2]);
                        BukkitScheduler scheduler = getServer().getScheduler();
                        int particle_2F = 0;
                        int finalParticle_2F = particle_2F;
                        particle_2F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                FileConfiguration config = plugin.getConfig();
                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X2") == Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y2") == Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z2") == Floor[2]) {
                                    Material checking = new Location(player.getWorld(), Floor[0],Floor[1],Floor[2]).getBlock().getType();
                                    if(checking != Material.AIR) {
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 1.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] - 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 1.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] - 0.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] + 1.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] - 0.5, 1);
                                    }else{
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, one of elevator blocks has been destroyed");
                                        Bukkit.getScheduler().cancelTask(finalParticle_2F);
                                    }
                                    if(locCheck1.getBlock().getType() != Material.AIR && locCheck2.getBlock().getType() != Material.AIR){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, a block has been placed above of block you tried to select as 2F.");
                                        Bukkit.getScheduler().cancelTask(finalParticle_2F);
                                    }
                                } else{
                                    Bukkit.getScheduler().cancelTask(finalParticle_2F);
                                }}}, 0L, 20L);
                        if (locCheck1.getBlock().getType() == Material.AIR && locCheck2.getBlock().getType() == Material.AIR) {
                            FileConfiguration config = plugin.getConfig();
                            if(config.contains("settings.elevators."+player.getDisplayName())){
                                if(config.contains("settings.elevators."+player.getDisplayName()+".X1")){
                                    if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") < Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == Floor[2]) {
                                        if(Floor[1] - config.getInt("settings.elevators."+player.getDisplayName()+".Y1") <= 2){
                                            player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please select the elevator block 2 blocks above or below of your 1F.");
                                            return true;
                                        }
                                        else {
                                            player.sendMessage(ChatColor.BLUE + "You've selected: " + block.getType() + " on a location: " + Floor[0] + " " + Floor[1] + " " + Floor[2] + " as your 2F.");
                                            String[] firstConfig = {"X2","Y2","Z2","prev-block-2"};
                                            int[] secondConfig = {Floor[0],Floor[1],Floor[2]};
                                            for(int i=0;i<4;i++){
                                                config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                                if(i!=3) {
                                                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                                }
                                                else{
                                                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], block.getType().name());
                                                }
                                            }
                                        }
                                    }

                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".Y1") > Floor[1]){
                                        String[] firstConfig = {"X2","Y2","Z2","prev-block-2"};
                                        int[] secondConfig = {Floor[0],config.getInt("settings.elevators." + player.getDisplayName() + ".Y1"),Floor[2]};
                                        for(int i=0;i<4;i++){
                                            config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                            if(i!=3) {
                                                config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                            }else{
                                                config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], config.getString("settings.elevators." + player.getDisplayName() + ".prev-block-1"));
                                            }
                                        }
                                        config.set("settings.elevators." + player.getDisplayName() + ".Y1", Floor[1]);
                                        config.set("settings.elevators."+player.getDisplayName()+".prev-block-1",block.getType().name());
                                        player.sendMessage(ChatColor.YELLOW + "Your 1F changed to: " + Floor[0] + " " + Floor[1] + " " + Floor[2]);
                                        int particle_1F = 0;
                                        int finalParticle_1F = particle_1F;
                                        particle_1F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                FileConfiguration config = plugin.getConfig();
                                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == Floor[2]) {
                                                    Material checking = new Location(player.getWorld(), Floor[0],Floor[1],Floor[2]).getBlock().getType();
                                                    if(checking != Material.AIR) {
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 1.5, Floor[2] + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] - 0.5, Floor[2] + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 1.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] - 0.5, Floor[1] + 0.5, Floor[2] + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] + 1.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, Floor[0] + 0.5, Floor[1] + 0.5, Floor[2] - 0.5, 1);
                                                    }else{
                                                    player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, one of elevator blocks has been destroyed");
                                                    Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                                }
                                                if(locCheck1.getBlock().getType() != Material.AIR && locCheck2.getBlock().getType() != Material.AIR){
                                                    player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, a block has been placed above of block you tried to select as 2F.");
                                                    Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                                }
                                                } else{
                                                    Bukkit.getScheduler().cancelTask(finalParticle_1F);
                                                }}}, 0L, 20L);
                                    }
                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == Floor[2]){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you can't select same block as 1F and 2F.");
                                        return true;
                                    }
                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") != Floor[0] && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") != Floor[1] && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") != Floor[2]){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                                        return true;
                                    }
                                    else{
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please select the elevator block 2 blocks above or below of your 1F.");
                                        return true;
                                    }
                                    List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                                    block.setType(Material.valueOf((firstElevatorBlock.get(0))));
                                    player.sendMessage(ChatColor.GREEN+"All right! Now you need to give a name to save your elevator.\n"+ChatColor.YELLOW+"Use /se name <name> to give a name to your elevator.");
                                    player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP, 2f,1f);
                                }
                                else{
                                    player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                                    return true;
                                }
                            }
                            else{
                                player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                                return true;
                            }
                            plugin.saveConfig();
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "An error has ocurred, please check if have blocks above of block you tried to select as 2F.");
                            return true;
                        }
                    }
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking() && Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
                        FileConfiguration config = plugin.getConfig();
                        ConfigurationSection names = config.getConfigurationSection("settings.elevators");
                        if (names == null){
                            player.sendMessage(ChatColor.RED + "An error has ocurred, no elevators found in this world.");
                            return false;
                        }
                        for (String elevators : names.getKeys(false)) {
                            if (config.get("settings.elevators." + elevators) != null && Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())) {
                                config.createSection("settings.elevators."+elevators+".deleting");
                                config.set("settings.elevators."+elevators+".deleting",true);
                                if (config.getInt("settings.elevators." + elevators + ".1F.X") == Floor[0]
                                        || config.getInt("settings.elevators." + elevators + ".2F.X") == Floor[0]
                                        && config.getInt("settings.elevators." + elevators + ".1F.Y") == Floor[1]
                                        || config.getInt("settings.elevators." + elevators + ".2F.Y") == Floor[1]
                                        && config.getInt("settings.elevators." + elevators + ".1F.Z") == Floor[2]
                                        || config.getInt("settings.elevators." + elevators + ".2F.Z") == Floor[2]) {
                                        player.sendMessage(ChatColor.YELLOW + "Are you sure delete the elevator:"+elevators+"?\n" + ChatColor.RED + "This will remove 1F and 2F.");
                                        player.sendMessage(ChatColor.YELLOW + "If you want delete, use /se delete to confirm");
                                        plugin.saveConfig();
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean check(Player player,int x, int y, int z){
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection names = config.getConfigurationSection("settings.elevators");
        if (names == null){
            player.sendMessage(ChatColor.RED + "An error has ocurred, no elevators found in this world.");
            return false;
        }
        for (String elevators : names.getKeys(false)) {
            if (config.get("settings.elevators." + elevators) != null) {
                for(int counter=1;counter<=50;counter++) {
                    if (config.getInt("settings.elevators." + elevators + "."+counter+"F.X") == x && config.getInt("settings.elevators." + elevators + "."+counter+"F.Y") == y && config.getInt("settings.elevators." + elevators + "."+counter+"F.Z") == z) {
                        if (Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())) {
                            player.sendMessage(ChatColor.RED + "You are the owner of this elevator!"); //next update: ChatColor.YELLOW+"if you want to expand to 3F, select 2F with left click.");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "This elevator has a owner!");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public static void resetBlocks(Player player_, String option){
        FileConfiguration config = plugin.getConfig();
        Player player = player_;
        if(option.equals("only1")) {
            new Location(player.getWorld(),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".X1"),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".Y1"),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".Z1"))
                    .getBlock().setType(Material.valueOf((config.getString("settings.elevators."+player.getDisplayName()+".prev-block-1"))));
            String[] firstConfig = {"X1","Y1","Z1","prev-block-1"};
            for(int i=0;i<4;i++){
                config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                if(i!=3) {
                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], null);
                }
                else{
                    config.set("settings.elevators."+player.getDisplayName()+"."+firstConfig[i], null);
                }
            }
        }
        if(option.equals("only2")) {
            new Location(player.getWorld(),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".X2"),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".Y2"),
                    config.getInt("settings.elevators." + player.getDisplayName() + ".Z2"))
                    .getBlock().setType(Material.valueOf((config.getString("settings.elevators."+player.getDisplayName()+".prev-block-2"))));
            String[] firstConfig = {"X2","Y2","Z2","prev-block-2"};
            for(int i=0;i<4;i++){
                config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                if(i!=3) {
                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], null);
                }
                else{
                    config.set("settings.elevators."+player.getDisplayName()+"."+firstConfig[i], null);
                }
            }
        }
        if(option.equals("all")){
            resetBlocks(player, "only1");
            resetBlocks(player, "only2");
        }
        plugin.saveConfig();
        return;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Wand!")) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

}
