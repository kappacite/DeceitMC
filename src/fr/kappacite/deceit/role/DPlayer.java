package fr.kappacite.deceit.role;

import com.bringholm.nametagchanger.NameTagChanger;
import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DPlayer {

    private static HashMap<Player, DPlayer> players = new HashMap<>();

    public static DPlayer instance(Player player){

        if(players.containsKey(player)) return players.get(player);
        DPlayer DPlayer = new DPlayer();
        players.put(player, DPlayer);
        return players.get(player);

    }

    private DPlayer(){ }

    private DRole role;
    private Player player;
    private String personnage;
    private boolean hasFuse = false;
    private int detectionTask;
    private boolean isDead;
    private int respawnTask;
    private int respawnTimer = 20;
    private int gazTime = 10;
    private boolean canVote = false;
    private boolean willDie = false;
    private int currentVote;
    private List<Player> hasVoted = new ArrayList<>();

    public DRole getRole() {
        return role;
    }

    public void setRole(DRole role) {
        this.role = role;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getPersonnage() {
        return personnage;
    }

    public void setPersonnage(String personnage) {
        this.personnage = personnage;
    }

    public boolean hasFuse() {
        return hasFuse;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getVote() {
        return 5-Deceit.getGame().getDay().getNumber();
    }

    public int getCurrentVote() {
        return currentVote;
    }

    public boolean isCanVote() {
        return canVote;
    }

    public boolean isWillDie() {
        return willDie;
    }

    public void setFuse(boolean hasFuse){
        this.hasFuse = hasFuse;
        if(this.hasFuse){
            startDetection();
        }else{
            endDetection();
        }
    }

    private void startDetection(){

        this.detectionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getDeceit(), () -> {

            Location loc = Deceit.getGame().getNight().getNearestFusePose(this.player.getLocation());
            Deceit.getTitle().sendActionBar(this.player, "§a" + Math.floor(this.player.getLocation().distance(loc)) + " §5Blocks.");

        }, 20, 20);

    }

    public void detectGaz(){

        int floor = Deceit.getGame().getDay().getNumber();
        int limit = Deceit.getDeceit().getConfig().getInt(floor + "-limit");

        if(this.player.getLocation().getY() < limit && !this.isDead){
            Deceit.getTitle().sendTitle(this.player, "§c" + this.gazTime, "", 20);
            this.gazTime--;

            if(gazTime == 0){
                die(false, true);
            }

        }else{
            this.gazTime = 15;
        }

    }

    public void die(boolean canVote, boolean willDie){

        this.isDead = true;
        this.canVote = canVote;
        this.willDie = willDie;
        this.player.teleport(this.player.getLocation().add(0, -1, 0));
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200, false, false));
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 100, false, false));
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 100, false, false));

        if(!willDie) this.respawnTimer = 10;

        this.respawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getDeceit(), () -> {

            Deceit.getTitle().sendTitle(this.player, "§4" + this.respawnTimer, "", 20);

            if(this.respawnTimer == 0){

                if(!this.willDie){
                    this.respawn();
                    if(this.getRole() instanceof DInfected) ((DInfected) this.getRole()).removeBlood(1);
                }else{
                    Deceit.getGame().removePlayer(this.player);
                    this.player.setGameMode(GameMode.SPECTATOR);
                    Deceit.getTitle().sendTitle(this.player, "§4Vous êtes MORT", "", 20);
                    Bukkit.getScheduler().cancelTask(this.respawnTask);
                    this.player.removePotionEffect(PotionEffectType.SLOW);
                    this.player.removePotionEffect(PotionEffectType.JUMP);
                    this.player.removePotionEffect(PotionEffectType.BLINDNESS);
                }

                this.respawnTimer = 20;
            }

            this.respawnTimer--;
        }, 20, 20);


    }

    public void respawn(){

        this.isDead = false;
        this.canVote = false;
        this.hasVoted.clear();
        this.currentVote = 0;
        this.player.teleport(this.player.getLocation().add(0, +1, 0));
        this.player.removePotionEffect(PotionEffectType.BLINDNESS);
        this.player.removePotionEffect(PotionEffectType.JUMP);
        this.player.removePotionEffect(PotionEffectType.SLOW);
        NameTagChanger.INSTANCE.changePlayerName(this.player, this.personnage);
        Bukkit.getScheduler().cancelTask(this.respawnTask);
    }

    public void addVoted(Player player){

        if(this.hasVoted.contains(player)) return;

        this.hasVoted.add(player);
        this.currentVote++;
        if(this.currentVote == getVote()){
            Deceit.getGame().getGamePlayers().remove(player);
            player.setGameMode(GameMode.SPECTATOR);
            Deceit.getTitle().sendTitle(player, "§4Vous êtes MORT", "", 20);
            Deceit.getGame().removePlayer(player);
        }
    }

    private void endDetection(){
        Bukkit.getScheduler().cancelTask(this.detectionTask);
    }

}
