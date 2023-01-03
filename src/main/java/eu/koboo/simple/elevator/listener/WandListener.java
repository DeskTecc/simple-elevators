package eu.koboo.simple.elevator.listener;

import eu.koboo.simple.elevator.SimpleElevator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class WandListener implements Listener {

    private int taskID;
    static Plugin plugin = getServer().getPluginManager().getPlugin("SimpleElevator");

    @EventHandler
    public boolean onPlayerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        assert block != null;
        if(player.getInventory().getItemInMainHand().hasItemMeta()) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Elevator Wand!")) {
                if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) && !player.isSneaking())) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        int F1X = (int) block.getLocation().getX();
                        int F1Y = (int) block.getLocation().getY();
                        int F1Z = (int) block.getLocation().getZ();
                        if(check(player,F1X,F1Y,F1Z)){
                            return true;
                        }
                        onBlockBreak(new BlockBreakEvent(block, player));
                        Location locCheck1 = new Location(player.getWorld(),F1X,F1Y+1,F1Z);
                        Location locCheck2 = new Location(player.getWorld(),F1X,F1Y+2,F1Z);
                        BukkitScheduler scheduler = getServer().getScheduler();
                        int particle_1F = 0;
                        int finalParticle_1F = particle_1F;
                        particle_1F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                FileConfiguration config = plugin.getConfig();
                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == F1X && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == F1Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == F1Z) {
                                    Material checking = new Location(player.getWorld(), F1X,F1Y,F1Z).getBlock().getType();
                                    if(checking != Material.AIR) {
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X + 0.5, F1Y + 1.5, F1Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X + 0.5, F1Y - 0.5, F1Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X + 1.5, F1Y + 0.5, F1Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X - 0.5, F1Y + 0.5, F1Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X + 0.5, F1Y + 0.5, F1Z + 1.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F1X + 0.5, F1Y + 0.5, F1Z - 0.5, 1);
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
                                int[] secondConfig = {F1X, F1Y, F1Z};
                                player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+F1X+" "+F1Y+" "+F1Z+" as your 1F.");
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
                                player.sendMessage(ChatColor.AQUA+"You've selected: " + block.getType()+" on a location: "+F1X+" "+F1Y+" "+F1Z+" as your 1F.");
                                String[] firstConfig = {"X1","Y1","Z1","prev-block-1"};
                                int[] secondConfig = {F1X,F1Y,F1Z};
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
                        int F2X = (int) block.getLocation().getX();
                        int F2Y = (int) block.getLocation().getY();
                        int F2Z = (int) block.getLocation().getZ();
                        if(check(player, F2X, F2Y, F2Z)){
                            return true;
                        }
                        Location locCheck1 = new Location(player.getWorld(), F2X, F2Y + 1, F2Z);
                        Location locCheck2 = new Location(player.getWorld(), F2X, F2Y + 2, F2Z);
                        BukkitScheduler scheduler = getServer().getScheduler();
                        int particle_2F = 0;
                        int finalParticle_2F = particle_2F;
                        particle_2F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                FileConfiguration config = plugin.getConfig();
                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X2") == F2X && config.getInt("settings.elevators."+player.getDisplayName()+".Y2") == F2Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z2") == F2Z) {
                                    Material checking = new Location(player.getWorld(), F2X,F2Y,F2Z).getBlock().getType();
                                    if(checking != Material.AIR) {
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 1.5, F2Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y - 0.5, F2Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 1.5, F2Y + 0.5, F2Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X - 0.5, F2Y + 0.5, F2Z + 0.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 0.5, F2Z + 1.5, 1);
                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 0.5, F2Z - 0.5, 1);
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
                                    if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") < F2Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == F2Z) {
                                        if(F2Y - config.getInt("settings.elevators."+player.getDisplayName()+".Y1") <= 2){
                                            player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please select the elevator block 2 blocks above or below of your 1F.");
                                            return true;
                                        }
                                        else {
                                            player.sendMessage(ChatColor.BLUE + "You've selected: " + block.getType() + " on a location: " + F2X + " " + F2Y + " " + F2Z + " as your 2F.");
                                            String[] firstConfig = {"X2","Y2","Z2","prev-block-2"};
                                            int[] secondConfig = {F2X,F2Y,F2Z};
                                            for(int i=0;i<4;i++){
                                                config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                                if(i!=3) {
                                                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                                }
                                                else{
                                                    config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], block.getType().name());
                                                }
                                            }
                                            List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                                            block.setType(Material.valueOf((firstElevatorBlock.get(0))));
                                        }
                                    }

                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".Y1") > F2Y){
                                        String[] firstConfig = {"X2","Y2","Z2","prev-block-2"};
                                        int[] secondConfig = {F2X,config.getInt("settings.elevators." + player.getDisplayName() + ".Y1"),F2Z};
                                        for(int i=0;i<4;i++){
                                            config.createSection("settings.elevators."+player.getDisplayName()+"."+firstConfig[i]);
                                            if(i!=3) {
                                                config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], secondConfig[i]);
                                            }else{
                                                config.set("settings.elevators." + player.getDisplayName() + "." + firstConfig[i], config.getString("settings.elevators." + player.getDisplayName() + ".prev-block-1"));
                                            }
                                        }
                                        config.set("settings.elevators." + player.getDisplayName() + ".Y1", F2Y);
                                        config.set("settings.elevators."+player.getDisplayName()+".prev-block-1",block.getType().name());
                                        player.sendMessage(ChatColor.YELLOW + "Your 1F changed to: " + F2X + " " + F2Y + " " + F2Z);
                                        int particle_1F = 0;
                                        int finalParticle_1F = particle_1F;
                                        particle_1F = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                FileConfiguration config = plugin.getConfig();
                                                if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == F2Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == F2Z) {
                                                    Material checking = new Location(player.getWorld(), F2X,F2Y,F2Z).getBlock().getType();
                                                    if(checking != Material.AIR) {
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 1.5, F2Z + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y - 0.5, F2Z + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 1.5, F2Y + 0.5, F2Z + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X - 0.5, F2Y + 0.5, F2Z + 0.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 0.5, F2Z + 1.5, 1);
                                                        player.spawnParticle(Particle.VILLAGER_HAPPY, F2X + 0.5, F2Y + 0.5, F2Z - 0.5, 1);
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
                                        List<String> firstElevatorBlock = (List<String>) config.get("settings.elevator-blocks");
                                        block.setType(Material.valueOf((firstElevatorBlock.get(0))));
                                    }
                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") == F2X && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") == F2Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") == F2Z){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you can't select same block as 1F and 2F.");
                                        return true;
                                    }
                                    else if(config.getInt("settings.elevators."+player.getDisplayName()+".X1") != F2X && config.getInt("settings.elevators."+player.getDisplayName()+".Y1") != F2Y && config.getInt("settings.elevators."+player.getDisplayName()+".Z1") != F2Z){
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, you need select 1F before 2F.");
                                        return true;
                                    }
                                    else{
                                        player.sendMessage(ChatColor.DARK_RED+"An error has ocurred, please select the elevator block 2 blocks above or below of your 1F.");
                                        return true;
                                    }
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
                        int F1X = (int) block.getLocation().getX();
                        int F1Y = (int) block.getLocation().getY();
                        int F1Z = (int) block.getLocation().getZ();
                        FileConfiguration config = plugin.getConfig();
                        ConfigurationSection names = config.getConfigurationSection("settings.elevators");
                        if (names == null){
                            player.sendMessage(ChatColor.RED + "An error has ocurred, no elevators found in this world.");
                            return false;
                        }
                        for (String elevators : names.getKeys(false)) {
                            if (config.get("settings.elevators." + elevators) != null) {
                                if (config.getInt("settings.elevators." + elevators + ".1F.X") == F1X && config.getInt("settings.elevators." + elevators + ".1F.Y") == F1Y && config.getInt("settings.elevators." + elevators + ".1F.Z") == F1Z) {
                                    if(Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())){
                                        config.createSection("settings.elevators."+elevators+".deleting");
                                        config.set("settings.elevators."+elevators+".deleting",true);
                                        player.sendMessage(ChatColor.YELLOW + "Are you sure delete this elevator?\n" + ChatColor.RED + "This will remove 1F and 2F.");
                                        player.sendMessage(ChatColor.YELLOW + "If you want delete, use /se delete to confirm");
                                        plugin.saveConfig();
                                    }
                                }
                                if(config.getInt("settings.elevators." + elevators + ".2F.X") == F1X && config.getInt("settings.elevators." + elevators + ".2F.Y") == F1Y && config.getInt("settings.elevators." + elevators + ".2F.Z") == F1Z){
                                    if(Objects.equals(config.getString("settings.elevators." + elevators + ".owner"), player.getDisplayName())){
                                        config.createSection("settings.elevators."+elevators+".deleting");
                                        config.set("settings.elevators."+elevators+".deleting",true);
                                        player.sendMessage(ChatColor.YELLOW + "Are you sure delete this elevator?\n" + ChatColor.RED + "This will remove 1F and 2F.");
                                        player.sendMessage(ChatColor.YELLOW + "If you want delete, use /se delete to confirm");
                                        plugin.saveConfig();
                                    }
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
            int Blockx1 = config.getInt("settings.elevators." + player.getDisplayName() + ".X1");
            int Blocky1 = config.getInt("settings.elevators." + player.getDisplayName() + ".Y1");
            int Blockz1 = config.getInt("settings.elevators." + player.getDisplayName() + ".Z1");
            String Blocktype1 = config.getString("settings.elevators."+player.getDisplayName()+".prev-block-1");
            Location old_block1 = new Location(player.getWorld(), Blockx1,Blocky1,Blockz1);
            old_block1.getBlock().setType(Material.valueOf(Blocktype1));
            config.set("settings.elevators."+player.getDisplayName()+".X1", null);
            config.set("settings.elevators."+player.getDisplayName()+".Y1", null);
            config.set("settings.elevators."+player.getDisplayName()+".Z1", null);
            config.set("settings.elevators."+player.getDisplayName()+".prev-block-1", null);
        }
        if(option.equals("only2")) {
            int Blockx2 = config.getInt("settings.elevators." + player.getDisplayName() + ".X2");
            int Blocky2 = config.getInt("settings.elevators." + player.getDisplayName() + ".Y2");
            int Blockz2 = config.getInt("settings.elevators." + player.getDisplayName() + ".Z2");
            String Blocktype2 = config.getString("settings.elevators." + player.getDisplayName() + ".prev-block-2");
            Location old_block2 = new Location(player.getWorld(), Blockx2, Blocky2, Blockz2);
            old_block2.getBlock().setType(Material.valueOf(Blocktype2));
            config.set("settings.elevators." + player.getDisplayName() + ".X2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".Y2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".Z2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".prev-block-2", null);
        }
        if(option.equals("all")){
            int Blockx1 = config.getInt("settings.elevators." + player.getDisplayName() + ".X1");
            int Blocky1 = config.getInt("settings.elevators." + player.getDisplayName() + ".Y1");
            int Blockz1 = config.getInt("settings.elevators." + player.getDisplayName() + ".Z1");
            String Blocktype1 = config.getString("settings.elevators."+player.getDisplayName()+".prev-block-1");
            Location old_block1 = new Location(player.getWorld(), Blockx1,Blocky1,Blockz1);
            old_block1.getBlock().setType(Material.valueOf(Blocktype1));
            config.set("settings.elevators."+player.getDisplayName()+".X1", null);
            config.set("settings.elevators."+player.getDisplayName()+".Y1", null);
            config.set("settings.elevators."+player.getDisplayName()+".Z1", null);
            config.set("settings.elevators."+player.getDisplayName()+".prev-block-1", null);

            int Blockx2 = config.getInt("settings.elevators." + player.getDisplayName() + ".X2");
            int Blocky2 = config.getInt("settings.elevators." + player.getDisplayName() + ".Y2");
            int Blockz2 = config.getInt("settings.elevators." + player.getDisplayName() + ".Z2");
            String Blocktype2 = config.getString("settings.elevators." + player.getDisplayName() + ".prev-block-2");
            Location old_block2 = new Location(player.getWorld(), Blockx2, Blocky2, Blockz2);
            old_block2.getBlock().setType(Material.valueOf(Blocktype2));
            config.set("settings.elevators." + player.getDisplayName() + ".X2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".Y2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".Z2", null);
            config.set("settings.elevators." + player.getDisplayName() + ".prev-block-2", null);
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
