package fr.kappacite.deceit.role;

import java.util.ArrayList;

public class DInnocent extends DRole{

    @Override
    public String getName() {
        return "§aInnocent";
    }

    @Override
    public boolean canSeeHorror() {
        return false;
    }

    @Override
    public boolean canTakeBlood() {
        return false;
    }

    @Override
    public String getObjectif() {
        return "§7Éliminer toutes les terreurs.";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
