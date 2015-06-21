package com.hea3ven.buildingbricks.core.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.hea3ven.buildingbricks.ModBuildingBricks;
import com.hea3ven.buildingbricks.core.materials.Material;
import com.hea3ven.buildingbricks.core.materials.MaterialBlockType;

public class EventHandlerTrowelOverlay {

	private ResourceLocation widgetsTexture = new ResourceLocation("textures/gui/widgets.png");

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.HOTBAR) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() == ModBuildingBricks.trowel) {
				Material mat = ModBuildingBricks.trowel.getBindedMaterial(stack);
				if (mat != null) {
					MaterialBlockType type = ModBuildingBricks.trowel.getCurrentBlockType(stack);
					renderTrowelOverlay(event.resolution, player.inventory.currentItem,
							event.partialTicks, mat, type);
				}
			}
		}
	}

	private void renderTrowelOverlay(ScaledResolution sr, int slot, float partialTicks,
			Material mat, MaterialBlockType type) {
		int xPos = sr.getScaledWidth() / 2 - 88 + 10 * 20;
		int yPos = sr.getScaledHeight() - 16 - 3;

		Item item = mat.getBlockItem(type);
		ItemStack stack = new ItemStack(item);

		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(widgetsTexture);
		mc.ingameGUI.drawTexturedModalRect(xPos - 3, yPos - 3, 0, 0, 21, 22);
		mc.ingameGUI.drawTexturedModalRect(xPos + 20, yPos - 3, 181, 0, 1, 22);

		renderItem(partialTicks, xPos, yPos, stack);
	}

	private void renderItem(float partialTicks, int xPos, int yPos, ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();

		float f1 = stack.animationsToGo - partialTicks;

		if (f1 > 0.0F) {
			GlStateManager.pushMatrix();
			float f2 = 1.0F + f1 / 5.0F;
			GlStateManager.translate(xPos + 8, yPos + 12, 0.0F);
			GlStateManager.scale(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
			GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0F);
		}

		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
		if (f1 > 0.0F) {
			GlStateManager.popMatrix();
		}

		mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, xPos, yPos);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

}
