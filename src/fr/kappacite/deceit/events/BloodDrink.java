package fr.kappacite.deceit.events;

import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.objects.Game;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class BloodDrink implements Listener {

    @EventHandler
    public void onDrink(PlayerInteractAtEntityEvent event){

        Player player = event.getPlayer();
        DPlayer dPlayer = DPlayer.instance(player);
        Entity entity = event.getRightClicked();
        Game game = Deceit.getGame();

        if(entity instanceof ArmorStand){

            event.setCancelled(true);

            if(!(dPlayer.getRole() instanceof DInfected)) return;

            ArmorStand armorStand = (ArmorStand) entity;
            if(armorStand.getHelmet().getType() == Material.DIAMOND_HELMET){

                DInfected infected = (DInfected) dPlayer.getRole();

                if(infected.canTakeBlood()){
                    if(game.isDay()){
                        infected.addBlood(1);
                        if(infected.getBlood() >= 3){
                            infected.removeBlood(infected.getBlood());
                            infected.addBlood(3);
                        }
                    }else{
                        infected.addBlood(0.5);
                        if(infected.getBlood() >= 3){
                            infected.removeBlood(infected.getBlood());
                            infected.addBlood(3);
                        }
                    }

                    armorStand.getLocation().getWorld().spawnEntity(armorStand.getLocation(), EntityType.ARMOR_STAND);
                    armorStand.remove();
                    Netherboard.instance().getBoard(player).set("§CSang: §b" + infected.getBlood() + "/3", 6);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {


                        double sourceVolume = Math.max(0.0, Math.min(0.3, 1.0));
                        double rolloffDistance = Math.max(16, 16 * 0.3);
                        double distance = player.getLocation().distance(armorStand.getLocation());

                        Double volumeOfSoundAtPlayer = sourceVolume * ( 0.8 - distance / rolloffDistance );

                        onlinePlayer.playSound(armorStand.getLocation(), Sound.ENTITY_GENERIC_DRINK,
                                Float.parseFloat(volumeOfSoundAtPlayer.toString()), 1);
                    }
                }


            }

        }

    }


}
