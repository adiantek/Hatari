package ovh.adiantek.hatari.mods;

import java.util.List;

import javax.swing.JComponent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.CommandManager.CommandValidator;
import ovh.adiantek.hatari.CommandManager.DoubleValidator;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KillAura extends Modification {
	private boolean autoattack = getBoolean("autoattack", true);
	private boolean vis = getBoolean("vis", false);
	private double radius = getDouble("radius", 6);

	public KillAura() {
		super(KillAura.class, Categories.COMBAT, "KillAura");
		addToggleCommand("killaura", "Automatically attacks entities in a set range");
		addToggleCommand("killaura autoattack",
				"Enable or disable attacking entities.");
		addToggleCommand("killaura autoattack onlyvisible",
				"Enable or disable attacking only visible entities.");
		CommandManager
				.createNewCommand()
				.setCommand("killaura radius")
				.setDescription("Set radius of KillAura")
				.setExecutor(this)
				.setRequestArguments(
						new CommandValidator[] { new DoubleValidator() },
						new String[] { "radius" }, false).register();
		FMLCommonHandler.instance().bus().register(this);
	}

	// TODO settings

	@Override
	public void resetConfig() {
		autoattack=setBoolean("autoattack", true);
		vis=setBoolean("vis", false);
		radius=setDouble("radius", 6);
	}

	@Executor
	public void event(String cmd, double radius) {
		if(radius<=0) {
			viewMessage("Radius must be >0.");
		}
		this.radius=setDouble("radius", radius);
		viewMessage("Setted radius to "+radius+".");
	}

	@Executor
	public void event(String cmd) {
		if (cmd.equals("killaura"))
			toggle();
		else if (cmd.equals("killaura autoattack")) {
			autoattack = setBoolean("autoattack", !autoattack);
			if (autoattack) {
				viewMessage("Enabled AutoAttack!");
			} else {
				viewMessage("Disabled AutoAttack!");
			}
		} else if (cmd.equals("killaura autoattack onlyvisible")) {
			vis = setBoolean("vis", !vis);
			if (vis) {
				viewMessage("Enabled visible only");
			} else {
				viewMessage("Disabled visible only");
			}

		}
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
		if (!isEnabled() || mc.thePlayer == null || mc.objectMouseOver == null)
			return;
		Entity ent = mc.objectMouseOver.entityHit;
		if (autoattack && ent != null && !Friends.instance.isFriend(ent)) {
			mc.thePlayer.swingItem();
			mc.playerController.attackEntity(mc.thePlayer, ent);
		}
		if (ent == null) {
			List<Entity> entities = mc.theWorld.loadedEntityList;
			double nearestDistance = -1;
			float defYaw = mc.thePlayer.rotationYaw;
			float defPitch = mc.thePlayer.rotationPitch;
			Entity ne = null;
			for (Entity s : entities) {
				if (!Friends.instance.isFriend(s) && s != mc.thePlayer) {
					if(vis && s.isInvisible()) {
						continue;
					}
					EntityLivingBase elb = (EntityLivingBase) s;
					float d = s.getDistanceToEntity(mc.thePlayer);
					if (elb.getHealth() > 0 && d <= radius
							&& (nearestDistance == -1 || d < nearestDistance)) {
						double cx = s.posX - mc.thePlayer.posX;
						double cy = s.posY - mc.thePlayer.posY + 1;
						double cz = s.posZ - mc.thePlayer.posZ;
						mc.thePlayer.rotationYaw = (float) (360 - d(Math.atan2(
								cx, cz))) % 360f;
						mc.thePlayer.rotationPitch = (float) -(d(Math.atan2(cy,
								Math.sqrt(cx * cx + cz * cz)))) % 360f;
						mc.entityRenderer.getMouseOver(1f);
						if(mc.objectMouseOver.entityHit!=null) {
							nearestDistance = d;
							ne = s;
							defYaw = mc.thePlayer.rotationYaw;
							defPitch = mc.thePlayer.rotationPitch;
						}
					}
				}
			}
			mc.thePlayer.rotationYaw = defYaw;
			mc.thePlayer.rotationPitch = defPitch;
		}
	}

	private static double d(double s) {
		return s * 180 / Math.PI;
	}
}
