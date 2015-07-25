package ovh.adiantek.hatari;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;


public class ModGuiFactory extends GuiScreen implements IModGuiFactory {
	public ModGuiFactory(){}
	public ModGuiFactory(GuiScreen screen) {
		previous=screen;
	}
	@Override
	public void initialize(Minecraft minecraftInstance) {}
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ModGuiFactory.class;
	}
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(
			RuntimeOptionCategoryElement element) {
		return null;
	}
	GuiScreen previous;
	@Override
	public void initGui() {
		Minecraft.getMinecraft().displayGuiScreen(previous);
		SettingsWindow.open(null);
		
	}
	

}
