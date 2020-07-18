package fr.kappacite.deceit.events;

import fr.kappacite.deceit.Deceit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoorOpen implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){

            if(event.getClickedBlock().getType() == Material.WOOD_BUTTON && Deceit.getGame().getNight().isCanOpenDoor()){

                Deceit.getGame().getNight().setCanOpenDoor(false);
                for (Player gamePlayer : Deceit.getGame().getGamePlayers()) {
                    Deceit.getTitle().sendTitle(gamePlayer, "§aLa porte va s'ouvrir", "§adans 5 secondes !", 20);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), () -> {

                    Location loc = (Location) Deceit.getDeceit().getConfig().get("exit");
                    loc.getBlock().setType(Material.AIR);
                    loc.add(0, 1, 0).getBlock().setType(Material.AIR);
                    loc.add(0, 1, 0).getBlock().setType(Material.AIR);


                }, 100);

            }

        }

    }

}
