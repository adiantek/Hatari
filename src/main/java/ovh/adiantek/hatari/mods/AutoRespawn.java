package ovh.adiantek.hatari.mods;

import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AutoRespawn extends Modification {

	public AutoRespawn() {
		super(AutoRespawn.class, Categories.PLAYER, "Auto-Respawn");
		addToggleCommand("autorespawn", "Automatically respawns when you die");
		MinecraftForge.EVENT_BUS.register(this);
	}
	@Override
	protected boolean onEnable() {
		return true;
	}
	@Override
	protected boolean onDisable() {
		return true;
	}
	@Executor
	public void event(String command) {
		toggle();
	}
	@SubscribeEvent
	public void event(GuiScreenEvent.InitGuiEvent.Pre s) {
		if(isEnabled())
		if(s.gui!=null && s.gui instanceof GuiGameOver) {
			if(!mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
				mc.thePlayer.respawnPlayer();
				s.setCanceled(true);
			}
		}
	}
}
