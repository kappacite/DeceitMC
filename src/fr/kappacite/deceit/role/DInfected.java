package fr.kappacite.deceit.role;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DInfected extends DRole{

    public static ArrayList<Player> infected = new ArrayList<>();

    private double blood;
    private boolean isTransformed = false;
    private boolean canEat = false;

    @Override
    public String getName() {
        return "§cInfecté";
    }

    @Override
    public boolean canSeeHorror() {
        return true;
    }

    @Override
    public boolean canTakeBlood() {
        return this.blood<3;
    }

    @Override
    public String getObjectif() {
        return "§cÉliminer tous les innocents.";
    }

    @Override
    public String getDescription() {
        return null;
    }

    public void addBlood(double blood){
        this.blood+=blood;
    }

    public void removeBlood(double blood){
        this.blood-=blood;
    }

    public double getBlood() {
        return blood;
    }

    public boolean isTransformed() {
        return isTransformed;
    }

    public void setTransformed(boolean transformed) {
        isTransformed = transformed;
        this.canEat = transformed;
    }

    public void setCanEat(boolean canEat) {
        this.canEat = canEat;
    }

    public boolean isCanEat() {
        return canEat;
    }
}
