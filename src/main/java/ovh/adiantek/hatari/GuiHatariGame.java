package ovh.adiantek.hatari;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import ovh.adiantek.hatari.mods.AutoSign;
import ovh.adiantek.hatari.mods.ChangeUsername;
import ovh.adiantek.hatari.mods.Fly;
import ovh.adiantek.hatari.windows.ActiveMods;
import ovh.adiantek.hatari.windows.Categories;
import ovh.adiantek.hatari.windows.WindowHub;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiHatariGame extends GuiScreen {
	public static final GuiHatariGame instance = new GuiHatariGame();
	private static final List<GuiWindow> windows = Collections
			.synchronizedList(new ArrayList<GuiWindow>());
	private static final List<GuiWindow> toRemove = Collections
			.synchronizedList(new ArrayList<GuiWindow>());
	private boolean showConsole;
	private boolean showWindow;
	private static Configurator config;

	private static void showFirstRun() {
		if (config.getBoolean("firstRun", false))
			return;
		final GuiWindow gw = new GuiWindow(0, 0, 250, 14 + 24 + 12 + 20 + 14,
				"Hatari 1.7.10 - First run") {
			private boolean firstRend = true;

			protected void actionPerformed(GuiButton gb) {
				if (gb.id == 1) {
					removeWindow(this);
					config.setBoolean("firstRun", true);
				} else {
					try {
						Desktop.getDesktop().browse(
								new URI("http://adiantek.ovh/"));
					} catch (Throwable throwable) {
						System.err.println("Couldn\'t open link");
						throwable.printStackTrace();
					}
				}
			}

			@Override
			public void prepareRender() {
				if (firstRend)
					setLocation(width / 2 - wdth / 2, height / 2 - hght / 2);
				firstRend = false;
			}
		};
		gw.setPinned(true);
		addWindow(gw);
		GuiText gt = new GuiText(0, 0, "Welcome to Hatari v1.1 by barwnikk!",
				0xffffffff, false);
		gt.x = (250 - Minecraft.getMinecraft().fontRenderer
				.getStringWidth(gt.text)) / 2;
		gw.addText(gt);
		GuiText gt2 = new GuiText(0, 14, "Press U to open the console.",
				0xffffffff, false);
		gt2.x = (250 - Minecraft.getMinecraft().fontRenderer
				.getStringWidth(gt2.text)) / 2;
		gw.addText(gt2);
		GuiText gt3 = new GuiText(0, 14 + 12, "Press Y to open the GUI",
				0xffffffff, false);
		gt3.x = (250 - Minecraft.getMinecraft().fontRenderer
				.getStringWidth(gt3.text)) / 2;
		gw.addText(gt3);
		GuiText gt4 = new GuiText(0, 14 + 24,
				"Press F4+H to disable/enable Hatari.", 0xffffffff, false);
		gt4.x = (250 - Minecraft.getMinecraft().fontRenderer
				.getStringWidth(gt4.text)) / 2;
		gw.addText(gt4);
		GuiButton gb = new GuiButton(0, 1, 14 + 24 + 12, 124, 20,
				"Visit web page");
		GuiButton gb2 = new GuiButton(1, 125, 14 + 24 + 12, 124, 20, "Close");
		gw.addButton(gb);
		gw.addButton(gb2);
	}

	@Override
	protected final void mouseClickMove(int x, int y, int lastButtonClicked,
			long timeSinceMouseClick) {
		if (Minecraft.getMinecraft().currentScreen != null
				&& !Minecraft.getMinecraft().currentScreen.doesGuiPauseGame()) {
			for (int i = GuiHatariGame.windows.size() - 1; i > -1; i--) {
				GuiWindow gw = GuiHatariGame.windows.get(i);
				if (gw.mouseClickMoveFromOtherGUI(x, y, lastButtonClicked,
						timeSinceMouseClick)) {
					GuiHatariGame.windows.remove(gw);
					GuiHatariGame.windows.add(gw);
					return;
				}
			}
		}
	}

	@Override
	protected final void mouseMovedOrUp(int mouseX, int mouseY, int which) {
		if (Minecraft.getMinecraft().currentScreen != null
				&& !Minecraft.getMinecraft().currentScreen.doesGuiPauseGame()) {
			for (int i = GuiHatariGame.windows.size() - 1; i > -1; i--) {
				GuiWindow gw = GuiHatariGame.windows.get(i);
				if (gw.mouseMovedOrUpFromOtherGUI(mouseX, mouseY, which)) {
					GuiHatariGame.windows.remove(gw);
					GuiHatariGame.windows.add(gw);
					return;
				}
			}
		}
	}

	@Override
	protected final void mouseClicked(int par1, int par2, int par3) {
		if (Minecraft.getMinecraft().currentScreen != null
				&& !Minecraft.getMinecraft().currentScreen.doesGuiPauseGame()) {
			for (int i = GuiHatariGame.windows.size() - 1; i > -1; i--) {
				GuiWindow gw = GuiHatariGame.windows.get(i);
				if (gw.mouseClickedFromOtherGUI(par1, par2, par3)) {
					GuiHatariGame.windows.remove(gw);
					GuiHatariGame.windows.add(gw);
					return;
				}
			}
		}
	}

	public static final void removeWindow(GuiWindow gw) {
		toRemove.add(gw);
	}

	public static final void addWindow(GuiWindow gw) {
		synchronized(GuiHatariGame.instance) {
			if (windows.contains(gw))
				return;
			windows.add(gw);
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(),
					Minecraft.getMinecraft().displayWidth,
					Minecraft.getMinecraft().displayHeight);
			gw.setWorldAndResolution(Minecraft.getMinecraft(), sr.getScaledWidth(),
					sr.getScaledHeight());
		}
	}

	@Override
	protected final void keyTyped(char character, int key) {
		GuiHatariGame.windows.get(GuiHatariGame.windows.size() - 1).keyTyped(
				character, key);
	}

	@Override
	public final void drawScreen(int x, int y, float par3) {
		synchronized (this) {
			if (!Hatari.visible)
				return;
			if (Minecraft.getMinecraft().thePlayer != null) {
				for (GuiWindow gw : toRemove) {
					windows.remove(gw);
				}
				toRemove.clear();
				GuiWindow.renders.clear();
				int focusWindow = -1;
				for (int i = windows.size() - 1; i >= 0; i--) {
					GuiWindow gw = windows.get(i);
					if (gw.isPinned()
							|| Minecraft.getMinecraft().currentScreen instanceof GuiHatariGame) {
						if (x > gw.startX && x < gw.startX + gw.wdth
								&& y > gw.startY && y < gw.startY + gw.hght) {
							focusWindow = i;
							break;
						}
					}
				}
				int i = 0;
				for (GuiWindow gw : windows) {
					if (gw.isPinned()
							|| Minecraft.getMinecraft().currentScreen instanceof GuiHatariGame) {
						gw.drawScreen(i == focusWindow ? x : -1,
								i == focusWindow ? y : -1, par3);
					}
					i++;
				}
				for (Object[] ob : GuiWindow.renders) {
					((GuiWindow) ob[4]).drawHoveringTextSuper(ob);
				}
			}
		}
	}

	@SubscribeEvent
	public final void onDraw(RenderGameOverlayEvent.Text draw) {
		init();
		drawScreen(-1, -1, draw.partialTicks);
		if (showWindow) {
			Minecraft.getMinecraft().displayGuiScreen(instance);
			showWindow = false;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Y)
				&& Minecraft.getMinecraft().currentScreen == null) {
			showWindow = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_U)
				&& Minecraft.getMinecraft().currentScreen == null) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConsole());
		}
	}

	@Override
	public final boolean doesGuiPauseGame() {
		return false;
	}

	public static final void init() {
		if (config != null)
			return;
		config = new Configurator(GuiHatariGame.class);
		addWindow(new WindowHub());
		showFirstRun();
	}

	@Override
	public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_,
			int p_146280_3_) {
		synchronized(this) {
			super.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
			for (GuiWindow window : windows)
				window.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
		}
	}
}
