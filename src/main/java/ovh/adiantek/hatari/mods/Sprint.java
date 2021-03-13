package ovh.adiantek.hatari.mods;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class Sprint extends Modification {
	public Sprint() {
		super(Sprint.class, Categories.MOVEMENT, "Sprint");
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("sprint", "Sprints automatically");
	}
	@Executor
	public void event(String cmd) {
		toggle();
	}
	@Override
	protected boolean onEnable() {
		return true;
	}
	@Override
	protected boolean onDisable() {
		return true;
	}
	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if (isEnabled() && mc.thePlayer!=null) {
			mc.thePlayer.setSprinting(true);
		}
	}
	
}
