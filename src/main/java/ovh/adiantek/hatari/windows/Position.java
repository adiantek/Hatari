package ovh.adiantek.hatari.windows;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import ovh.adiantek.hatari.GuiWindow;
import ovh.adiantek.hatari.Modification;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Position extends Modification {
	private GuiWindow gw;
	private Minecraft minecraft;
	public Position() {
		super(Position.class, "Position");
		this.minecraft=Minecraft.getMinecraft();
		gw=new GuiWindow(getInteger("posX", 100), getInteger("posY", 100), 150, 13, "Loading...");
		WindowHub.addWindow("Position", gw);
		FMLCommonHandler.instance().bus().register(this);
	}
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent ot) {
		if(ot.phase==TickEvent.Phase.END) {
			EntityPlayer player = ot.player;
			if(player==minecraft.thePlayer) {
				int posX = (int) (player.posX+0.5);
				int posY = (int) (player.posY+0.5);
				int posZ = (int) (player.posZ+0.5);
				int i4 = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	            gw.setTitle("X: "+posX+" Y: "+posY+" Z: "+posZ+" | "+Direction.directions[i4]);
			}
		}
	}
}
