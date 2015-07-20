package ovh.adiantek.hatari.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import ovh.adiantek.hatari.Configurator;
import ovh.adiantek.hatari.GuiWindow;
import ovh.adiantek.hatari.Modification;

public class ActiveMods extends GuiWindow {
	private Configurator configurator;
	public ActiveMods() {
		this.setTitle("Active Mods");
		this.setSize(150, 13);
		configurator = new Configurator(ActiveMods.class);
		this.setLocation(
				configurator.getInteger("posX", (int) (System.currentTimeMillis()%Minecraft.getMinecraft().displayWidth/2)),
				configurator.getInteger("posY", (int) (System.currentTimeMillis()%Minecraft.getMinecraft().displayHeight/2)));
		WindowHub.addWindow("Active Mods", this);
	}
	@Override
	public void prepareRender() {
		hght = 13+enabled.size()*10;
		if(enabled.size()>0)hght+=3;
	}
	@Override
	protected void mouseClicked(int x, int y, int p_73864_3_) {
		if(y<0)
			return;
		int pos = y/10;
		if(pos>=enabled.size())
			return;
		modifications.get(enabled.get(pos)).disable();
	}
	@Override
	public void renderContent(int x, int y, float par3) {
    	int swX = startX+2;
    	int swY = startY+14;
    	int hgPos = -1;
		if(x>startX&&x<startX+wdth&&y>startY+15&&y<startY+hght-1) {
			int pos = (y-15-startY)/10;
			hgPos=startY+pos*10+14;
			drawRect(swX, hgPos, startX+wdth-2, hgPos+10, 0x80ffffff);
			drawHoveringText(Arrays.asList("Disable "+enabled.get(pos)+"?"), x, y, fontRendererObj);
			RenderHelper.disableStandardItemLighting();
		}
    	for(String title : enabled) {
			mc.fontRenderer.drawString(title, swX, swY+1, 0xffffff, false);
			swY+=10;
    	}
	}
	public static void update() {
		enabled.clear();
		for(Map.Entry<String, Modification> entries : modifications.entrySet()) {
			if(entries.getValue().isEnabled())
				enabled.add(entries.getKey());
		}
	}
	private static TreeMap<String, Modification> modifications = new TreeMap<String, Modification>();
	private static ArrayList<String> enabled = new ArrayList<String>();
	public static void addModification(String name, Modification gw) {
		modifications.put(name, gw);
	}
	public static Collection<Modification> getAllModifications() {
		return modifications.values();
	}
}
