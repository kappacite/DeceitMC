package fr.kappacite.deceit.objects;

import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DInnocent;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class Night {

    private int neededFuse;
    private int placedFuse = 0;
    private boolean isRage = false;
    private boolean canOpenDoor = false;
    private boolean isFinish = true;

    private Game game;

    private int number = 0;

    private int timer = 60;
    private int task;

    private int doorTask;

    public void start(){

        this.number++;
        this.spawnFuse();
        this.spawnLight();
        this.spawnFusePose();
        this.neededFuse = 4-this.number;
        this.game.setDay(false);

        for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
            entry.getValue().set("§bPhase: §6Nuit " + number, 3);
        }

        this.game.getGamePlayers().forEach(p -> {
            Deceit.getTitle().sendTitle(p, "§CNuit " + this.number, "§cFusibles: " + this.neededFuse, 20);
        });

        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getPlugin(Deceit.class), () -> {

            game.getObjective().detectObjectives();
            for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
                entry.getValue().set("§bTimer: §6" + timer + "§6s", 2);
            }

            for(Player player : game.getGamePlayers()){
                DPlayer dPlayer = DPlayer.instance(player);
                dPlayer.detectGaz();
            }

            if(this.timer == 0 && !this.isRage){
                this.isRage = true;

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Deceit.getTitle().sendTitle(onlinePlayer, "§cLes Terreurs sont enragées...", "§cFUYEZ !", 20);

                    DPlayer dPlayer = DPlayer.instance(onlinePlayer);
                    if(dPlayer.getRole() instanceof DInfected){
                        Netherboard.instance().getBoard(onlinePlayer).set("§CSang: §b3/3", 6);
                    }

                }

                this.timer = 60;

                if(number == 3){
                    this.game.end(false);
                    Bukkit.getScheduler().cancelTask(this.task);
                    return;
                }

            }else if(timer == 0){
                this.game.end(false);
                Bukkit.getScheduler().cancelTask(this.task);
                return;
            }

            timer--;

        },20,20);


    }

    private void end(){
        Bukkit.getScheduler().cancelTask(this.task);
        this.timer = 60;
        this.placedFuse = 0;
        this.isRage = false;
        this.spawnDoor();
        this.game.getDay().start();
    }

    public void addFuse(){

        this.placedFuse++;
        if(this.placedFuse == this.neededFuse) {

            if(this.number != 3){

                Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), this::end, 200);

                for (Player gamePlayer : this.game.getGamePlayers()) {
                    DPlayer dPlayer = DPlayer.instance(gamePlayer);
                    dPlayer.setFuse(false);
                    this.game.setDay(true);
                    Deceit.getTitle().sendTitle(gamePlayer, "§A10 secondes", "§aavant le jour..", 20);
                }

            } else {

                for (Player gamePlayer : this.game.getGamePlayers()) {
                    Deceit.getTitle().sendTitle(gamePlayer, "§ADirigez-vous", "§avers la sortie !", 20);
                    this.canOpenDoor = true;
                    startDoorDetection();
                }

            }

        }

    }

    private void startDoorDetection(){

        this.doorTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getDeceit(), () -> {

            ArrayList<Player> exitPlayers = new ArrayList<>();

            for (Player gamePlayer : this.game.getGamePlayers()) {
                DPlayer dPlayer = DPlayer.instance(gamePlayer);
                if(dPlayer.getRole() instanceof DInnocent){

                    Location exit = (Location) Deceit.getDeceit().getConfig().get("exit");

                    Deceit.getTitle().sendActionBar(gamePlayer, "§a" + Math.floor(gamePlayer.getLocation().distance(exit)) + " §5Blocks");

                    if(Math.floor(exit.distance(gamePlayer.getLocation())) == 0){

                        Deceit.getTitle().sendTitle(gamePlayer, "§eFélicitation !", "§aVous vous êtes échappé", 20);
                        exitPlayers.add(gamePlayer);
                        gamePlayer.setGameMode(GameMode.SPECTATOR);
                    }

                }

            }

            this.game.getGamePlayers().removeAll(exitPlayers);

            boolean hasInnocentWin = true;

            for (Player gamePlayer : this.game.getGamePlayers()) {
                DPlayer dPlayer = DPlayer.instance(gamePlayer);
                if(dPlayer.getRole() instanceof DInnocent){
                    hasInnocentWin = false;
                }
            }

            if(hasInnocentWin && this.isFinish){
                this.isFinish = false;
                Bukkit.getScheduler().cancelTask(this.doorTask);
                this.game.end(true);
            }


        }, 20, 20);

    }

    private void spawnFuse(){

        FileConfiguration config = Deceit.getFiles().getFuseConfig();

        ArrayList<Location> allFuses = new ArrayList<>();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            allFuses.add(loc);
        }

        Collections.shuffle(allFuses);
        int spawned = allFuses.size()/2;

        for(int i = 0; i<spawned; i++){

            Location loc = allFuses.get(i);

            loc.getBlock().setType(Material.FENCE);
            loc.add(0,1,0).getBlock().setType(Material.TORCH);
        }

    }

    private void spawnLight(){

        FileConfiguration config = Deceit.getFiles().getLightConfig();

        ArrayList<Location> allLight = new ArrayList<>();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            allLight.add(loc);
        }

        Collections.shuffle(allLight);
        int spawned = 50;

        for(Location loc : allLight){

            if(new Random().nextInt(100) >= spawned) {
                loc.getBlock().setType(Material.GLOWSTONE);
            }else{
                loc.getBlock().setType(Material.CLAY);
            }

        }

    }

    private void spawnFusePose(){

        FileConfiguration config = Deceit.getFiles().getFusePoseConfig();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            ((Location) config.get(this.number + "." + s)).getBlock().setType(Material.SPONGE);
        }
    }

    public Location getNearestFusePose(Location pLoc){

        ArrayList<Location> locs = new ArrayList<>();

        FileConfiguration config = Deceit.getFiles().getFusePoseConfig();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){

            Block block = ((Location) config.get(this.number + "." + s)).getBlock();

            if(block.getType() != Material.BEDROCK) locs.add(block.getLocation());

        }

        Location loc = locs.get(0);

        Double distance = 0d;
        distance = locs.get(0).distance(pLoc);

        for(Location l : locs){
            if(pLoc.distance(l) <= distance) loc = l;
        }

        return loc;
    }

    private void spawnDoor(){

        FileConfiguration config = Deceit.getFiles().getDoorConfig();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            loc.getBlock().setType(Material.AIR);
            loc.add(0, -1, 0).getBlock().setType(Material.AIR);
            loc.add(0, -1, 0).getBlock().setType(Material.AIR);
        }

    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getNumber() {
        return number;
    }

    public boolean isRage() {
        return isRage;
    }

    public boolean isCanOpenDoor() {
        return canOpenDoor;
    }

    public void setCanOpenDoor(boolean canOpenDoor) {
        this.canOpenDoor = canOpenDoor;
    }
}
