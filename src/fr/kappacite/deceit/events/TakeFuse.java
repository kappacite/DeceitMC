package fr.kappacite.deceit.events;

import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.objects.Game;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class TakeFuse implements Listener {

    @EventHandler
    public void onFuse(PlayerInteractEvent event){

        Player player = event.getPlayer();
        DPlayer dPlayer = DPlayer.instance(player);
        Game game = Deceit.getGame();

        if(!game.isDay()){

            if(!dPlayer.hasFuse() && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TORCH){
                dPlayer.setFuse(true);
                event.getClickedBlock().setType(Material.AIR);
                Netherboard.instance().getBoard(player).set("§bFusible: §6Oui", 7);
                return;
            }

            if(dPlayer.hasFuse() && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SPONGE){
                event.getClickedBlock().setType(Material.SPONGE);
                event.getClickedBlock().setData((byte) 1);
                game.getNight().addFuse();
                dPlayer.setFuse(false);
                Netherboard.instance().getBoard(player).set("§bFusible: §6Non", 7);
            }

        }

    }

}
