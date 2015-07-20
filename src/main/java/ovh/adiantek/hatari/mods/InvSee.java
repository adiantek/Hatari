package ovh.adiantek.hatari.mods;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.GuiHatariGame;
import ovh.adiantek.hatari.GuiWindow;
import ovh.adiantek.hatari.Modification;

public class InvSee extends Modification {
	public InvSee() {
		super(InvSee.class, "InvSee");
		CommandManager
				.createNewCommand()
				.setCommand("Invsee")
				.setExecutor(this)
				.setDescription(
						"View EQ, potion effects about player near you.")
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.EntityPlayerValidator() },
						new String[] { "player" }, false).register();
		CommandManager
				.createNewCommand()
				.setCommand("invsee")
				.setExecutor(this)
				.setDescription(
						"View EQ, potion effects about player near you.")
				.setRequestArguments(
						new CommandManager.CommandValidator[] {},
						new String[] {}, false).register();
	}

	protected static final ResourceLocation field_147001_a = new ResourceLocation(
			"textures/gui/container/inventory.png");
	private static HashMap<Integer, ItemStack> getItemDamageFromColor;
	static {
		getItemDamageFromColor = new HashMap<Integer, ItemStack>();
		for (int i = 0; i < 65536; i++) {
			try {
				List<PotionEffect> pl = PotionHelper.getPotionEffects(i, true);
				for (PotionEffect pe : pl) {
					ItemStack is = new ItemStack(Item.getItemById(373));
					is.setItemDamage(i);
					getItemDamageFromColor.put(Potion.potionTypes[pe
							.getPotionID()].getLiquidColor(), is);
					break;
				}
			} catch (Throwable t) {

			}
		}
	}

	@Executor
	public void exec(String command, EntityPlayer player) {
		if(player==null) {
			viewMessage("Player not found!");
			return;
		}
		invSee(player);
	}

	@Executor
	public void exec(String command) {
		invSee(null);
	}

	private void invSee(final EntityPlayer ep) {
		if (ep == null) {
			GuiHatariGame.addWindow(new InvSeeWindow(null));
		} else {
			GuiHatariGame.addWindow(new InvSeeWindow(ep.getCommandSenderName()));
		}
	}

	private class InvSeeWindow extends GuiWindow {
		private EntityLivingBase ep;
		private String username;
		private InventoryPlayer inv;
		private final RenderItem itemRenderer = new RenderItem();

		public InvSeeWindow(String username) {
			super(100, 100, 150, 14 + (16 * 6) + 10, "Inventory"
					+ (username == null ? "" : " - "
							+ findPlayer(username).func_145748_c_()
									.getFormattedText()));
			setPinned(true);
			setCloseable(true);
			if (username != null)
				ep = findPlayer(username);
			if (username != null)
				this.username = ep.getCommandSenderName();
		}

		public void renderContent(int x, int y, float par3) {
			int dx = startX + 2;
			int dy = startY + 14;
			List<EntityPlayer> players = Minecraft.getMinecraft().theWorld.playerEntities;
			if (username != null) {
				this.ep=null;
				for (EntityPlayer ep : players) {
					if (ep.getCommandSenderName().equals(username)) {
						this.ep = ep;
						break;
					}
				}
			}
			else {
				try {
					Entity e = Minecraft.getMinecraft().objectMouseOver.entityHit;
					if(e!=null && e instanceof EntityLivingBase)
						ep = (EntityLivingBase) e;
				} catch(NullPointerException e) {
					
				}
			}
			if (ep == null) {
				if (username == null) {
					this.drawCenteredString(fontRendererObj, "There aren't entity on mouse over.", wdth/2+startX, startY+hght/2, 0xffffffff);
				} else {
					this.drawCenteredString(fontRendererObj, "Player is not near you.", 146/2+dx, dy+hght/2, 0xffffffff);
				}
				return;
			}
			setTitle("Inventory - " + ep.func_145748_c_().getFormattedText());
			if (ep instanceof EntityPlayer)
				inv = ((EntityPlayer) ep).inventory;
			else
				inv = null;
			if (inv != null)
				for (int i = 3; i >= 0; i--) {
					if (inv.armorInventory[i] != null) {
						GL11.glEnable(GL12.GL_RESCALE_NORMAL);
						RenderHelper.enableGUIStandardItemLighting();
						renderItem(inv.armorInventory[i], dx + (150 - 20), dy
								+ (3 - i) * 16, par3);
						RenderHelper.disableStandardItemLighting();
						GL11.glDisable(GL12.GL_RESCALE_NORMAL);
						int width = 150 - 4;
						try {
							width = width
									* (inv.armorInventory[i].getMaxDamage() - inv.armorInventory[i]
											.getItemDamageForDisplay())
									/ inv.armorInventory[i].getMaxDamage();
						} catch (Throwable t) {
						}
						this.drawRect(dx, dy + (3 - i) * 16, dx + width, dy
								+ (4 - i) * 16, 0x80ffffff);
						drawCenteredString(
								fontRendererObj,
								(inv.armorInventory[i].getMaxDamage() - inv.armorInventory[i]
										.getItemDamageForDisplay())
										+ "/"
										+ inv.armorInventory[i].getMaxDamage(),
								dx + 146 / 2, dy + (3 - i) * 16 + 2, 0xffffffff);
					}
				}
			if (inv != null)
				if (inv.getCurrentItem() != null) {
					GL11.glEnable(GL12.GL_RESCALE_NORMAL);
					RenderHelper.enableGUIStandardItemLighting();
					renderItem(inv.getCurrentItem(), dx + (150 - 20),
							dy + 4 * 16, par3);
					RenderHelper.disableStandardItemLighting();
					GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					int width = 150 - 4;
					try {
						width = width
								* (inv.getCurrentItem().getMaxDamage() - inv
										.getCurrentItem()
										.getItemDamageForDisplay())
								/ inv.getCurrentItem().getMaxDamage();
					} catch (Throwable t) {
					}
					this.drawRect(dx, dy + 4 * 16, dx + width, dy + 5 * 16,
							0x80ffffff);
					drawCenteredString(fontRendererObj, (inv.getCurrentItem()
							.getMaxDamage() - inv.getCurrentItem()
							.getItemDamageForDisplay())
							+ "/" + inv.getCurrentItem().getMaxDamage(),
							dx + 146 / 2, dy + 4 * 16 + 2, 0xffffffff);
				}
			float health = ep.getHealth();
			float maxHealth = ep.getMaxHealth();
			int width = 150 - 4;
			width = (int) (width * health / maxHealth);
			this.drawRect(dx, dy + 5 * 16, dx + width, dy + 6 * 16, 0x80ffffff);
			drawCenteredString(fontRendererObj, (int) (health) + "/"
					+ (int) (maxHealth) + " HP", dx + 146 / 2, dy + 5 * 16 + 2,
					0xffffffff);

			hght = 14 + (16 * 6) + (18 * ep.getActivePotionEffects().size())
					+ 12;

			int potionPosition = dy + 6 * 16 + 2;

			for (Object o : ep.getActivePotionEffects()) {
				PotionEffect var8 = (PotionEffect) o;
				String var9 = StatCollector.translateToLocal(
						var8.getEffectName()).trim();
				if (var8.getAmplifier() > 0) {
					var9 = var9
							+ " "
							+ StatCollector.translateToLocal(
									"potion.potency." + var8.getAmplifier())
									.trim();
				}

				if (var8.getDuration() > 20) {
					var9 = var9 + " (" + Potion.getDurationString(var8) + ")";
				}
				Potion var10 = Potion.potionTypes[var8.getPotionID()];

				int idicon = var10.getStatusIconIndex();
				GL11.glDisable(GL11.GL_LIGHTING);
				this.mc.getTextureManager().bindTexture(field_147001_a);
				this.drawTexturedModalRect(dx, potionPosition,
						0 + idicon % 8 * 18, 198 + idicon / 8 * 18, 18, 18);

				fontRendererObj.drawString(var9, dx + 18, potionPosition,
						var10.isBadEffect() ? 0xffff5555 : 0xffffffff);
				potionPosition += 18;
			}
			if (inv == null)
				return;
			if (x > startX && x < startX + wdth && y > startY + 14
					&& y < startY + (14 + 16 * 6)) {
				int linia = (y - startY - 14) / 16;
				if (linia < 4) {
					int armor = 3 - linia;
					if (inv.armorInventory[armor] != null) {
						this.renderToolTip(inv.armorInventory[armor], x, y);
					}
				} else if (linia == 4) {
					if (inv.getCurrentItem() != null) {
						this.renderToolTip(inv.getCurrentItem(), x, y);
					}
				}
			}
		}
		private void renderItem(ItemStack var5, int par2, int par3, float par4) {
			if (var5 != null) {
				float var6 = (float) var5.animationsToGo - par4;

				if (var6 > 0.0F) {
					GL11.glPushMatrix();
					float var7 = 1.0F + var6 / 5.0F;
					GL11.glTranslatef((float) (par2 + 8), (float) (par3 + 12),
							0.0F);
					GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
					GL11.glTranslatef((float) (-(par2 + 8)),
							(float) (-(par3 + 12)), 0.0F);
				}
				itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer,
						this.mc.getTextureManager(), var5, par2, par3);
				if (var6 > 0.0F) {
					GL11.glPopMatrix();
				}
				itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer,
						this.mc.getTextureManager(), var5, par2, par3);
			}
		}
	}
}
