package ovh.adiantek.hatari.mods;

import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;

public class Flip extends Modification {

	public Flip() {
		super(Flip.class, Categories.PLAYER, "Flip");
		addToggleCommand("flip", "Spin 180°");
	}
	@Executor
	public void exec(String cmd) {
		toggle();
	}
	@Override
	protected boolean onEnable() {
		if(mc.thePlayer!=null)
			mc.thePlayer.rotationYaw+=180;
		return false;
	}
	
}
