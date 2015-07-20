package ovh.adiantek.hatari.windows;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import ovh.adiantek.hatari.Configurator;
import ovh.adiantek.hatari.GuiWindow;
import ovh.adiantek.hatari.Modification;

public class Categories extends GuiWindow {
	private static Configurator configurator = new Configurator(Categories.class);
	private static Random random = new Random();
	public static void init() {}
	public static final Categories COMBAT = new Categories("Combat");
	public static final Categories MISC = new Categories("Misc");
	public static final Categories MOVEMENT = new Categories("Movement");
	public static final Categories PLAYER = new Categories("Player");
	public static final Categories RENDER = new Categories("Render");
	public static final Categories WORLD = new Categories("World");
	private String title;
	private Categories(String title) {
		super(
				random.nextInt(Minecraft.getMinecraft().displayWidth/2),
				random.nextInt(Minecraft.getMinecraft().displayHeight/2),
				150,
				13,
				title);
		load(configurator, title);
		WindowHub.addWindow(title, this);
	}
	@Override
	public void prepareRender() {
		hght = modifications.keySet().size()*10+13;
		if(modifications.size()!=0)hght+=3;
	}
	@Override
	protected void mouseClicked(int x, int y, int p_73864_3_) {
		if(y<0)
			return;
		int pos = y/10;
		if(pos>=modifications.size())
			return;
		((Modification)getAllModifications().toArray()[pos]).toggle();
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
		}
    	for(String title : modifications.keySet()) {
    		boolean isEnabled = modifications.get(title).isEnabled();
    		if(isEnabled && (hgPos==-1 || hgPos!=swY)) {
    			drawRect(swX, swY, startX+wdth-2, swY+10, 0x40ffffff);
    		}
    		if(hgPos==swY) {
    			drawHoveringText(Arrays.asList((isEnabled?"Disable":"Enabled")+" "+title+"?"), x, y, fontRendererObj);
    			RenderHelper.disableStandardItemLighting();
    		}
        	mc.fontRenderer.drawString(title, swX, swY+1, 0xffffff, false);
    		swY+=10;
    	}
	}
	public static void save() {
		COMBAT.save(configurator, "Combat");
		MISC.save(configurator, "Misc");
		MOVEMENT.save(configurator, "Movement");
		PLAYER.save(configurator, "Player");
		RENDER.save(configurator, "Render");
		WORLD.save(configurator, "World");
	}
	private TreeMap<String, Modification> modifications = new TreeMap<String, Modification>();
	public void addModification(String name, Modification gw) {
		modifications.put(name, gw);
		ActiveMods.addModification(name, gw);
	}
	public Collection<Modification> getAllModifications() {
		return modifications.values();
	}
}
