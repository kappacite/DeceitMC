package fr.kappacite.deceit.objects;

import com.bringholm.nametagchanger.NameTagChanger;
import fr.kappacite.deceit.Deceit;
import fr.kappacite.deceit.role.DInfected;
import fr.kappacite.deceit.role.DPlayer;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Day {

    private int timer = 30;
    private int task;
    private int number = 0;

    private Game game;

    public void start(){

        this.number++;
        this.spawnBlood();
        this.spawnLight();
        this.spawnDoor();
        this.spawnAmmo();
        this.game.getObjective().spawnObjectives();

        for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
            entry.getValue().set("§bPhase: §6Jour " + number, 3);
        }

        for (Player gamePlayer : this.game.getGamePlayers()) {

            Deceit.getTitle().sendTitle(gamePlayer, "§aJour " + number, "", 20);
            DPlayer dPlayer = DPlayer.instance(gamePlayer);
            if(dPlayer.getRole() instanceof DInfected){
                DInfected role = (DInfected) dPlayer.getRole();
                if(role.isTransformed()){

                    role.setTransformed(false);
                    role.removeBlood(3);
                    if(role.getBlood() < 0){
                        role.removeBlood(role.getBlood());
                    }
                    NameTagChanger.INSTANCE.changePlayerName(gamePlayer, dPlayer.getPersonnage());
                    NameTagChanger.INSTANCE.resetPlayerSkin(gamePlayer);

                    Netherboard.instance().getBoard(gamePlayer).set("§CSang: §b" + role.getBlood() + "/3", 6);
                }
            }

        }

        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Deceit.getPlugin(Deceit.class), () -> {

            game.getObjective().detectObjectives();
            for(Map.Entry<Player, BPlayerBoard> entry : Netherboard.instance().getBoards().entrySet()){
                entry.getValue().set("§bTimer: §6" + timer + "§6s", 2);
            }

            for(Player player : game.getGamePlayers()){
                DPlayer dPlayer = DPlayer.instance(player);
                dPlayer.detectGaz();
            }

            if(this.timer == 0) {

                this.game.getNight().start();
                end();
            }
            this.timer--;

        },20,20);



    }

    private void end(){

        this.timer = 30;
        Bukkit.getScheduler().cancelTask(this.task);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getNumber() {
        return number;
    }

    private void spawnAmmo(){

        FileConfiguration config = Deceit.getFiles().getAmmoConfig();

        ArrayList<Location> allAmmo = new ArrayList<>();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            allAmmo.add(loc);
        }

        Collections.shuffle(allAmmo);
        int spawned = allAmmo.size()/2;

        for(int i = 0; i<spawned; i++){
            Location loc = allAmmo.get(i);
            loc.getBlock().setType(Material.STONE_BUTTON);
            Block block = loc.getBlock();
            BlockState bs = block.getState();
            Button button = (Button) bs.getData();
            button.setFacingDirection(BlockFace.UP);
            bs.setData(button);
            bs.update(true);
        }

    }

    private void spawnBlood(){

        FileConfiguration config = Deceit.getFiles().getBloodConfig();

        ArrayList<Location> allBlood = new ArrayList<>();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            allBlood.add(loc);
        }

        Collections.shuffle(allBlood);
        int spawned = allBlood.size()/2;

        for(int i = 0; i<spawned; i++){

            Location loc = allBlood.get(i);

            ArmorStand blood = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            blood.setHelmet(new ItemStack(Material.WATER_BUCKET));
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

        for(Location loc : allLight){
           loc.getBlock().setType(Material.GLOWSTONE);

        }

    }

    private void spawnDoor(){

        FileConfiguration config = Deceit.getFiles().getDoorConfig();

        for(String s : config.getConfigurationSection("" + this.number).getKeys(false)){
            Location loc = (Location) config.get("" + this.number + "." + s);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 1, 0).getBlock().setType(Material.BEDROCK);
            loc.add(0, 1, 0).getBlock().setType(Material.BEDROCK);
        }

    }

}
