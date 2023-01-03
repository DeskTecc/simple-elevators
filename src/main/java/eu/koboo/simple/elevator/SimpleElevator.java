package eu.koboo.simple.elevator;

import eu.koboo.simple.elevator.config.ElevatorConfig;
import eu.koboo.simple.elevator.listener.PlayerCommandListener;
import eu.koboo.simple.elevator.listener.PlayerMoveListener;
import eu.koboo.simple.elevator.listener.PlayerToggleSneakListener;
import eu.koboo.simple.elevator.listener.WandListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.bukkit.Bukkit.getPlayer;

public class SimpleElevator extends JavaPlugin {

    private AtomicReference<ElevatorConfig> configReference;
    @Override
    public void onEnable() {
        File pluginDir = getDataFolder();
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }

        File configFile = new File(pluginDir, "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
        configReference = new AtomicReference<>(ElevatorConfig.loadConfig(this));
        this.getCommand("se").setExecutor(new PlayerCommandListener());
        Bukkit.getPluginManager().registerEvents(new WandListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleSneakListener(this), this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public ElevatorConfig getElevatorConfig() {
        return configReference.get();
    }

    public Location findNextElevatorAbove(Location location) {
        FileConfiguration config = this.getConfig();
        World world = location.getWorld();
        if(world == null) {
            return null;
        }
        ConfigurationSection names = config.getConfigurationSection("settings.elevators");
        if(names == null){
            return null;
        }
        for (String coords : names.getKeys(false)) {
                if (location.getBlockX() == config.getInt("settings.elevators." + coords + ".1F.X")
                        || location.getBlockX() == config.getInt("settings.elevators." + coords + ".2F.X")
                        && location.getBlockY() - 1 == config.getInt("settings.elevators." + coords + ".1F.Y")
                        || location.getBlockY() - 1 == config.getInt("settings.elevators." + coords + ".2F.Y")
                        && location.getBlockZ() == config.getInt("settings.elevators." + coords + ".1F.Z")
                        || location.getBlockZ() == config.getInt("settings.elevators." + coords + ".2F.Z")) {
                    return findNextElevator(location, location.getBlockY(), world.getMaxHeight(), true);
                }
        }
        return null;
    }

    public Location findNextElevatorBelow(Location location) {
        FileConfiguration config = this.getConfig();
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        ConfigurationSection names = config.getConfigurationSection("settings.elevators");
        if(names == null){
            return null;
        }
        for (String coords : names.getKeys(false)) {
                if (location.getBlockX() == config.getInt("settings.elevators." + coords + ".1F.X")
                        || location.getBlockX() == config.getInt("settings.elevators." + coords + ".2F.X")
                        && location.getBlockY() + 1 == config.getInt("settings.elevators." + coords + ".1F.Y")
                        || location.getBlockY() + 1 == config.getInt("settings.elevators." + coords + ".2F.Y")
                        && location.getBlockZ() == config.getInt("settings.elevators." + coords + ".1F.Z")
                        || location.getBlockZ() == config.getInt("settings.elevators." + coords + ".2F.Z")) {
                    return findNextElevator(location, world.getMinHeight(), location.getBlockY(), false);
                }
        }
        return null;
    }

    public Location findNextElevator(Location location, int from, int to, boolean up) {
        Location tempLoc = null;
        for(int i = from; i <= to; i++) {
            tempLoc = Objects.requireNonNullElseGet(tempLoc, location::clone);
            if(up) {
                tempLoc = tempLoc.add(0, 1, 0);
            } else {
                tempLoc = tempLoc.subtract(0, 1, 0);
            }
            Block block = tempLoc.getBlock();
            if(!getElevatorConfig().elevatorBlockList().contains(block.getType())) {
                continue;
            }
            if(!(tempLoc.add(0,2,0).getBlock().getType() == Material.AIR) || !(tempLoc.subtract(0,1,0).getBlock().getType() == Material.AIR)){
                return null;
            }
            return tempLoc.add(0, 1, 0);
        }
        return null;
    }

}
