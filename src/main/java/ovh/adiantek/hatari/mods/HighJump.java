package ovh.adiantek.hatari.mods;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class HighJump extends Modification {
	private double height = getDouble("height", 2);
	@Executor
	public void exec(String command, double value) {
		height=setDouble("height", value);
		viewMessage("Setted height of jump to "+value);
	}
	public HighJump() {
		super(HighJump.class, Categories.MOVEMENT, "High Jump");
		MinecraftForge.EVENT_BUS.register(this);
		addToggleCommand("highjump", "Enable or disable High Jump");
		CommandManager
				.createNewCommand()
				.setCommand("highjump set")
				.setExecutor(this)
				.setDescription("Set height of jump")
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.DoubleValidator() }, new String[] { "height" }, false)
				.register();
		CommandManager
				.createNewCommand()
				.setCommand("highjump set")
				.setExecutor(this)
				.setDescription("Set height of jump")
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.PercentValidator() }, new String[] { "height" }, false)
				.register();
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
	public void event(LivingJumpEvent event) {
		if(isEnabled())
			event.entityLiving.motionY=(height-1)/10+0.41999998688697815D;
	}
}
