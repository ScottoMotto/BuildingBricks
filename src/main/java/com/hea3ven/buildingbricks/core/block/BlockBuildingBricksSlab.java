package com.hea3ven.buildingbricks.core.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.hea3ven.buildingbricks.core.block.base.BlockBuildingBricks;
import com.hea3ven.buildingbricks.core.materials.MaterialBlockLogic;
import com.hea3ven.buildingbricks.core.materials.MaterialBlockType;
import com.hea3ven.buildingbricks.core.materials.StructureMaterial;

public class BlockBuildingBricksSlab extends BlockSlab implements BlockBuildingBricks {

	protected MaterialBlockLogic blockLogic;

	public BlockBuildingBricksSlab(StructureMaterial structMat) {
		super(structMat.getMcMaterial());
		setSoundType(structMat.getSoundType());
		useNeighborBrightness = true;

		blockLogic = new MaterialBlockLogic(structMat, MaterialBlockType.SLAB);
		blockLogic.initBlock(this);

		IBlockState state = getDefaultState();
		state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
		setDefaultState(state);
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return getUnlocalizedName();
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	@Override
	public IProperty getVariantProperty() {
		return null;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return null;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta |= state.getValue(HALF).ordinal();
		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState();
		state = state.withProperty(HALF, (meta & 0x1) == 1 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
		return state;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos,
			EnumFacing face) {
		return getMaterial(state).isOpaque() && super.doesSideBlockRendering(state, world, pos, face);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos,
			EnumFacing side) {
		BlockPos otherPos = pos.offset(side);
		IBlockState otherState = world.getBlockState(otherPos);
		if (side.getAxis() != Axis.Y) {
			if (!(otherState.getBlock() instanceof BlockSlab) ||
					((BlockSlab) otherState.getBlock()).isDouble())
				return !otherState.doesSideBlockRendering(world, otherPos, side.getOpposite());
			else
				return otherState.getValue(HALF) != state.getValue(HALF) ||
						otherState.getMaterial() != state.getMaterial();
		}

		if (state.getValue(HALF) == EnumBlockHalf.BOTTOM)
			return side != EnumFacing.DOWN || otherState.getMaterial() != state.getMaterial() ||
					!otherState.doesSideBlockRendering(world, otherPos, side.getOpposite());
		else
			return side != EnumFacing.UP || otherState.getMaterial() != state.getMaterial() ||
					!otherState.doesSideBlockRendering(world, otherPos, side.getOpposite());
	}

	//region COMMON BLOCK CODE

	@Override
	public MaterialBlockLogic getBlockLogic() {
		return blockLogic;
	}

	@Override
	public boolean requiresUpdates() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return blockLogic.getBlockLayer();
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return 0;
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return blockLogic.getHarvestTool(state);
	}

	//endregion COMMON BLOCK CODE
}
