package ovh.adiantek.hatari;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiWindow extends GuiScreen {

	public int startX;
	public int startY;
	public int wdth;
	public int hght;
	private static int borderColor = 0xff000000;
	private static int backgroundTitleColor = 0x80000000;
	private static int backgroundColor = 0x40000000;
	private int moveX = 0;
	private boolean isCloseable;
	private int moveY = 0;
	private boolean moving = false;
	private String title;
	private ArrayList<GuiText> gtlist = new ArrayList<GuiText>();
	private GuiButton selectedButton;
	private boolean isMinimized;
	private boolean pinned;

	protected GuiWindow() {
		initGui();
	}

	public GuiWindow(int startX, int startY, int width, int height, String title) {
		this.startX = startX;
		this.startY = startY;
		this.wdth = width;
		this.hght = height;
		this.title = title;
		initGui();
	}

	public void setPinned(boolean v) {
		this.pinned = v;
	}

	public boolean isPinned() {
		return this.pinned;
	}

	public String toString() {
		return "GuiWindow[" + title + "]";
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public boolean isCloseable() {
		return isCloseable;
	}

	public void setCloseable(boolean isCloseable) {
		this.isCloseable = isCloseable;
	}

	public void addButton(GuiButton gb) {
		buttonList.add(gb);
	}

	public void addText(GuiText gb) {
		gtlist.add(gb);
	}

	private void correctLocation() {
		if (startX + wdth > width)
			startX = width - wdth;
		if (startY + hght > height)
			startY = height - hght;
		if (startX < 0)
			startX = 0;
		if (startY < 0)
			startY = 0;
	}

	public void setLocation(int x, int y) {
		startX = x;
		startY = y;
	}

	public void setSize(int width, int height) {
		this.wdth = width;
		this.hght = height;
	}

	public void save(Configurator conf, String key) {
		conf.setInteger(key + "_startX", startX);
		conf.setInteger(key + "_startY", startY);
		conf.setInteger(key + "_wdth", wdth);
		conf.setInteger(key + "_hght", hght);
		conf.setString(key + "_title", title);
		conf.setBoolean(key + "_minimized", isMinimized);
		conf.setBoolean(key + "_pinned", pinned);
		conf.setBoolean(key + "_closeable", isCloseable);
	}

	public void load(Configurator conf, String key) {
		startX = conf.getInteger(key + "_startX", 0);
		startY = conf.getInteger(key + "_startY", 0);
		wdth = conf.getInteger(key + "_wdth", wdth);
		hght = conf.getInteger(key + "_hght", hght);
		title = conf.getString(key + "_title", title);
		isMinimized = conf.getBoolean(key + "_minimized", isMinimized);
		pinned = conf.getBoolean(key + "_pinned", pinned);
		isCloseable = conf.getBoolean(key + "_closeable", isCloseable);
	}

	public final void drawScreen(int x, int y, float par3) {
		prepareRender();
		correctLocation();
		int tx = x - startX;
		int ty = y - startY;
		int tmpH = hght;
		if (isMinimized())
			hght = 13;
		drawRect(startX, startY, wdth + startX, 13 + startY,
				backgroundTitleColor);
		drawRect(startX, startY + 13, wdth + startX, hght + startY,
				backgroundColor);
		drawBorder();
		boolean drawButtons = this.mc.currentScreen instanceof GuiHatariGame;
		if (drawButtons) {
			boolean focused0 = tx > wdth - 11 && tx < wdth - 2 && ty < 11
					&& ty > 2;
			boolean focused1 = tx > wdth - 22 && tx < wdth - 13 && ty < 11
					&& ty > 2;
			boolean focused2 = tx > wdth - 33 && tx < wdth - 24 && ty < 11
					&& ty > 2;
			drawButtons(focused0, focused1, focused2, x, y);
		}
		this.drawString(fontRendererObj,
				substrToWidth(title, wdth - (drawButtons ? 22 : 2)),
				startX + 2, startY + 3, 0xffffffff);
		if (isMinimized()) {
			hght = tmpH;
			return;
		}
		int swX = startX + 1;
		int swY = startY + 14;
		renderContent(x, y, par3);
		for (Object o : buttonList) {
			GuiButton b = (GuiButton) o;
			b.xPosition += swX;
			b.yPosition += swY;
		}
		for (Object o : gtlist) {
			GuiText b = (GuiText) o;
			b.x += swX;
			b.y += swY;
		}
		super.drawScreen(x, y, par3);
		for (Object o : gtlist) {
			GuiText b = (GuiText) o;
			b.drawText(this.mc);
			b.x -= swX;
			b.y -= swY;
		}
		for (Object o : buttonList) {
			GuiButton b = (GuiButton) o;
			b.xPosition -= swX;
			b.yPosition -= swY;
		}
	}

	private String substrToWidth(String s, int wd) {
		if (s.length() < 2)
			return "";
		int w = this.fontRendererObj.getStringWidth(s);
		if (w <= wd) {
			return s;
		}
		return substrToWidth2(s,
				wd - this.fontRendererObj.getStringWidth("..."))
				+ "...";
	}

	private String substrToWidth2(String s, int wd) {
		if (s.length() < 2)
			return "";
		int w = this.fontRendererObj.getStringWidth(s);
		if (w > wd) {
			return substrToWidth2(s.substring(0, s.length() - 1), wd);
		}
		return s;
	}

	private void drawBorder() {
		drawRect(startX, startY, startX + wdth, startY + 1, borderColor);
		drawRect(startX, startY + 12, startX + wdth, startY + 13, borderColor);
		drawRect(startX, startY + hght - 1, startX + wdth, hght + startY,
				borderColor);
		drawRect(startX, startY, startX + 1, startY + hght, borderColor);
		drawRect(startX + wdth - 1, startY, startX + wdth, hght + startY,
				borderColor);
	}

	private void drawButtons(boolean focused0, boolean focused1,
			boolean focused2, int mouseX, int mouseY) {
		if (isCloseable) {
			drawRect(startX + wdth - 11, startY + 2, startX + wdth - 2,
					startY + 11, focused0 ? 0xff404040 : 0xff000000);
			fontRendererObj.drawString("X",
					startX + wdth - (fontRendererObj.getStringWidth("X") / 2)
							- 6, startY + 2, 0xffffffff, false);

			drawRect(startX + wdth - 22, startY + 2, startX + wdth - 2 - 11,
					startY + 11, focused1 ? 0xff404040
							: !isPinned() ? 0xff000000 : 0xff404040);
			drawRect(startX + wdth - 33, startY + 2, startX + wdth - 24,
					startY + 11, focused2 ? 0xff404040
							: !isMinimized() ? 0xff000000 : 0xff404040);
			fontRendererObj.drawString("_",
					startX + wdth - (fontRendererObj.getStringWidth("_") / 2)
							- 17 - 11, startY + 2, 0xffffffff, false);
		} else {
			drawRect(startX + wdth - 11, startY + 2, startX + wdth - 2,
					startY + 11, focused0 ? 0xff404040
							: !isPinned() ? 0xff000000 : 0xff404040);
			drawRect(startX + wdth - 22, startY + 2, startX + wdth - 13,
					startY + 11, focused1 ? 0xff404040
							: !isMinimized() ? 0xff000000 : 0xff404040);
			fontRendererObj.drawString("_",
					startX + wdth - (fontRendererObj.getStringWidth("_") / 2)
							- 17, startY + 2, 0xffffffff, false);
		}

		if (!isCloseable) {
			focused2 = focused1;
			focused1 = focused0;
		} else if (focused0) {
			drawHoveringText(Arrays.asList("Close"), mouseX, mouseY,
					fontRendererObj);

		}
		if (focused1) {
			if (isPinned()) {
				drawHoveringText(Arrays.asList("Unpin"), mouseX, mouseY,
						fontRendererObj);
			} else {
				drawHoveringText(Arrays.asList("Pin"), mouseX, mouseY,
						fontRendererObj);
			}
		} else if (focused2) {
			if (!isMinimized()) {
				drawHoveringText(Arrays.asList("Minimize"), mouseX, mouseY,
						fontRendererObj);
			} else {
				drawHoveringText(Arrays.asList("Restore"), mouseX, mouseY,
						fontRendererObj);
			}

		}
	}

	static List<Object[]> renders = Collections
			.synchronizedList(new ArrayList<Object[]>());

	public void drawHoveringText(List texts, int posX, int posY,
			FontRenderer font) {
		renders.add(new Object[] { texts, posX, posY, font, this });
	}

	void drawHoveringTextSuper(Object[] obj) {
		super.drawHoveringText((List) obj[0], (Integer) obj[1],
				(Integer) obj[2], (FontRenderer) obj[3]);
	}

	public boolean isMinimized() {
		return isMinimized;
	}

	public void setMinimized(boolean minimized) {
		this.isMinimized = minimized;
	}

	boolean mouseClickMoveFromOtherGUI(int x, int y, int z, long howLong) {
		if (!moving) {
			int tx = x - startX;
			int ty = y - startY;
			if (x > startX && x < startX + wdth && y > startY
					&& y < startY + 12) {
				if (tx > wdth - 11 && tx < wdth - 2 && ty < 11 && ty > 2) {
				} else if (tx > wdth - 22 && tx < wdth - 13 && ty < 11
						&& ty > 2) {
				} else {
					moving = true;
					moveX = x - startX;
					moveY = y - startY;
					return true;
				}
				return true;
			} else if (x > startX && x < startX + wdth && y > startY
					&& y < startY + hght) {
				mouseClickMove(x, y, z, howLong);
				return true;
			}
		} else {
			setLocation(x - moveX, y - moveY);
			return true;
		}
		return false;
	}

	@Override
	public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_,
			int p_146280_3_) {
		this.mc = p_146280_1_;
		this.fontRendererObj = p_146280_1_.fontRenderer;
		this.width = p_146280_2_;
		this.height = p_146280_3_;
	}

	boolean mouseMovedOrUpFromOtherGUI(int x, int y, int p_146286_3_) {
		moving = false;
		int swX = startX + 1;
		int swY = startY + 15;
		int tx = x - swX;
		int ty = y - swY;
		if (this.selectedButton != null && p_146286_3_ == 0) {
			this.selectedButton.mouseReleased(x - swX, y - swY);
			this.selectedButton = null;
		}

		if (x > startX && x < startX + wdth && y > startY && y < startY + hght) {
			mouseMovedOrUp(tx, ty, p_146286_3_);
			return true;
		}
		return false;
	}

	boolean mouseClickedFromOtherGUI(int x, int y, int par3) {
		int swX = startX + 1;
		int swY = startY + 15;

		int tx = x - startX;
		int ty = y - startY;
		if (tx > wdth - 11 && tx < wdth - 2 && ty < 11 && ty > 2) {
			if (isCloseable) {
				GuiHatariGame.removeWindow(this);
			} else
				setPinned(!isPinned());
			return true;
		} else if (tx > wdth - 22 && tx < wdth - 13 && ty < 11 && ty > 2) {
			if (isCloseable)
				setPinned(!isPinned());
			else
				setMinimized(!isMinimized);
			return true;
		} else if (tx > wdth - 33 && tx < wdth - 24 && ty < 11 && ty > 2) {
			if (isCloseable) {
				setMinimized(!isMinimized);
				return true;
			}
		}
		if (x > startX && x < startX + wdth && y > startY && y < startY + hght) {
			mouseClicked(tx - 1, ty - 15, par3);
			return true;
		}
		return false;
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
	}

	public void renderContent(int x, int y, float par3) {
	}

	public void prepareRender() {
	}
}
