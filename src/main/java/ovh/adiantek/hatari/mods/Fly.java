package ovh.adiantek.hatari.mods;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.CommandManager.StricteArgument;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Fly extends Modification {
	private boolean force = getBoolean("force", false);
	private double speed = getDouble("speed", 1d);
	private long lastSet = 0;

	public Fly() {
		super(Fly.class, Categories.MOVEMENT, "Fly");
		addToggleCommand("fly", "Enable or disable fly");
		addToggleCommand("fly force", "Force flying on servers (can prevent being kicked for flying)");
		CommandManager
				.createNewCommand()
				.setCommand("fly set")
				.setExecutor(this)
				.setDescription("Set fly speed")
				.setRequestArguments(
						new CommandManager.CommandValidator[] {
								new CommandManager.DoubleValidator() },
						new String[] { "speed" }, false).register();
		CommandManager
				.createNewCommand()
				.setCommand("fly set")
				.setExecutor(this)
				.setDescription("Set fly speed")
				.setRequestArguments(
						new CommandManager.CommandValidator[] {
								new CommandManager.PercentValidator() },
						new String[] { "speed" }, false).register();
		FMLCommonHandler.instance().bus().register(this);
	}

	@Executor
	public void event(String command) {
		if(command.equals("fly"))
			toggle();
		else if(command.equals("fly force")) {
			force = setBoolean("force", !force);
			if (force) {
				viewMessage("Enabled ForceFly.");
			} else {
				viewMessage("Disabled ForceFly.");
			}
		}
	}

	@Executor
	public void exec(String command, double speed) {
		this.speed = speed;
		if (isEnabled())
			viewMessage("Setted fly speed to " + speed * 100 + "%");
		else
			viewMessage("Setted fly speed to " + speed * 100
					+ "%\nTip: use fly toggle to enable fly.");

	}


	@Override
	protected boolean onEnable() {
		if(mc.thePlayer==null)
			return true;
		mc.thePlayer.capabilities.allowFlying = true;
		mc.thePlayer.capabilities.setFlySpeed((float) (speed * 0.05f));
		return true;
	}

	@Override
	protected void save() {
		setBoolean("force", force);
		setDouble("speed", speed);
	}

	@Override
	protected boolean onDisable() {
		mc.thePlayer.capabilities.allowFlying = false;
		mc.thePlayer.capabilities.setFlySpeed((float) (speed * 0.05f));
		return true;
	}

	@SubscribeEvent
	public void event(TickEvent.PlayerTickEvent ot) {
		if (ot.phase == TickEvent.Phase.END) {
			EntityPlayer player = ot.player;
			
			if (player == mc.thePlayer) {
				if (player == null)
					return;
				if (!isEnabled() && player.capabilities.allowFlying) {
					setEnabledState(true);
					viewMessage("Enabled Fly by server!");
				}
				if (isEnabled()) {
					player.capabilities.setFlySpeed((float) (speed * 0.05f));
				}
				if (isEnabled() && !player.capabilities.allowFlying) {
					if (force) {
						player.capabilities.allowFlying = true;
						setEnabledState(true);
						if (System.currentTimeMillis() - lastSet > 3000) {
							lastSet = System.currentTimeMillis();
							viewMessage("Server tried to disable fly");
						}
					} else {
						setEnabledState(false);
						viewMessage("Disabled Fly by server!");
					}
				}
			}
		}
	}
}
