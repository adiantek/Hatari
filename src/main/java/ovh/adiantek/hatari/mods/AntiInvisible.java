package ovh.adiantek.hatari.mods;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import ovh.adiantek.hatari.Hatari;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AntiInvisible extends Modification {

	public AntiInvisible() {
		super(AntiInvisible.class, Categories.RENDER, "AntiInvisible");
		MinecraftForge.EVENT_BUS.register(this);
		addToggleCommand("antiinvisible", "Shows invisible entities");
	}
	private boolean invisible;
	@Executor
	public void executor(String command) {
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
	public void event(RenderLivingEvent.Pre p) {
		if(!isEnabled() || Hatari.instance.visible) {
			invisible=false;
			return;
		}
		invisible=p.entity.isInvisible();
		if(!invisible)
			return;
		p.entity.setInvisible(false);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.25F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
	}
	@SubscribeEvent
	public void event(RenderLivingEvent.Post p) {
		if(!invisible)
			return;
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        p.entity.setInvisible(true);
	}
}
