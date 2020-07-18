package fr.kappacite.deceit.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Button;


public class TakeAmmo implements Listener {

    @EventHandler
    public void onTakeAmmo(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){

            event.setCancelled(true);

            if(event.getClickedBlock().getType() == Material.STONE_BUTTON){

                event.getClickedBlock().setType(Material.AIR);
                event.getClickedBlock().getState().setType(Material.AIR);
                event.getClickedBlock().getState().update(true);

                ItemStack item = player.getInventory().getItem(0);

                int start = item.getItemMeta().getDisplayName().indexOf("(")+1;
                int finish = item.getItemMeta().getDisplayName().indexOf(")");
                int ammo = Integer.parseInt(item.getItemMeta().getDisplayName().substring(start, finish));
                ammo+=9;

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§cPistolet §b(" + (ammo) + ")");
                item.setItemMeta(meta);

            }
        }

    }

}
