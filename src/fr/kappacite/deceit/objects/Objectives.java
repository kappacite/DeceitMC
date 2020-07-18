package fr.kappacite.deceit.objects;

import fr.kappacite.deceit.Deceit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Objectives {

    private ObjectivesType type;
    private int task;
    private Location location;
    private ItemType itemType;
    private int seconds = 10;
    private int step;
    private int currentStep;
    private boolean canTake = true;
    private static ArrayList<Objectives> objectives = new ArrayList<>();
    private static HashMap<Player, Objectives> object = new HashMap<>();

    enum ObjectivesType {
        CIBLE,
        ZONE,
        DOUBLE;
    }

    enum ItemType{
        ANTIDOTE,
        CAMERA,
        SCANNER,
        FLASHLIGHT,
        LETHAL,
        SHOTGUN;
    }

    private Objectives(){

    }

    public Location getLocation() {
        return location;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setType(ItemType type) {
        this.itemType = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static Objectives instance(){
        return new Objectives();
    }

    public void spawnObjectives(){

        FileConfiguration config = Deceit.getFiles().getObjectivesConfig();

        Game game = Deceit.getGame();
        this.objectives.clear();

        for(String s : config.getConfigurationSection("" + game.getDay().getNumber()).getKeys(false)){

            Location location = (Location) config.get("" + game.getDay().getNumber() + "." + s + ".loc");
            location.getBlock().setType(Material.BEACON);
            String type = config.getString("" + game.getDay().getNumber() + "." + s + ".type");

            Objectives objective = new Objectives();
            objective.setLocation(location);
            switch(type){
                case "ANTIDOTE":
                    objective.setType(ItemType.ANTIDOTE);
                    break;
                case "CAMERA":
                    objective.setType(ItemType.CAMERA);
                    break;
                case "SCANNER":
                    objective.setType(ItemType.SCANNER);
                    break;
            }

            this.objectives.add(objective);

        }

    }

    public void detectObjectives(){

            for(Player player :Deceit.getGame().getGamePlayers()) {

                for (Objectives objective : this.objectives) {

                    if (objective.canTake) {

                        if (objective.getLocation().distance(player.getLocation()) <= 1.7) {

                            if (objective.getItemType() == ItemType.ANTIDOTE) {

                                ItemStack antidote = new ItemBuilder(Material.BLAZE_ROD).displayname("§bAntidote").build();
                                player.getInventory().addItem(antidote);
                                objective.canTake = false;
                                objective.getLocation().getBlock().setType(Material.AIR);
                                return;
                            }

                            if (objective.getItemType() == ItemType.CAMERA) {

                                ItemStack camera = new ItemBuilder(Material.GOLD_SPADE).displayname("§6Camera §b(5)").build();
                                player.getInventory().addItem(camera);
                                objective.canTake = false;
                                objective.getLocation().getBlock().setType(Material.AIR);
                                return;
                            }

                            if (objective.getItemType() == ItemType.SCANNER) {

                                ItemStack camera = new ItemBuilder(Material.EMERALD).displayname("§aScanner").build();
                                player.getInventory().addItem(camera);
                                objective.canTake = false;
                                objective.getLocation().getBlock().setType(Material.AIR);
                                return;
                            }
                    }

                }
            }

            }
    }

}
