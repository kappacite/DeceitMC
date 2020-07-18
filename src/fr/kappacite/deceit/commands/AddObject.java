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

public class AddObject implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){

            Player player = (Player) sender;
            Location location = player.getLocation();

            if(label.equalsIgnoreCase("addobject")){

                if(player.hasPermission("deceit.admin")){

                    File file = Deceit.getFiles().getObjectivesFile();
                    FileConfiguration config = Deceit.getFiles().getObjectivesConfig();

                    Integer id = 0;
                    if(config.getConfigurationSection(args[0]) != null) id = config.getConfigurationSection(args[0]).getKeys(false).size();

                    config.set(args[0] + "." + id + ".loc", location);
                    config.set(args[0] + "." + id + ".type", args[1].toUpperCase());
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage("§7Object(" + args[1].toUpperCase() +") set en§f: §b" + Math.floor(location.getX()) + " "
                            + Math.floor(location.getY()) + " " + Math.floor(location.getZ()) + "§7.");

                }

            }

        }

        return false;
    }
}
