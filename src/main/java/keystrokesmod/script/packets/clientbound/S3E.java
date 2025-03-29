package keystrokesmod.script.packets.clientbound;

import net.minecraft.network.play.server.S3EPacketTeams;

import java.util.Collection;

public class S3E extends SPacket {
    private String name;
    private String displayName;
    private String prefix;
    private String suffix;
    private String nametagVisibility;
    private Collection<String> playerList;
    private int action;
    private int friendlyFlags;
    private int color;
    public S3E(S3EPacketTeams packet) {
        super(packet);
        this.name = packet.getName();
        this.displayName = packet.getDisplayName();
        this.prefix = packet.getPrefix();
        this.suffix = packet.getSuffix();
        this.nametagVisibility = packet.getNameTagVisibility();
        this.playerList = packet.getPlayers();
        this.action = packet.getAction();
        this.friendlyFlags = packet.getFriendlyFlags();
        this.color = packet.getColor();
    }
}
