package ovh.adiantek.hatari.mods;

import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class NoHurtShakes extends Modification {
	public NoHurtShakes() {
		super(NoHurtShakes.class, Categories.RENDER, "NoHurtShakes");
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("nohurtshakes",
				"Removes the hurtcam effect");
	}

	public boolean onEnable() {
		return true;
	}

	public boolean onDisable() {
		return true;
	}
	@Executor
	public void event(String cmd) {
		toggle();
	}
	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if (!isEnabled() || mc.thePlayer == null)
			return;
		mc.thePlayer.hurtTime = 0;
	}
}
