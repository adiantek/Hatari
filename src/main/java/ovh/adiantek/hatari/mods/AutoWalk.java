package ovh.adiantek.hatari.mods;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class AutoWalk extends Modification {
	public AutoWalk() {
		super(AutoWalk.class, Categories.MOVEMENT, "Auto-Walk");
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("autowalk", "Walks for you");
	}
	@Override
	protected boolean onEnable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
		return true;
	}
	@Override
	protected boolean onDisable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
		return true;
	}
	@Executor
	public void event(String cmd) {
		toggle();
	}
	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if (isEnabled() && mc.thePlayer!=null) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
		}
	}
}
