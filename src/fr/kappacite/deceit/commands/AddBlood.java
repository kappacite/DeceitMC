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

public class AddBlood implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){

            Player player = (Player) sender;
            Location location = player.getLocation();

            if(label.equalsIgnoreCase("addblood")){

                if(player.hasPermission("deceit.admin")){

                    File file = Deceit.getFiles().getBloodFile();
                    FileConfiguration config = Deceit.getFiles().getBloodConfig();

                    Integer id = 0;
                    if(config.getConfigurationSection(args[0]) != null) id = config.getConfigurationSection(args[0]).getKeys(false).size();

                    config.set(args[0] + "." + id, location);
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage("§7Sang set en§f: §b" + location.getX() + " " + location.getY() + " " + location.getZ() + "§7.");

                }

            }

        }

        return false;
    }

}
