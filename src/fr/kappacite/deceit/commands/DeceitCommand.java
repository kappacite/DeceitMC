package fr.kappacite.deceit.commands;

import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeceitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){

            Player player = (Player) sender;
            DPlayer dPlayer = DPlayer.instance(player);

            if(label.equalsIgnoreCase("deceit")){

                if(!Deceit.getGame().isHasStarted()) return false;

                if(args.length == 0){

                    player.sendMessage("§b/§7deceit inno §fpour parler avec les innocents.");
                    player.sendMessage("§b/§7deceit infecte §fpour parler avec l'infecter.");
                    return false;

                }

                if(args.length == 1) return false;

                StringBuilder builder = new StringBuilder();

                for(int i = 1; i<args.length; i++){
                    builder.append(args[i]);
                }

                if(args[0].equalsIgnoreCase("inno")){

                    Deceit.getGame().getGamePlayers().forEach(p -> {
                        p.sendMessage("§aINNOCENT §f" + player.getName() + "≫ §7" + builder.toString());
                    });
                    return true;
                }

                if(args[0].equalsIgnoreCase("infecte")){

                    if(!(dPlayer.getRole() instanceof DInfected)) return false;

                    DInfected.infected.forEach(p -> {
                        p.sendMessage("§CINFECTE §f" + player.getName() + "≫ §c" + builder.toString());
                    });
                    return true;
                }

                player.sendMessage("§b/§7deceit inno §fpour parler avec les innocents.");
                player.sendMessage("§b/§7deceit infecte §fpour parler avec l'infecter.");
                return false;

            }

        }

        return false;
    }
}
