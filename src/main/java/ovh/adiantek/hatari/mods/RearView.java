package ovh.adiantek.hatari.mods;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

//TODO
public class RearView extends Modification {

	private int mirrorFBO;
	private int mirrorTex;
	private int mirrorDepth;
	public Field renderEndNanoTime;
    public RenderGlobalHelper mirrorRenderGlobal;
	private int framecount;

	public RearView() {
		super(RearView.class, Categories.RENDER, "Rear View");
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
        mirrorRenderGlobal = new RenderGlobalHelper();
		mirrorFBO = ARBFramebufferObject.glGenFramebuffers();
		mirrorTex = GL11.glGenTextures();
		mirrorDepth = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorTex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, 320, 180, 0,
				GL11.GL_RGBA, GL11.GL_INT, (java.nio.IntBuffer) null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorDepth);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 320,
				180, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_INT,
				(java.nio.IntBuffer) null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		
		
		try {
			renderEndNanoTime = EntityRenderer.class
					.getDeclaredField("renderEndNanoTime");
		} catch (Exception e) {
		}
		if (renderEndNanoTime == null)
			try {
				renderEndNanoTime = EntityRenderer.class
						.getDeclaredField("field_78534_ac");
			} catch (Exception e) {
			}
		if (renderEndNanoTime != null) {
			renderEndNanoTime.setAccessible(true);
		}
	}

    private void switchToFB() {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, mirrorFBO);
        ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER,
                ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D,
                mirrorTex, 0);
        ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER,
                ARBFramebufferObject.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D,
                mirrorDepth, 0);
    }

    private void switchFromFB() {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
    }

    public void updateMirror(float partialTick) {
        int w, h;
        float y, py, p, pp;
        boolean hide, pause;
        int view, limit;
        long endTime = 0;
        MovingObjectPosition mouseOver;
        w = mc.displayWidth;
        h = mc.displayHeight;
        y = mc.renderViewEntity.rotationYaw;
        py = mc.renderViewEntity.prevRotationYaw;
        p = mc.renderViewEntity.rotationPitch;
        pp = mc.renderViewEntity.prevRotationPitch;
        hide = mc.gameSettings.hideGUI;
        view = mc.gameSettings.thirdPersonView;
        limit = mc.gameSettings.limitFramerate;
        mouseOver = mc.objectMouseOver;

        switchToFB();

        if (limit != 0 && renderEndNanoTime != null) {
            try {
                endTime = renderEndNanoTime.getLong(mc.entityRenderer);
            } catch (Exception e) {
            }
        }

        mc.displayHeight = 180;
        mc.displayWidth = 320;
        mc.gameSettings.hideGUI = true;
        mc.gameSettings.thirdPersonView = 0;
        mc.renderViewEntity.rotationYaw += 180;
        mc.renderViewEntity.prevRotationYaw += 180;
        mc.renderViewEntity.rotationPitch = -p + 18;
        mc.renderViewEntity.prevRotationPitch = -pp + 18;
        mirrorRenderGlobal.switchTo();

        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT | GL11.GL_ENABLE_BIT |
                GL11.GL_CURRENT_BIT | GL11.GL_POLYGON_BIT |
                GL11.GL_TEXTURE_BIT);

        mc.entityRenderer.updateCameraAndRender(partialTick);

        if (limit != 0 && renderEndNanoTime != null) {
            try {
                renderEndNanoTime.setLong(mc.entityRenderer, endTime);
            } catch (Exception e) {
            }
        }
        GL11.glPopAttrib();

        mirrorRenderGlobal.switchFrom();
        mc.objectMouseOver = mouseOver;
        mc.renderViewEntity.rotationYaw = y;
        mc.renderViewEntity.prevRotationYaw = py;
        mc.renderViewEntity.rotationPitch = p;
        mc.renderViewEntity.prevRotationPitch = pp;
        mc.gameSettings.limitFramerate = limit;
        mc.gameSettings.thirdPersonView = view;
        mc.gameSettings.hideGUI = hide;
        mc.displayWidth = w;
        mc.displayHeight = h;

        switchFromFB();
    }
    @SubscribeEvent
    public void tickEnd(TickEvent.RenderTickEvent r) {
        Tessellator tes = Tessellator.instance;
        if (mc.theWorld == null || mc.currentScreen != null || mc.gameSettings.thirdPersonView != 0
                || mc.thePlayer == null) return;
        if(r.phase==TickEvent.Phase.START) {

            updateMirror(r.renderTickTime);
            return;
        }
        boolean onLeft = true;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_POLYGON_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, 1000, 3000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -2000);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3ub((byte) 24, (byte) 24, (byte) 24);
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertex(0, mc.displayHeight / 15, 0);
        tes.addVertex(0, mc.displayHeight / 10, 0);
        tes.addVertex(mc.displayWidth / 10, mc.displayHeight / 7.5, 0);
        tes.addVertex(mc.displayWidth / 10, mc.displayHeight / 12.5, 0);
        tes.draw();
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertex(mc.displayWidth / 34, mc.displayHeight / 39, 0);
        tes.addVertex(mc.displayWidth / 34, mc.displayHeight / 2.9, 0);
        tes.addVertex(mc.displayWidth / 2.965, mc.displayHeight / 3.45, 0);
        tes.addVertex(mc.displayWidth / 2.965, mc.displayHeight / 24, 0);
        tes.draw();

        GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorTex);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        tes.startDrawing(GL11.GL_QUADS);
        tes.addVertexWithUV(mc.displayWidth / 30, mc.displayHeight / 30, 0, onLeft ? 1 : 0, 1);
        tes.addVertexWithUV(mc.displayWidth / 30, mc.displayHeight / 3, 0.2, onLeft ? 1 : 0, 0);
        tes.addVertexWithUV(mc.displayWidth / 3, mc.displayHeight / 3.5, 0.4, onLeft ? 0 : 1, 0);
        tes.addVertexWithUV(mc.displayWidth / 3, mc.displayHeight / 20, 0.3, onLeft ? 0 : 1, 1);
        tes.draw();

        GL11.glPopAttrib();
        mc.entityRenderer.setupOverlayRendering();
    }
    
    
    
	private class RenderGlobalHelper {
		public Minecraft mc;
		public RenderGlobal rg, orig;
		public boolean advanced_opengl;
		public boolean fancy_graphics;
		public int ambient_occlusion;
		RenderGlobalHelper() {
			mc = Minecraft.getMinecraft();
			rg = new RenderGlobal(mc);
			rg.registerDestroyBlockIcons((TextureMap) mc.getTextureManager()
					.getTexture(TextureMap.locationBlocksTexture));
			orig = null;
		}

		public void getSettings() {
			advanced_opengl = mc.gameSettings.advancedOpengl;
			fancy_graphics = mc.gameSettings.fancyGraphics;
			ambient_occlusion = mc.gameSettings.ambientOcclusion;
		}

		public boolean settingsChanged() {
			return advanced_opengl != mc.gameSettings.advancedOpengl
					|| fancy_graphics != mc.gameSettings.fancyGraphics
					|| ambient_occlusion != mc.gameSettings.ambientOcclusion;
		}

		public void switchTo() {
			if (orig == null)
				orig = mc.renderGlobal;
			if (orig.theWorld != rg.theWorld) {
				rg.setWorldAndLoadRenderers(orig.theWorld);
				getSettings();
			} else if (settingsChanged()) {
				rg.loadRenderers();
				getSettings();
			}
			mc.renderGlobal = rg;
		}

		public void switchFrom() {
			if (orig != null)
				mc.renderGlobal = orig;
			orig = null;
		}
	}
}
