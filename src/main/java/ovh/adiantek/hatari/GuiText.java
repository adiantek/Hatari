package ovh.adiantek.hatari;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiText extends Gui{

	public String text;
	public int x;
	public int y;
	public int color;
	public boolean shadow;
	public GuiText(int x, int y, String text, int color, boolean shadow) {
		this.x=x;
		this.y=y;
		this.text=text;
		this.color=color;
		this.shadow=shadow;
	}
	public void drawText(Minecraft mc) {
		mc.fontRenderer.drawString(text, x, y, color, shadow);
	}
}
