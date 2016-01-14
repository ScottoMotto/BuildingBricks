package com.hea3ven.buildingbricks.core.client.settings;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.hea3ven.buildingbricks.core.ModBuildingBricks;
import com.hea3ven.buildingbricks.core.materials.MaterialBlockType;
import com.hea3ven.buildingbricks.core.network.TrowelRotateBlockTypeMessage;

@SideOnly(Side.CLIENT)
public class TrowelKeyBindings {
	public static Consumer<KeyInputEvent> getOnTrowelPrev() {
		return new Consumer<KeyInputEvent>() {
			@Override
			public void accept(KeyInputEvent event) {
				ItemStack stack = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
				if (stack != null && stack.getItem() == ModBuildingBricks.trowel) {
					TrowelRotateBlockTypeMessage.send(false, null);
				}
			}
		};
	}

	public static Consumer<KeyInputEvent> getOnTrowelNext() {
		return new Consumer<KeyInputEvent>() {
			@Override
			public void accept(KeyInputEvent event) {
				ItemStack stack = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
				if (stack != null && stack.getItem() == ModBuildingBricks.trowel) {
					TrowelRotateBlockTypeMessage.send(true, null);
				}
			}
		};
	}

	public static Function<MouseEvent, Boolean> getOnTrowelScroll() {
		return new Function<MouseEvent, Boolean>() {
			@Override
			public Boolean apply(MouseEvent event) {
				TrowelRotateBlockTypeMessage.send(event.dwheel < 0, null);
				return true;
			}
		};
	}

	public static Consumer<KeyInputEvent> getOnTrowelSelect(final MaterialBlockType blockType) {
		return new Consumer<KeyInputEvent>() {
			@Override
			public void accept(KeyInputEvent event) {
				TrowelRotateBlockTypeMessage.send(false, blockType);
			}
		};
	}
}
