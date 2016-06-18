package com.hea3ven.buildingbricks.core.item;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.hea3ven.buildingbricks.core.ModBuildingBricks;
import com.hea3ven.buildingbricks.core.client.gui.GuiTrowel;
import com.hea3ven.buildingbricks.core.inventory.MaterialItemStackConsumer;
import com.hea3ven.buildingbricks.core.inventory.SlotTrowelBlockType;
import com.hea3ven.buildingbricks.core.inventory.SlotTrowelMaterial;
import com.hea3ven.buildingbricks.core.materials.Material;
import com.hea3ven.buildingbricks.core.materials.MaterialBlockType;
import com.hea3ven.buildingbricks.core.materials.MaterialRegistry;
import com.hea3ven.buildingbricks.core.materials.MaterialStack.ItemMaterial;
import com.hea3ven.tools.commonutils.inventory.GenericContainer;

public class ItemTrowel extends Item implements ItemMaterial {

	public static boolean trowelsInCreative = true;

	public ItemTrowel() {
		setHasSubtypes(true);
	}

	@Override
	public void setMaterial(ItemStack stack, Material mat) {
		if (mat == null) {
			if (stack.hasTagCompound())
				stack.getTagCompound().removeTag("material");
		} else {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setString("material", mat.getMaterialId());
		}
	}

	@Override
	public Material getMaterial(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("material", NBT.TAG_STRING))
			return MaterialRegistry.get(stack.getTagCompound().getString("material"));
		else
			return null;
	}

	@Override
	public int getMetadata(ItemStack stack) {
		Material mat = getMaterial(stack);
		return mat == null ? 0 : MaterialRegistry.getMeta(mat);
	}

	@Override
	public int getDamage(ItemStack stack) {
		return getMetadata(stack);
	}

	public MaterialBlockType getCurrentBlockType(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			Material result = getMaterial(stack);
			if (result == null)
				return null;
			setCurrentBlockType(stack, result.getBlockRotation().getFirst());
		}
		MaterialBlockType blockType =
				MaterialBlockType.getBlockType(stack.getTagCompound().getInteger("blockType"));
		Material mat = getMaterial(stack);
		if (mat != null && mat.getBlock(blockType) == null) {
			blockType = mat.getBlockRotation().getNext(blockType);
			setCurrentBlockType(stack, blockType);
		}
		return blockType;
	}

	public void setCurrentBlockType(ItemStack stack, MaterialBlockType blockType) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("blockType", blockType.ordinal());
	}

	public void setNextBlockRotation(ItemStack stack) {
		Material mat = getMaterial(stack);
		if (mat != null) {
			MaterialBlockType blockType = getCurrentBlockType(stack);
			blockType = mat.getBlockRotation().getNext(blockType);
			setCurrentBlockType(stack, blockType);
		}
	}

	public void setPrevBlockRotation(ItemStack stack) {
		Material mat = getMaterial(stack);
		if (mat != null) {
			MaterialBlockType blockType = getCurrentBlockType(stack);
			blockType = mat.getBlockRotation().getPrev(blockType);
			setCurrentBlockType(stack, blockType);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn, EnumHand hand) {
		if (playerIn.isSneaking()) {
			playerIn.openGui(ModBuildingBricks.MODID, GuiTrowel.ID, worldIn,
					MathHelper.floor_double(playerIn.posX), MathHelper.floor_double(playerIn.posY),
					MathHelper.floor_double(playerIn.posZ));
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		Material mat = getMaterial(stack);
		if (mat == null)
			return super.onItemUse(stack, player, world, pos, hand, side, hitX, hitY, hitZ);
		else {
			MaterialBlockType blockType = getCurrentBlockType(stack);
			ItemStack useStack = mat.getBlock(blockType).getStack().copy();
			MaterialItemStackConsumer consumer =
					new MaterialItemStackConsumer(blockType, mat, new PlayerMainInvWrapper(player.inventory));
			if (!player.capabilities.isCreativeMode && consumer.failed())
				return EnumActionResult.FAIL;
			if (useStack.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ) == EnumActionResult.FAIL)
				return EnumActionResult.FAIL;
			if (!player.capabilities.isCreativeMode)
				consumer.apply(world, player.getPosition());
			return EnumActionResult.SUCCESS;
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Material mat = getMaterial(stack);
		if (mat == null)
			return super.getItemStackDisplayName(stack);
		else
			return I18n.translateToLocalFormatted("item.buildingbricks.trowelBinded.name",
					mat.getLocalizedName());
	}

	public Container getContainer(EntityPlayer player) {
		return new GenericContainer()
				.addGenericSlots(44, 36, 1, 1, (slot, x, y) -> new SlotTrowelMaterial(player, slot, x, y))
				.addGenericSlots(98, 9, 4, 4, (slot, x, y) -> new SlotTrowelBlockType(player, slot, x, y))
				.addPlayerSlots(player.inventory, ImmutableSet.of(player.inventory.currentItem));
	}

	public ItemStack createStack() {
		return createStack(null);
	}

	public ItemStack createStack(Material mat) {
		if (mat == null)
			return new ItemStack(this);

		ItemStack stack = new ItemStack(this);
		setMaterial(stack, mat);
		return stack;
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(createStack());
		if (trowelsInCreative) {
			subItems.addAll(
					MaterialRegistry.getAll().stream().map(this::createStack).collect(Collectors.toList()));
		}
	}
}
