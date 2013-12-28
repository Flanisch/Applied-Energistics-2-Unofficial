package appeng.block.networking;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseItemBlockChargeable;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockEnergyCube;
import appeng.client.texture.ExtraTextures;
import appeng.core.features.AEFeature;
import appeng.tile.networking.TileEnergyCell;
import appeng.util.Platform;

public class BlockEnergyCell extends AEBaseBlock
{

	public double getMaxPower()
	{
		return 200000.0;
	}

	public BlockEnergyCell(Class c) {
		super( c, Material.glass );
	}

	public BlockEnergyCell() {
		this( BlockEnergyCell.class );
		setfeature( EnumSet.of( AEFeature.Core ) );
		setTileEntiy( TileEnergyCell.class );
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List list)
	{
		super.getSubBlocks( id, tab, list );

		ItemStack charged = new ItemStack( this, 1 );
		NBTTagCompound tag = Platform.openNbtData( charged );
		tag.setDouble( "internalCurrentPower", getMaxPower() );
		tag.setDouble( "internalMaxPower", getMaxPower() );
		list.add( charged );
	}

	@Override
	protected Class<? extends BaseBlockRender> getRenderer()
	{
		return RenderBlockEnergyCube.class;
	}

	@Override
	public Icon getIcon(int direction, int metadata)
	{
		switch (metadata)
		{
		case 0:
			return ExtraTextures.MEEnergyCell0.getIcon();
		case 1:
			return ExtraTextures.MEEnergyCell1.getIcon();
		case 2:
			return ExtraTextures.MEEnergyCell2.getIcon();
		case 3:
			return ExtraTextures.MEEnergyCell3.getIcon();
		case 4:
			return ExtraTextures.MEEnergyCell4.getIcon();
		case 5:
			return ExtraTextures.MEEnergyCell5.getIcon();
		case 6:
			return ExtraTextures.MEEnergyCell6.getIcon();
		case 7:
			return ExtraTextures.MEEnergyCell7.getIcon();

		}
		return super.getIcon( direction, metadata );
	}

	@Override
	public Class getItemBlockClass()
	{
		return AEBaseItemBlockChargeable.class;
	}

}