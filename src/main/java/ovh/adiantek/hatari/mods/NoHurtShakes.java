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
				"Disable visual appearance of red tint after attack");
	}

	public boolean onEnable() {
		return true;
	}

	public boolean onDisable() {
		return true;
	}

	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if (!isEnabled() || mc.thePlayer == null)
			return;

		mc.thePlayer.hurtTime = 0;
		if (mc.objectMouseOver != null) {
			if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				if (mc.objectMouseOver.blockY == 248
						&& mc.objectMouseOver.sideHit == 1) {
					if ((mc.objectMouseOver.blockX % 2 == 0 && mc.objectMouseOver.blockZ % 2 == 0)
							|| (mc.objectMouseOver.blockX % 2 == 1 && mc.objectMouseOver.blockZ % 2 == 1)) {
						if(mc.thePlayer.getHeldItem()!=null && mc.thePlayer.getHeldItem().getItem()==Item.getItemById(5))
						mc.playerController.onPlayerRightClick(mc.thePlayer,
								mc.theWorld, mc.thePlayer.getHeldItem(),
								mc.objectMouseOver.blockX,
								mc.objectMouseOver.blockY,
								mc.objectMouseOver.blockZ,
								mc.objectMouseOver.sideHit,
								mc.objectMouseOver.hitVec);
						mc.thePlayer.swingItem();
					}
				}
			}
		}
	}
}