package fr.kappacite.deceit.commands;

import fr.kappacite.deceit.Deceit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class AddSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){

            Player player = (Player) sender;
            Location location = player.getLocation();

            if(label.equalsIgnoreCase("addspawn")){

                if(player.hasPermission("deceit.admin")){

                    File file = Deceit.getFiles().getSpawnFile();
                    FileConfiguration config = Deceit.getFiles().getSpawnConfig();

                    Integer id = config.getKeys(false).size();

                    config.set("" + id, location);
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage("§7Spawn set en§f: §b" + location.getX() + " " + location.getY() + " " + location.getZ() + "§7.");

                }

            }

        }

        return false;
    }

}
