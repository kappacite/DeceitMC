package fr.kappacite.deceit;

import com.bringholm.nametagchanger.NameTagChanger;
import fr.kappacite.deceit.commands.*;
import fr.kappacite.deceit.events.*;
import fr.kappacite.deceit.objects.Game;
import fr.kappacite.deceit.objects.Skin;
import fr.kappacite.deceit.objects.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Deceit extends JavaPlugin {

    private static Title title = new Title();
    private static Game game = new Game();
    private static Deceit deceit;
    private static Files files = new Files();

    private static Database database;

    public void onEnable(){

        deceit = this;

        String host = get("database.host").toString();
        String dbName = get("database.name").toString();
        String user = get("database.user").toString();
        String password = get("database.password").toString();

        database = new Database("jdbc:mysql://", host, dbName, user, password);
        database.connection();

        files.generateConfigFiles();

        if (!NameTagChanger.INSTANCE.isEnabled()) {
            NameTagChanger.INSTANCE.setPlugin(this);
            NameTagChanger.INSTANCE.enable();
        }

        GameState.setState(GameState.LOBBY);

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new BloodDrink(), this);
        manager.registerEvents(new TakeFuse(), this);
        manager.registerEvents(new Join(), this);
        manager.registerEvents(new Quit(), this);
        manager.registerEvents(new CustomItem(), this);
        manager.registerEvents(new Transform(), this);
        manager.registerEvents(new MicroEvents(), this);
        manager.registerEvents(new TakeAmmo(), this);
        manager.registerEvents(new DoorOpen(), this);

        getCommand("addspawn").setExecutor(new AddSpawn());
        getCommand("addfuse").setExecutor(new AddFuse());
        getCommand("addblood").setExecutor(new AddBlood());
        getCommand("addlight").setExecutor(new AddLight());
        getCommand("addfusepose").setExecutor(new AddFusePose());
        getCommand("addobject").setExecutor(new AddObject());
        getCommand("adddoor").setExecutor(new AddDoor());
        getCommand("addammo").setExecutor(new AddAmmo());
        getCommand("deceit").setExecutor(new DeceitCommand());

        initialiseSkin();
        saveDefaultConfig();

        database.createTable();
        database.updateStatus("\"lobby\"", "\"online\"");

    }

    public void onDisable(){
        database.updateStatus("\"down\"", "\"offline\"");
        database.disconnect();
    }

    public static Deceit getDeceit() {
        return deceit;
    }

    public static Title getTitle() {
        return title;
    }

    public static Game getGame() {
        return game;
    }

    public static Files getFiles() { return files; }

    private void initialiseSkin(){

        Skin.getSkin("Werewolf");
        Skin.werewolf = Skin.getSkin("Werewolf");

    }

    public static Database database() {
        return database;
    }

    public static Object get(String path){
        return getDeceit().getConfig().get(path);
    }

}
