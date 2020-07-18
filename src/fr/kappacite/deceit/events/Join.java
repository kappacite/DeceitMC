package fr.kappacite.deceit.events;

import com.avaje.ebean.event.BeanPersistListener;
import com.bringholm.nametagchanger.NameTagChanger;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.GameState;
import fr.kappacite.deceit.objects.Game;
import fr.kappacite.deceit.objects.Title;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class Join implements Listener {

    private int timer = 15;
    public static int task;

    @EventHandler
    public void onPrejoin(AsyncPlayerPreLoginEvent event){

        if(GameState.isState(GameState.PREGAME)){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cLa partie commence, vous ne pouvez pas rejoindre !");
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        DPlayer dPlayer = DPlayer.instance(player);
        Game game = Deceit.getGame();
        Location location = (Location) Deceit.getDeceit().getConfig().get("spawn");

        game.getDay().setGame(game);
        game.getNight().setGame(game);

        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.teleport(location);

        BPlayerBoard board = Netherboard.instance().createBoard(player, "§CDeceitMC");

        if(GameState.isState(GameState.LOBBY)){

            game.addPlayer(player);
            board.set("§8=============", 0);
            board.set("§bÉtat: §6Recherche de joueurs...", 1);
            board.set("§bTimer: §6Partie non démarrée", 2);
            board.set("§bPhase: §6Jour", 3);
            board.set("§bRole: §6Non distribué", 4);
            board.set("§r§8=============", 5);
            board.set("§bFusible: §6Non", 7);
            board.set("§3§8=============", 8);

            player.getNearbyEntities(100, 100, 100).forEach(e -> {
                if(e.getType() == EntityType.ARMOR_STAND) e.remove();
            });

            if(Bukkit.getOnlinePlayers().size() == 2){

                GameState.setState(GameState.PREGAME);
                Deceit.database().updateStatus("\"pregame\"", "\"online\"");


                for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
                    entry.getValue().set("§bÉtat: §6Démarrage", 1);
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Deceit.getTitle().sendTitle(onlinePlayer, "§eDébut de la partie dans",  "§e" + timer + "s", 20);
                }

                task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getPlugin(Deceit.class), () -> {

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        Netherboard.instance().getBoard(onlinePlayer).set("§bTimer: §6" + timer +"s", 2);
                    }

                    if(timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1){

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            Deceit.getTitle().sendTitle(onlinePlayer, "§eDébut de la partie dans",  "§e" + timer + "s", 20);
                        }
                    }

                    if(timer == 0){
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            Deceit.getTitle().sendTitle(onlinePlayer, "§eLa partie commence !",  "Bonne chance !", 20);
                        }
                        game.start();
                        GameState.setState(GameState.GAME);
                        Deceit.database().updateStatus("\"game\"", "\"online\"");
                        Bukkit.getScheduler().cancelTask(task);

                        for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
                            entry.getValue().set("§bÉtat: §6En cours", 1);
                        }
                    }

                    timer--;

                }, 20, 20);

            }

        }

        if(GameState.isState(GameState.GAME)){

            player.setGameMode(GameMode.SPECTATOR);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(player);
            }

        }


    }


}
