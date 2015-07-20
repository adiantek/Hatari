package ovh.adiantek.hatari.windows;

import java.util.Collection;
import java.util.TreeMap;

import ovh.adiantek.hatari.Configurator;
import ovh.adiantek.hatari.GuiHatariGame;
import ovh.adiantek.hatari.GuiWindow;

public class WindowHub extends GuiWindow {
	public WindowHub() {
		super(10, 10, 150, 13, "Window Hub");
		new Position();
	}
	@Override
	public void prepareRender() {
		hght = windows.keySet().size()*10+13;
		if(windows.size()!=0)hght+=3;
	}
	@Override
	protected void mouseClicked(int x, int y, int p_73864_3_) {
		if(y<0)
			return;
		int pos = y/10;
		if(pos>=windows.size())
			return;
		String title = windows.keySet().toArray()[pos].toString();
		visible.put(title, !visible.get(title));
		if(visible.get(title)) {
			GuiHatariGame.addWindow(windows.get(title));
		} else {
			GuiHatariGame.removeWindow(windows.get(title));
		}
	}
	@Override
	public void renderContent(int x, int y, float par3) {
    	int swX = startX+2;
    	int swY = startY+14;
    	int hgPos = -1;
		if(x>startX&&x<startX+wdth&&y>startY+15&&y<startY+hght-1) {
			int pos = (y-15-startY)/10;
			hgPos=startY+pos*10+14;
			drawRect(swX, hgPos, startX+wdth-2, hgPos+10, 0x40ffffff);
		}
    	for(String title : windows.keySet()) {
    		if(visible.get(title) && (hgPos==-1 || hgPos!=swY)) {
    			drawRect(swX, swY, startX+wdth-2, swY+10, 0x20ffffff);
    		}
        	mc.fontRenderer.drawString(title, swX, swY+1, 0xffffff, false);
    		swY+=10;
    	}
	}
	public static void save() {
		new Configurator(WindowHub.class).setObject("visible", visible);
	}
	private static TreeMap<String, GuiWindow> windows = new TreeMap<String, GuiWindow>();
	private static TreeMap<String, Boolean> visible = (TreeMap<String, Boolean>) new Configurator(WindowHub.class).getObject("visible", new TreeMap<String, Boolean>());
	public static void addWindow(String name, GuiWindow gw) {
		windows.put(name, gw);
		visible.put(name, false);
	}
	public static void removeWindow(String name) {
		if(windows.containsKey(name)) {
			GuiHatariGame.removeWindow(windows.get(name));
		}
		windows.remove(name);
		visible.remove(name);
	}
	public static Collection<GuiWindow> getAllWindows() {
		return windows.values();
	}
}
