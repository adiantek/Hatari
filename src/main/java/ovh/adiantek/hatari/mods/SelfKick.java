package ovh.adiantek.hatari.mods;

import org.bukkit.ChatColor;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import ovh.adiantek.hatari.GuiHatariGame;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class SelfKick extends Modification {
	private Packet bugPacket = new C01PacketChatMessage(ChatColor.COLOR_CHAR+"");
	//private Packet bugPacket = new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, Double.NaN, Double.NaN, Double.NaN, true);
	public SelfKick() {
		super(SelfKick.class, Categories.PLAYER, "SelfKick");
		addToggleCommand("selfkick", "Disconnect from server while PvP");
	}
	@Executor
	public void event(String cmd) {
		mc.getNetHandler().addToSendQueue(bugPacket);
	}
	@Override
	protected boolean onEnable() {
		mc.getNetHandler().addToSendQueue(bugPacket);
		return false;
	}
}