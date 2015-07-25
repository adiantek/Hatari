package ovh.adiantek.hatari.mods;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class Fullbright extends Modification {

	private float old;
	
	public Fullbright() {
		super(Fullbright.class, Categories.RENDER, "Fullbright");
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("fullbright", "Allow for clear as day nights, fully lit caves.");
	}
	public boolean onEnable() {
		old=mc.gameSettings.gammaSetting;
		mc.gameSettings.gammaSetting=100;
		return true;
	}
	public boolean onDisable() {
		if(old!=-1)
			mc.gameSettings.gammaSetting=old;
		else
			mc.gameSettings.gammaSetting=0;
		return true;
	}

	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if(isEnabled() && mc.gameSettings.gammaSetting!=100) {
			setEnabledState(false);
			viewMessage("Disabled Fullbright, because player changed brightness in settings.");
		}
	}
}
