package fr.kappacite.deceit.objects;

import com.bringholm.nametagchanger.NameTagChanger;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.GameState;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DInnocent;
import fr.kappacite.deceit.role.DPlayer;
import fr.kappacite.deceit.role.DRole;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Game {

    private ArrayList<Player> gamePlayers = new ArrayList<>();

    private Day day;
    private Night night;
    private Objectives objective;
    private boolean isDay = true;
    private boolean hasStarted = false;

    public Game(){
        this.day = new Day();
        this.night = new Night();
        this.objective = Objectives.instance();
    }

    public void start(){

        this.distribRoleAndPersonnage();
        this.getDay().start();
        this.spawnPlayer();
        this.hasStarted = true;

    }

    public void end(boolean isInnocentWinner){

        if(isInnocentWinner){
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Deceit.getTitle().sendTitle(onlinePlayer, "§7Les §aInnocents §7ont gagnés !", "", 20);
            }
        }else{
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Deceit.getTitle().sendTitle(onlinePlayer, "§7Les §cTerreurs §7ont gagnés !", "", 20);
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Deceit.getDeceit(), Bukkit::shutdown, 100);

    }

    public Day getDay() {
        return day;
    }

    public Night getNight() {
        return night;
    }

    public Objectives getObjective() {
        return objective;
    }

    private void distribRoleAndPersonnage(){

        ArrayList<DRole> roles = new ArrayList<>();
        roles.add(new DInfected()); roles.add(new DInfected());
        roles.add(new DInnocent()); roles.add(new DInnocent());
        roles.add(new DInnocent()); roles.add(new DInnocent());

        ArrayList<String> personnages = new ArrayList<>();
        personnages.addAll(Arrays.asList("Hans", "Alex", "Rachel", "Chang", "Nina", "Lisa"));

        Collections.shuffle(personnages);
        Collections.shuffle(roles);

        gamePlayers.forEach(p -> {

            DPlayer dPlayer = DPlayer.instance(p);
            DRole role = roles.get(0);
            String personnage = personnages.get(0);

            dPlayer.setPersonnage(personnage);
            dPlayer.setRole(role);
            dPlayer.setPlayer(p);

            Deceit.getTitle().sendTitle(p, "§7Vous êtes " + role.getName() + "§7.", role.getObjectif(), 20);
            Deceit.getTitle().sendActionBar(p, "§5Vous jouez §b" + personnage + "§5.");

            p.setPlayerListName(personnage);

            BPlayerBoard board = Netherboard.instance().getBoard(p);
            board.set("§bRole: " + role.getName(), 4);

            if(role instanceof DInfected){

                ItemStack transform = new ItemBuilder(Material.REDSTONE).displayname("§cSe transformer").build();

                p.getInventory().setItem(8, transform);

                if(role.canSeeHorror()){
                    board.set("§CSang: §b0/3", 6);
                    board.set("§3§8=============", 8);
                }

                DInfected.infected.add(p);

            }

            ItemStack gun = new ItemBuilder(Material.STICK).displayname("§cPistolet §b(9)").build();
            ItemStack sword = new ItemBuilder(Material.IRON_SWORD).displayname("§aCouteau").build();
            ItemStack revive = new ItemBuilder(Material.DIAMOND).displayname("§bRelever un joueur").build();

            p.getInventory().addItem(gun);
            p.getInventory().addItem(sword);
            p.getInventory().setItem(7, revive);


            NameTagChanger.INSTANCE.changePlayerName(p, personnage);
            NameTagChanger.INSTANCE.updatePlayer(p);

            personnages.remove(personnage);
            roles.remove(role);

        });

        DInfected.infected.get(0).sendMessage("Ton allier est §c" + DInfected.infected.get(1).getName());
        DInfected.infected.get(1).sendMessage("Ton allier est §c" + DInfected.infected.get(0).getName());

    }

    private void spawnPlayer(){

        FileConfiguration config = Deceit.getFiles().getSpawnConfig();

        ArrayList<Location> allSpawn = new ArrayList<>();

        for(String s : config.getKeys(false)){
            Location loc = (Location) config.get(s);
            allSpawn.add(loc);
        }

        Collections.shuffle(allSpawn);
        int id = 0;

        for(Player p : gamePlayers){

            Location loc = allSpawn.get(id);
            p.teleport(loc);
            id++;
        }

    }
    


    public void addPlayer(Player player){
        this.gamePlayers.add(player);
    }

    public void removePlayer(Player player){

        this.gamePlayers.remove(player);
        System.out.println(this.gamePlayers.size());
        if(this.gamePlayers.size() == 1){
            if(GameState.isState(GameState.GAME)){

                Player p = gamePlayers.get(0);
                DPlayer dPlayer = DPlayer.instance(p);

                if(dPlayer.getRole() instanceof DInfected){
                    end(false);
                }else{
                    end(true);
                }

            }
        }
    }

    public ArrayList<Player> getGamePlayers() {
        return gamePlayers;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public boolean isDay() {
        return isDay;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }
}
