package fr.kappacite.deceit.objects;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import net.minecraft.server.v1_9_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

    public void sendTitle(Player player, String title, String subtitle, int ticks){
        IChatBaseComponent baseTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent baseSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, baseTitle);
        PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, baseSubTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitlePacket);
        sendTime(player, ticks);
    }

    private void sendTime(Player player, int ticks){
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES,  null, 20, ticks, 20);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
    }

    public void sendActionBar(Player player, String message){
        IChatBaseComponent baseTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(baseTitle, (byte)2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
