package fr.kappacite.deceit.objects;

import com.bringholm.nametagchanger.NameTagChanger;
import org.bukkit.entity.Player;

public class Skin {

    public static com.bringholm.nametagchanger.skin.Skin werewolf;
    private static com.bringholm.nametagchanger.skin.Skin skin;

    public static com.bringholm.nametagchanger.skin.Skin getSkin(String player){

        NameTagChanger.INSTANCE.getSkin(player, (skin, successful, exception) -> {
            if (successful) {
                setSkin(skin);
            }else{
                setSkin(com.bringholm.nametagchanger.skin.Skin.EMPTY_SKIN);
            }
        });

        return skin;
    }

    private static void setSkin(com.bringholm.nametagchanger.skin.Skin s){
        skin = s;
    }
}
