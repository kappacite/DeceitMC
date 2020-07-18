package fr.kappacite.deceit.events;

import com.bringholm.nametagchanger.NameTagChanger;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DPlayer;
import net.minecraft.server.v1_9_R1.EntityArrow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class CustomItem implements Listener {

    private static HashMap<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onInteractOnplayer(PlayerInteractAtEntityEvent event){

        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();

        if(rightClicked instanceof Player){

            Player target = (Player) rightClicked;
            DPlayer dTarget = DPlayer.instance(target);
            if(player.getInventory().getItemInMainHand() == null) return;

            if(dTarget.getRole() instanceof DInfected && ((DInfected) dTarget.getRole()).isTransformed()) return;

            if(player.getInventory().getItemInMainHand().getType() == Material.EMERALD){
                player.getInventory().removeItem(player.getInventory().getItemInMainHand());
                player.sendMessage("§aScan en cours...");

                Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), () ->
                        player.sendMessage("§a" + target.getName() + " est " + dTarget.getRole().getName() + " §A!"), 60);
            }

            if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND){
                if(dTarget.isDead() && !dTarget.isWillDie()){
                    dTarget.respawn();
                }
            }

            if(player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD){

                if(dTarget.isDead() && dTarget.isWillDie()){
                    dTarget.respawn();
                    player.getInventory().removeItem(player.getInventory().getItemInMainHand());
                }

            }

            if(player.getInventory().getItemInMainHand().getType() == Material.NETHER_STAR){

                if(!dTarget.isDead()){
                    dTarget.die(false, true);
                    player.getInventory().removeItem(player.getInventory().getItemInMainHand());
                }

            }

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack usedItem = event.getItem();

        if (usedItem != null) {

            if (usedItem.getType() == Material.STICK) {

                Integer start = usedItem.getItemMeta().getDisplayName().indexOf("(")+1;
                Integer finish = usedItem.getItemMeta().getDisplayName().indexOf(")");
                Integer ammo = Integer.parseInt(usedItem.getItemMeta().getDisplayName().substring(start, finish));
                if(ammo != 0){
                    ItemMeta meta = usedItem.getItemMeta();
                    meta.setDisplayName("§cPistolet §b(" + (ammo-1) + ")");
                    usedItem.setItemMeta(meta);
                    Vector playerDirection = player.getLocation().getDirection();
                    EntityArrow arrow = ((CraftArrow) player.launchProjectile(Arrow.class, playerDirection)).getHandle();
                    arrow.knockbackStrength = 0;
                    arrow.fallDistance = 10000;
                    arrow.shooter = ((CraftPlayer) player).getHandle();
                    arrow.setInvisible(true);
                    arrow.fromPlayer = EntityArrow.PickupStatus.DISALLOWED;
                }

            }

            if (usedItem.getType() == Material.GOLD_SPADE) {

                Integer start = usedItem.getItemMeta().getDisplayName().indexOf("(")+1;
                Integer ammo = Integer.parseInt(usedItem.getItemMeta().getDisplayName().substring(start, start+1))-1;

                ItemMeta meta = usedItem.getItemMeta();
                meta.setDisplayName("§6Camera §b(" + ammo + ")");
                usedItem.setItemMeta(meta);

                if(ammo == 0){
                    player.getInventory().removeItem(usedItem);
                }

                for (Entity ent : player.getNearbyEntities(3, 0, 3)) {
                    if (ent instanceof Player) {
                        Player target = (Player) ent;
                        DPlayer dPlayer = DPlayer.instance(target);
                        if (player.hasLineOfSight(target)) {
                            if(dPlayer.getRole() instanceof DInfected && ((DInfected) dPlayer.getRole()).isTransformed()){
                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
                            }
                        }
                    }

                }

            }

        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){

        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        DPlayer dPlayer = DPlayer.instance(player);
        Entity damager = event.getDamager();

        event.setCancelled(true);

        if(dPlayer.isDead()){
            if(dPlayer.isCanVote()){
                NameTagChanger.INSTANCE.changePlayerName(player, dPlayer.getPersonnage() + " §c" + dPlayer.getCurrentVote() + "/" + dPlayer.getVote());
                dPlayer.addVoted(player);
                return;
            }
        }

        boolean isDead = false;

        if(damager instanceof Player){

            if(dPlayer.isDead()) return;
            DPlayer dDamager = DPlayer.instance((Player) damager);
            if(dDamager.isDead()) return;

            if((System.currentTimeMillis()/1000) - cooldown.getOrDefault(player, 0L) < 1) return;

            if(event.getEntityType() == EntityType.PLAYER){

                Player pDamage = (Player) damager;
                ItemStack item = pDamage.getInventory().getItemInMainHand();

                if(item.getType() != Material.IRON_SWORD){
                    return;
                }
            }

            if((player.getHealth()-5) <= 0){
                player.setHealth(20);
                isDead = true;
            }else{
                player.setHealth(player.getHealth()-5);
                player.setNoDamageTicks(20);
                cooldown.put(player, (System.currentTimeMillis()/1000));
            }
        }else{
            if(damager instanceof Projectile){
                Projectile projectile = (Projectile) damager;
                if(projectile.getShooter() instanceof Player){
                    DPlayer dDamager = DPlayer.instance((Player) projectile.getShooter());
                    if(dDamager.isDead()) return;
                    if(dPlayer.isDead()) return;
                }
            }

            if((player.getHealth()-2.5) <= 0){
                player.setHealth(20);
                isDead = true;
            }else{
                player.setHealth(player.getHealth()-2.5);
            }
        }

        if(isDead){
            if(Deceit.getGame().isDay()){
                dPlayer.die(true, false);
                NameTagChanger.INSTANCE.changePlayerName(player, dPlayer.getPersonnage() + " §c0/" + dPlayer.getVote());
            }else{
                dPlayer.die(false, false);
            }
        }

    }

    @EventHandler
    public void onHit(ProjectileHitEvent event){

        if(event.getEntity() instanceof Arrow){
            event.getEntity().remove();
        }
    }

}
