package fr.kappacite.deceit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MicroEvents implements Listener {

    @EventHandler
    public void onFood(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){ event.setCancelled(true); }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){ event.setCancelled(true); }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);
    }

}
