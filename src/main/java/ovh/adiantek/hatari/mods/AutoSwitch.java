package ovh.adiantek.hatari.mods;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class AutoSwitch extends Modification {

	public AutoSwitch() {
		super(AutoSwitch.class, Categories.PLAYER, "Auto-Switch");
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("autoswitch", "Cycles your hotbar slots");
	}
	@Executor
	public void exec(String cmd) {
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
		if (isEnabled() && mc.thePlayer!=null && e.phase==TickEvent.Phase.START) {
			mc.thePlayer.inventory.currentItem++;
			while(mc.thePlayer.inventory.currentItem>8)
				mc.thePlayer.inventory.currentItem-=9;
		}
	}
	
}
