package appeng.items.tools.quartz;

import java.util.EnumSet;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraftforge.common.MinecraftForge;
import appeng.core.Configuration;
import appeng.core.features.AEFeature;
import appeng.core.features.AEFeatureHandler;
import appeng.core.features.IAEFeature;

public class ToolQuartzAxe extends ItemAxe implements IAEFeature
{

	AEFeatureHandler feature;

	@Override
	public AEFeatureHandler feature()
	{
		return feature;
	}

	public ToolQuartzAxe(AEFeature Type) {
		super( Configuration.instance.getItemID( ToolQuartzAxe.class, Type.name() ), EnumToolMaterial.IRON );
		MinecraftForge.setToolClass( this, "axe", EnumToolMaterial.IRON.getHarvestLevel() );
		feature = new AEFeatureHandler( EnumSet.of( Type, AEFeature.QuartzAxe ), this, Type.name() );
	}

}