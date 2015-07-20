package ovh.adiantek.hatari.mods;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KillAura extends Modification {
	private boolean autoattack = getBoolean("autoattack", true);
	private double radius = 6;
	public KillAura() {
		super(KillAura.class, Categories.COMBAT, "KillAura");
		addToggleCommand("killaura", "Automatycally kills mobs or players");
		addToggleCommand("killaura autoattack", "Enable or disable attacking entities.");
		FMLCommonHandler.instance().bus().register(this);
	}
	@Executor
	public void event(String cmd) {
		if(cmd.equals("killaura"))
			toggle();
		else if(cmd.equals("killaura autoattack"))
			autoattack=setBoolean("autoattack", !autoattack);
	}
	@Override
	protected boolean onEnable() {
		return true;
	}
	@Override
	protected boolean onDisable() {
		return true;
	}
	private boolean shouldAttack(Entity entity) {
		if(!(entity instanceof EntityPlayer))
			return false;
		if(entity == mc.thePlayer)
			return false;
		return !Friends.instance.isFriend(entity.getCommandSenderName());
	}

	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		if(!isEnabled() || mc.thePlayer==null || mc.objectMouseOver==null)
			return;
		Entity ent = mc.objectMouseOver.entityHit;
		if(ent==null) {
			List<Entity> entities = mc.theWorld.loadedEntityList;
			double nearestDistance = -1;
			float defYaw = mc.thePlayer.rotationYaw;
			float defPitch = mc.thePlayer.rotationPitch;
			Entity ne = null;
			for(Entity s : entities) {
				if(shouldAttack(s)) {
					EntityLivingBase elb = (EntityLivingBase) s;
					float d = s.getDistanceToEntity(mc.thePlayer);
					if(elb.getHealth()>0 && d<=radius&&(nearestDistance==-1||d<nearestDistance)) {
						double cx = s.posX-mc.thePlayer.posX;
						double cy = s.posY-mc.thePlayer.posY+1;
						double cz = s.posZ-mc.thePlayer.posZ;
						mc.thePlayer.rotationYaw=(float) (360-d(Math.atan2(cx, cz)))%360f;
						mc.thePlayer.rotationPitch=(float) -(d(Math.atan2(cy, Math.sqrt(cx*cx+cz*cz))))%360f;
						nearestDistance=d;
						ne=s;
						defYaw=mc.thePlayer.rotationYaw;
						defPitch=mc.thePlayer.rotationPitch;
					}
				}
			}
			mc.thePlayer.rotationYaw=defYaw;
			mc.thePlayer.rotationPitch=defPitch;
			mc.thePlayer.sendMotionUpdates();
			mc.entityRenderer.getMouseOver(1f);
		}
		if(autoattack && ent!=null && shouldAttack(ent)) {
			mc.thePlayer.swingItem();
			mc.playerController.attackEntity(mc.thePlayer, ent);
		}
	}
	private static double d(double s) {
		return s*180/Math.PI;
	}
}
