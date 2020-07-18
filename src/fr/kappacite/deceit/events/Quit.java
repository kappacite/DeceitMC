package fr.kappacite.deceit.events;

import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.GameState;
import fr.kappacite.deceit.events.Join;
import fr.kappacite.deceit.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        if(GameState.isState(GameState.LOBBY)){
            Game game = Deceit.getGame();
            game.removePlayer(event.getPlayer());
        }

        if(GameState.isState(GameState.PREGAME)){

            Game game = Deceit.getGame();
            game.removePlayer(event.getPlayer());

            GameState.setState(GameState.LOBBY);
            Deceit.database().updateStatus("\"lobby\"", "\"online\"");
            Bukkit.getScheduler().cancelTask(Join.task);
        }

    }
}
