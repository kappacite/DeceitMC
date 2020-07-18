package fr.kappacite.deceit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Files {

    private File fuseFile;
    private FileConfiguration fuseConfig;

    private File fusePoseFile;
    private FileConfiguration fusePoseConfig;

    private File bloodFile;
    private FileConfiguration bloodConfig;

    private File lightFile;
    private FileConfiguration lightConfig;

    private File spawnFile;
    private FileConfiguration spawnConfig;

    private File objectivesFile;
    private FileConfiguration objectivesConfig;

    private File doorFile;
    private FileConfiguration doorConfig;

    private File ammoFile;
    private FileConfiguration ammoConfig;

    public void generateConfigFiles(){

        fuseFile = new File(Deceit.getDeceit().getDataFolder(), "fuse.yml");
        bloodFile = new File(Deceit.getDeceit().getDataFolder(), "blood.yml");
        lightFile = new File(Deceit.getDeceit().getDataFolder(), "light.yml");
        spawnFile = new File(Deceit.getDeceit().getDataFolder(), "spawn.yml");
        fusePoseFile = new File(Deceit.getDeceit().getDataFolder(), "fusepose.yml");
        objectivesFile = new File(Deceit.getDeceit().getDataFolder(), "objectives.yml");
        doorFile = new File(Deceit.getDeceit().getDataFolder(), "door.yml");
        ammoFile = new File(Deceit.getDeceit().getDataFolder(), "ammo.yml");

        if(!fuseFile.exists()){
            fuseFile.getParentFile().mkdirs();
            Deceit.getDeceit().saveResource("fuse.yml", false);
            Deceit.getDeceit().saveResource("fusepose.yml", false);
            Deceit.getDeceit().saveResource("blood.yml", false);
            Deceit.getDeceit().saveResource("spawn.yml", false);
            Deceit.getDeceit().saveResource("light.yml", false);
            Deceit.getDeceit().saveResource("objectives.yml", false);
            Deceit.getDeceit().saveResource("door.yml", false);
            Deceit.getDeceit().saveResource("ammo.yml", false);
        }

        fuseConfig = YamlConfiguration.loadConfiguration(fuseFile);
        bloodConfig = YamlConfiguration.loadConfiguration(bloodFile);
        lightConfig = YamlConfiguration.loadConfiguration(lightFile);
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        fusePoseConfig = YamlConfiguration.loadConfiguration(fusePoseFile);
        objectivesConfig = YamlConfiguration.loadConfiguration(objectivesFile);
        doorConfig = YamlConfiguration.loadConfiguration(doorFile);
        ammoConfig = YamlConfiguration.loadConfiguration(ammoFile);
    }

    public File getFuseFile() {
        return fuseFile;
    }

    public FileConfiguration getFuseConfig() {
        return fuseConfig;
    }

    public File getFusePoseFile() {
        return fusePoseFile;
    }

    public FileConfiguration getFusePoseConfig() {
        return fusePoseConfig;
    }

    public File getBloodFile() {
        return bloodFile;
    }

    public FileConfiguration getBloodConfig() {
        return bloodConfig;
    }

    public File getLightFile() {
        return lightFile;
    }

    public FileConfiguration getLightConfig() {
        return lightConfig;
    }

    public File getSpawnFile() {
        return spawnFile;
    }

    public FileConfiguration getSpawnConfig() {
        return spawnConfig;
    }

    public File getObjectivesFile() {
        return objectivesFile;
    }

    public FileConfiguration getObjectivesConfig() {
        return objectivesConfig;
    }

    public File getDoorFile() {
        return doorFile;
    }

    public FileConfiguration getDoorConfig() {
        return doorConfig;
    }

    public File getAmmoFile() {
        return ammoFile;
    }

    public FileConfiguration getAmmoConfig() {
        return ammoConfig;
    }
}
