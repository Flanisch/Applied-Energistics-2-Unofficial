package appeng.client.render.blocks;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import appeng.api.util.IOrientable;
import appeng.block.AEBaseBlock;
import appeng.block.misc.BlockQuartzTorch;
import appeng.client.render.BaseBlockRender;

public class RenderQuartzTorch extends BaseBlockRender
{

	public RenderQuartzTorch() {
		super( false, 20 );
	}

	@Override
	public void renderInventory(AEBaseBlock blk, ItemStack is, RenderBlocks renderer)
	{
		Tessellator tess = Tessellator.instance;

		float Point2 = 6.0f / 16.0f;
		float Point3 = 7.0f / 16.0f;
		float Point13 = 10.0f / 16.0f;
		float Point12 = 9.0f / 16.0f;

		float Onepx = 1.0f / 16.0f;
		float rbottom = 5.0f / 16.0f;
		float rtop = 10.0f / 16.0f;

		float bottom = 7.0f / 16.0f;
		float top = 8.0f / 16.0f;

		float xOff = 0.0f;
		float yOff = 0.0f;
		float zOff = 0.0f;

		renderer.setRenderBounds( Point3 + xOff, rbottom + yOff, Point3 + zOff, Point12 + xOff, rtop + yOff, Point12 + zOff );
		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );

		renderer.setRenderBounds( Point3 + xOff, rtop + yOff, Point3 + zOff, Point3 + Onepx + xOff, rtop + Onepx + yOff, Point3 + Onepx + zOff );
		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );

		renderer.setRenderBounds( Point12 - Onepx + xOff, rbottom - Onepx + yOff, Point12 - Onepx + zOff, Point12 + xOff, rbottom + yOff, Point12 + zOff );

		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );

		blk.getRendererInstance().setTemporaryRenderIcon( Block.hopperBlock.getIcon( 0, 0 ) );
		renderer.renderAllFaces = true;

		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point2 + zOff, Point13 + xOff, top + yOff, Point3 + zOff );

		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );
		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point12 + zOff, Point13 + xOff, top + yOff, Point13 + zOff );

		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );
		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point3 + zOff, Point3 + xOff, top + yOff, Point12 + zOff );

		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );
		renderer.setRenderBounds( Point12 + xOff, bottom + yOff, Point3 + zOff, Point13 + xOff, top + yOff, Point12 + zOff );

		renderInvBlock( EnumSet.allOf( ForgeDirection.class ), blk, tess, 0xffffff, renderer );

		renderer.renderAllFaces = false;
		blk.getRendererInstance().setTemporaryRenderIcon( null );

	}

	@Override
	public boolean renderInWorld(AEBaseBlock block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer)
	{
		BlockQuartzTorch blk = (BlockQuartzTorch) block;

		IOrientable te = getOrientable( block, world, x, y, z );

		float Point2 = 6.0f / 16.0f;
		float Point3 = 7.0f / 16.0f;
		float Point13 = 10.0f / 16.0f;
		float Point12 = 9.0f / 16.0f;

		float Onepx = 1.0f / 16.0f;
		float rbottom = 5.0f / 16.0f;
		float rtop = 10.0f / 16.0f;

		float bottom = 7.0f / 16.0f;
		float top = 8.0f / 16.0f;

		float xOff = 0.0f;
		float yOff = 0.0f;
		float zOff = 0.0f;

		renderer.renderAllFaces = true;
		if ( te != null )
		{
			ForgeDirection forward = te.getUp();
			xOff = forward.offsetX * -(4.0f / 16.0f);
			yOff = forward.offsetY * -(4.0f / 16.0f);
			zOff = forward.offsetZ * -(4.0f / 16.0f);
		}

		renderer.setRenderBounds( Point3 + xOff, rbottom + yOff, Point3 + zOff, Point12 + xOff, rtop + yOff, Point12 + zOff );
		super.renderInWorld( block, world, x, y, z, renderer );

		int r = (x + y + z) % 2;
		if ( r == 0 )
		{
			renderer.setRenderBounds( Point3 + xOff, rtop + yOff, Point3 + zOff, Point3 + Onepx + xOff, rtop + Onepx + yOff, Point3 + Onepx + zOff );
			super.renderInWorld( block, world, x, y, z, renderer );

			renderer.setRenderBounds( Point12 - Onepx + xOff, rbottom - Onepx + yOff, Point12 - Onepx + zOff, Point12 + xOff, rbottom + yOff, Point12 + zOff );
			super.renderInWorld( block, world, x, y, z, renderer );
		}
		else
		{
			renderer.setRenderBounds( Point3 + xOff, rbottom - Onepx + yOff, Point3 + zOff, Point3 + Onepx + xOff, rbottom + yOff, Point3 + Onepx + zOff );
			super.renderInWorld( block, world, x, y, z, renderer );

			renderer.setRenderBounds( Point12 - Onepx + xOff, rtop + yOff, Point12 - Onepx + zOff, Point12 + xOff, rtop + Onepx + yOff, Point12 + zOff );
			super.renderInWorld( block, world, x, y, z, renderer );
		}

		blk.getRendererInstance().setTemporaryRenderIcon( Block.hopperBlock.getIcon( 0, 0 ) );

		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point2 + zOff, Point13 + xOff, top + yOff, Point3 + zOff );
		boolean out = renderer.renderStandardBlock( blk, x, y, z );

		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point12 + zOff, Point13 + xOff, top + yOff, Point13 + zOff );
		renderer.renderStandardBlock( blk, x, y, z );

		renderer.setRenderBounds( Point2 + xOff, bottom + yOff, Point3 + zOff, Point3 + xOff, top + yOff, Point12 + zOff );
		renderer.renderStandardBlock( blk, x, y, z );

		renderer.setRenderBounds( Point12 + xOff, bottom + yOff, Point3 + zOff, Point13 + xOff, top + yOff, Point12 + zOff );
		renderer.renderStandardBlock( blk, x, y, z );

		if ( te != null )
		{
			ForgeDirection forward = te.getUp();
			switch (forward)
			{
			case EAST:
				renderer.setRenderBounds( 0, bottom + yOff, bottom + zOff, Point2 + xOff, top + yOff, top + zOff );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			case WEST:
				renderer.setRenderBounds( Point13 + xOff, bottom + yOff, bottom + zOff, 1.0, top + yOff, top + zOff );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			case NORTH:
				renderer.setRenderBounds( bottom + xOff, bottom + yOff, Point13 + zOff, top + xOff, top + yOff, 1.0 );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			case SOUTH:
				renderer.setRenderBounds( bottom + xOff, bottom + yOff, 0, top + xOff, top + yOff, Point2 + zOff );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			case UP:
				renderer.setRenderBounds( Point2, 0, Point2, Point3, bottom + yOff, Point3 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point2, 0, Point12, Point3, bottom + yOff, Point13 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point12, 0, Point2, Point13, bottom + yOff, Point3 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point12, 0, Point12, Point13, bottom + yOff, Point13 );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			case DOWN:
				renderer.setRenderBounds( Point2, top + yOff, Point2, Point3, 1.0, Point3 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point2, top + yOff, Point12, Point3, 1.0, Point13 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point12, top + yOff, Point2, Point13, 1.0, Point3 );
				renderer.renderStandardBlock( blk, x, y, z );
				renderer.setRenderBounds( Point12, top + yOff, Point12, Point13, 1.0, Point13 );
				renderer.renderStandardBlock( blk, x, y, z );
				break;
			default:
			}
		}

		renderer.renderAllFaces = false;
		blk.getRendererInstance().setTemporaryRenderIcon( null );

		return out;
	}
}