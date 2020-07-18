package fr.kappacite.deceit.events;

import com.bringholm.nametagchanger.NameTagChanger;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.objects.Skin;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Transform implements Listener {

    @EventHandler
    public void onTransform(PlayerInteractEvent event){

        Player player = event.getPlayer();
        DPlayer dPlayer = DPlayer.instance(player);
        ItemStack currentItem = event.getItem();

        if(currentItem != null && currentItem.getType() == Material.REDSTONE){
            if(dPlayer.getRole() instanceof DInfected){
                DInfected role = (DInfected) dPlayer.getRole();

                if(Deceit.getGame().isDay()) return;

                if(role.isTransformed()){
                    role.setTransformed(false);
                    role.removeBlood(1);
                    if(role.getBlood() < 0){
                        role.removeBlood(role.getBlood());
                    }

                    if(Deceit.getGame().getNight().isRage()){
                        role.removeBlood(role.getBlood());
                        role.addBlood(3);
                    }

                    player.removePotionEffect(PotionEffectType.SPEED);
                    NameTagChanger.INSTANCE.resetPlayerSkin(player);
                    NameTagChanger.INSTANCE.changePlayerName(player, dPlayer.getPersonnage());
                    NameTagChanger.INSTANCE.updatePlayer(player);
                    Netherboard.instance().getBoard(player).set("§CSang: §b" + role.getBlood() + "/3", 6);
                    Deceit.getTitle().sendTitle(player, "§cVous êtes", "§cdétransformés", 10);
                }else{
                    if(role.getBlood() == 3 || Deceit.getGame().getNight().isRage()){

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1));
                        role.setTransformed(true);
                        Deceit.getTitle().sendTitle(player, "§cVous êtes", "§ctransformés", 20);
                        Skin.getSkin("Werewolf");
                        NameTagChanger.INSTANCE.setPlayerSkin(player, Skin.getSkin("Werewolf"));
                        NameTagChanger.INSTANCE.changePlayerName(player, "§kczjefbjh");
                        NameTagChanger.INSTANCE.updatePlayer(player);

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 3f, 1);
                        }

                    }
                }
            }
        }

    }

    @EventHandler
    public void onEat(PlayerMoveEvent event){

        Player player = event.getPlayer();
        DPlayer dPlayer = DPlayer.instance(player);

        if(dPlayer.getRole() instanceof DInfected){

            DInfected role = (DInfected) dPlayer.getRole();

            if(role.isTransformed()){

                for(Entity entity : player.getNearbyEntities(3, 0, 3)){
                    if(entity instanceof Player){

                        Player target = (Player) entity;
                        DPlayer dTarget = DPlayer.instance(target);

                        if(dTarget.getRole() instanceof DInfected) return;

                        if(player.getLocation().distance(entity.getLocation()) <= 1){
                            if(player.hasLineOfSight(entity)){

                                if(!role.isCanEat()) return;

                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200, false, false));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200, false, false));

                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200, false, false));
                                target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200, false, false));

                                target.spawnParticle(Particle.REDSTONE, target.getLocation(), 30);
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 3f, 1);
                                }

                                Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), () -> {
                                    player.removePotionEffect(PotionEffectType.SLOW);
                                    player.removePotionEffect(PotionEffectType.JUMP);

                                    dTarget.die(false, true);
                                }, 30);

                                role.setCanEat(false);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), () -> {

                                    if(!Deceit.getGame().getNight().isRage()) {

                                        role.setTransformed(false);
                                        role.removeBlood(3);
                                        if (role.getBlood() < 0) {
                                            role.removeBlood(role.getBlood());
                                        }
                                        Netherboard.instance().getBoard(player).set("§CSang: §b" + role.getBlood() + "/3", 6);
                                        Deceit.getTitle().sendTitle(player, "§cVous êtes", "§cdétransformés", 10);

                                        player.removePotionEffect(PotionEffectType.SPEED);
                                        NameTagChanger.INSTANCE.resetPlayerSkin(player);
                                        NameTagChanger.INSTANCE.changePlayerName(player, dPlayer.getPersonnage());
                                        NameTagChanger.INSTANCE.updatePlayer(player);
                                    }else{
                                        role.setCanEat(true);
                                    }
                                }, 100);

                            }

                        }

                    }

                }

            }

        }
    }


}
