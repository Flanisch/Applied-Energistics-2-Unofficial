package appeng.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ExtraTextures
{
	BlockVibrationChamberFrontOn("BlockVibrationChamberFrontOn"),

	OreQuartzStone("OreQuartzStone"),

	MEChest("BlockMEChest"), BlockMEChestItems("BlockMEChestItems"),

	BlockControllerPowered("BlockControllerPowered"), BlockControllerColumnPowered("BlockControllerColumnPowered"), BlockControllerColumn(
			"BlockControllerColumn"), BlockControllerLights("BlockControllerLights"), BlockControllerColumnLights("BlockControllerColumnLights"), BlockControllerColumnConflict(
			"BlockControllerColumnConflict"), BlockControllerConflict("BlockControllerConflict"), BlockControllerInsideA("BlockControllerInsideA"), BlockControllerInsideB(
			"BlockControllerInsideB"),

	BlockChargerInside("BlockChargerInside"),

	BlockContainmentWallMerged("BlockContainmentWallMerged"), BlockHeatVentMerged("BlockHeatVentMerged"),

	MEStorageCellTextures("MEStorageCellTextures"), White("White"),

	BlockInterfaceAlternate("BlockInterfaceAlternate"), BlockInterfaceAlternateArrow("BlockInterfaceAlternateArrow"),

	BlockMatterCannonParticle("BlockMatterCannonParticle"), BlockEnergyParticle("BlockEnergyParticle"),

	GlassFrame("BlockQuartzGlassFrame"),

	MEDenseEnergyCell0("BlockDenseEnergyCell0"), MEDenseEnergyCell1("BlockDenseEnergyCell1"), MEDenseEnergyCell2("BlockDenseEnergyCell2"), MEDenseEnergyCell3(
			"BlockDenseEnergyCell3"), MEDenseEnergyCell4("BlockDenseEnergyCell4"), MEDenseEnergyCell5("BlockDenseEnergyCell5"), MEDenseEnergyCell6(
			"BlockDenseEnergyCell6"), MEDenseEnergyCell7("BlockDenseEnergyCell7"),

	MEEnergyCell0("BlockEnergyCell0"), MEEnergyCell1("BlockEnergyCell1"), MEEnergyCell2("BlockEnergyCell2"), MEEnergyCell3("BlockEnergyCell3"), MEEnergyCell4(
			"BlockEnergyCell4"), MEEnergyCell5("BlockEnergyCell5"), MEEnergyCell6("BlockEnergyCell6"), MEEnergyCell7("BlockEnergyCell7"),

	BlockSpatialPylon_dim("BlockSpatialPylon_dim"), BlockSpatialPylon_red("BlockSpatialPylon_red"),

	BlockSpatialPylonC("BlockSpatialPylon_spanned"), BlockSpatialPylonC_dim("BlockSpatialPylon_spanned_dim"), BlockSpatialPylonC_red(
			"BlockSpatialPylon_spanned_red"),

	BlockSpatialPylonE("BlockSpatialPylon_end"), BlockSpatialPylonE_dim("BlockSpatialPylon_end_dim"), BlockSpatialPylonE_red("BlockSpatialPylon_end_red");

	final private String name;
	public Icon icon;

	public static ResourceLocation GuiTexture(String string)
	{
		return new ResourceLocation( "appliedenergistics2", "textures/" + string );
	}

	public String getName()
	{
		return name;
	}

	private ExtraTextures(String name) {
		this.name = name;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public void registerIcon(TextureMap map)
	{
		icon = map.registerIcon( "appliedenergistics2:" + name );
	}

	@SideOnly(Side.CLIENT)
	public static Icon getMissing()
	{
		return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture( TextureMap.locationBlocksTexture )).getAtlasSprite( "missingno" );
	}
}