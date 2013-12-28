package appeng.parts.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import appeng.api.AEApi;
import appeng.api.implementations.IPartCable;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartCollsionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.IReadOnlyCollection;
import appeng.block.AEBaseBlock;
import appeng.client.texture.CableBusTextures;
import appeng.client.texture.FlipableIcon;
import appeng.client.texture.TaughtIcon;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.parts.AEBasePart;
import appeng.util.Platform;

public class PartCable extends AEBasePart implements IPartCable
{

	int channelsOnSide[] = new int[] { 0, 0, 0, 0, 0, 0 };

	EnumSet<ForgeDirection> connections = EnumSet.noneOf( ForgeDirection.class );
	boolean powered = false;

	public PartCable(Class c, ItemStack is) {
		super( c, is );
		proxy.setIdlePowerUsage( 1.0 / 16.0 );
		proxy.myColor = AEColor.fromCable( is );
	}

	public Icon getGlassTexture(AEColor c)
	{
		switch (c)
		{
		case Black:
			return CableBusTextures.MECable_Black.getIcon();
		case Blue:
			return CableBusTextures.MECable_Blue.getIcon();
		case Brown:
			return CableBusTextures.MECable_Brown.getIcon();
		case Cyan:
			return CableBusTextures.MECable_Cyan.getIcon();
		case Gray:
			return CableBusTextures.MECable_Grey.getIcon();
		case Green:
			return CableBusTextures.MECable_Green.getIcon();
		case LightBlue:
			return CableBusTextures.MECable_LightBlue.getIcon();
		case LightGray:
			return CableBusTextures.MECable_LightGrey.getIcon();
		case Lime:
			return CableBusTextures.MECable_Lime.getIcon();
		case Magenta:
			return CableBusTextures.MECable_Magenta.getIcon();
		case Orange:
			return CableBusTextures.MECable_Orange.getIcon();
		case Pink:
			return CableBusTextures.MECable_Pink.getIcon();
		case Purple:
			return CableBusTextures.MECable_Purple.getIcon();
		case Red:
			return CableBusTextures.MECable_Red.getIcon();
		case White:
			return CableBusTextures.MECable_White.getIcon();
		case Yellow:
			return CableBusTextures.MECable_Yellow.getIcon();
		default:
		}
		return is.getIconIndex();
	}

	public Icon getTexture(AEColor c)
	{
		return getGlassTexture( c );
	}

	public Icon getCoveredTexture(AEColor c)
	{
		switch (c)
		{
		case Black:
			return CableBusTextures.MECovered_Black.getIcon();
		case Blue:
			return CableBusTextures.MECovered_Blue.getIcon();
		case Brown:
			return CableBusTextures.MECovered_Brown.getIcon();
		case Cyan:
			return CableBusTextures.MECovered_Cyan.getIcon();
		case Gray:
			return CableBusTextures.MECovered_Gray.getIcon();
		case Green:
			return CableBusTextures.MECovered_Green.getIcon();
		case LightBlue:
			return CableBusTextures.MECovered_LightBlue.getIcon();
		case LightGray:
			return CableBusTextures.MECovered_LightGrey.getIcon();
		case Lime:
			return CableBusTextures.MECovered_Lime.getIcon();
		case Magenta:
			return CableBusTextures.MECovered_Magenta.getIcon();
		case Orange:
			return CableBusTextures.MECovered_Orange.getIcon();
		case Pink:
			return CableBusTextures.MECovered_Pink.getIcon();
		case Purple:
			return CableBusTextures.MECovered_Purple.getIcon();
		case Red:
			return CableBusTextures.MECovered_Red.getIcon();
		case White:
			return CableBusTextures.MECovered_White.getIcon();
		case Yellow:
			return CableBusTextures.MECovered_Yellow.getIcon();
		default:
		}
		return AEApi.instance().parts().partCableCovered.item().getIconIndex( AEApi.instance().parts().partCableCovered.stack( 1 ) );
	}

	public Icon getSmartTexture(AEColor c)
	{
		switch (c)
		{
		case Black:
			return CableBusTextures.MESmart_Black.getIcon();
		case Blue:
			return CableBusTextures.MESmart_Blue.getIcon();
		case Brown:
			return CableBusTextures.MESmart_Brown.getIcon();
		case Cyan:
			return CableBusTextures.MESmart_Cyan.getIcon();
		case Gray:
			return CableBusTextures.MESmart_Gray.getIcon();
		case Green:
			return CableBusTextures.MESmart_Green.getIcon();
		case LightBlue:
			return CableBusTextures.MESmart_LightBlue.getIcon();
		case LightGray:
			return CableBusTextures.MESmart_LightGrey.getIcon();
		case Lime:
			return CableBusTextures.MESmart_Lime.getIcon();
		case Magenta:
			return CableBusTextures.MESmart_Magenta.getIcon();
		case Orange:
			return CableBusTextures.MESmart_Orange.getIcon();
		case Pink:
			return CableBusTextures.MESmart_Pink.getIcon();
		case Purple:
			return CableBusTextures.MESmart_Purple.getIcon();
		case Red:
			return CableBusTextures.MESmart_Red.getIcon();
		case White:
			return CableBusTextures.MESmart_White.getIcon();
		case Yellow:
			return CableBusTextures.MESmart_Yellow.getIcon();
		default:
		}

		return is.getIconIndex();
	}

	@Override
	public AEColor getCableColor()
	{
		return proxy.myColor;
	}

	@Override
	public AECableType getCableConnectionType()
	{
		return AECableType.GLASS;
	}

	public AENetworkProxy getProxy()
	{
		return proxy;
	}

	public void markForUpdate()
	{
		getHost().markForUpdate();
	}

	@Override
	public void writeToStream(DataOutputStream data) throws IOException
	{
		int cs = 0;
		int sideOut = 0;

		IGridNode n = getGridNode();
		if ( n != null )
		{
			for (ForgeDirection thisSide : ForgeDirection.VALID_DIRECTIONS)
			{
				IPart part = getHost().getPart( thisSide );
				if ( part != null )
				{
					if ( part.getGridNode() != null )
					{
						IReadOnlyCollection<IGridConnection> set = part.getGridNode().getConnections();
						for (IGridConnection gc : set)
						{
							sideOut |= gc.getUsedChannels() << (4 * thisSide.ordinal());
						}
					}
				}
			}

			for (IGridConnection gc : n.getConnections())
			{
				ForgeDirection side = gc.getDirection( n );
				if ( side != ForgeDirection.UNKNOWN )
				{
					sideOut |= gc.getUsedChannels() << (4 * side.ordinal());
					cs |= (1 << side.ordinal());
				}
			}
		}

		try
		{
			if ( proxy.getEnergy().isNetworkPowered() )
				cs |= (1 << ForgeDirection.UNKNOWN.ordinal());
		}
		catch (GridAccessException e)
		{
			// aww...
		}

		data.writeByte( (byte) cs );
		data.writeInt( sideOut );
	}

	@Override
	public boolean readFromStream(DataInputStream data) throws IOException
	{
		int cs = data.readByte();
		int sideOut = data.readInt();

		EnumSet<ForgeDirection> myC = connections;
		boolean wasPowered = powered;
		powered = false;
		boolean chchanged = false;

		for (ForgeDirection d : ForgeDirection.values())
		{
			if ( d != ForgeDirection.UNKNOWN )
			{
				int ch = (sideOut >> (d.ordinal() * 4)) & 0xF;
				if ( ch != channelsOnSide[d.ordinal()] )
				{
					chchanged = true;
					channelsOnSide[d.ordinal()] = ch;
				}
			}

			if ( d == ForgeDirection.UNKNOWN )
			{
				int id = 1 << d.ordinal();
				if ( id == (cs & id) )
					powered = true;
			}
			else
			{
				int id = 1 << d.ordinal();
				if ( id == (cs & id) )
					connections.add( d );
				else
					connections.remove( d );
			}
		}

		return !myC.equals( connections ) || wasPowered != powered || chchanged;
	}

	@Override
	public void getBoxes(IPartCollsionHelper bch)
	{
		bch.addBox( 6.0, 6.0, 6.0, 10.0, 10.0, 10.0 );

		if ( Platform.isServer() )
		{
			IGridNode n = getGridNode();
			if ( n != null )
				connections = n.getConnectedSides();
			else
				connections.clear();
		}

		for (ForgeDirection of : connections)
		{
			switch (of)
			{
			case DOWN:
				bch.addBox( 6.0, 0.0, 6.0, 10.0, 6.0, 10.0 );
				break;
			case EAST:
				bch.addBox( 10.0, 6.0, 6.0, 16.0, 10.0, 10.0 );
				break;
			case NORTH:
				bch.addBox( 6.0, 6.0, 0.0, 10.0, 10.0, 6.0 );
				break;
			case SOUTH:
				bch.addBox( 6.0, 6.0, 10.0, 10.0, 10.0, 16.0 );
				break;
			case UP:
				bch.addBox( 6.0, 10.0, 6.0, 10.0, 16.0, 10.0 );
				break;
			case WEST:
				bch.addBox( 0.0, 6.0, 6.0, 6.0, 10.0, 10.0 );
				break;
			default:
				continue;
			}
		}
	}

	@Override
	public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer)
	{
		GL11.glTranslated( -0.2, -0.3, 0.0 );

		rh.setTexture( getTexture( getCableColor() ) );
		rh.setBounds( 6.0f, 6.0f, 2.0f, 10.0f, 10.0f, 14.0f );
		rh.renderInventoryBox( renderer );
		rh.setTexture( null );
	}

	public void rendereGlassConection(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer, ForgeDirection of)
	{
		TileEntity te = this.tile.worldObj.getBlockTileEntity( x + of.offsetX, y + of.offsetY, z + of.offsetZ );
		IPartHost ccph = te instanceof IPartHost ? (IPartHost) te : null;
		IGridHost gh = te instanceof IGridHost ? (IGridHost) te : null;

		if ( gh != null && ccph != null && gh.getCableConnectionType( of ) == AECableType.GLASS && ccph.getColor() != AEColor.Transparent
				&& ccph.getPart( of.getOpposite() ) == null )
			rh.setTexture( getTexture( ccph.getColor() ) );
		else if ( ccph == null && gh != null && gh.getCableConnectionType( of ) != AECableType.GLASS )
		{
			rh.setTexture( getCoveredTexture( getCableColor() ) );
			switch (of)
			{
			case DOWN:
				rh.setBounds( 5, 0, 5, 11, 4, 11 );
				break;
			case EAST:
				rh.setBounds( 12, 5, 5, 16, 11, 11 );
				break;
			case NORTH:
				rh.setBounds( 5, 5, 0, 11, 11, 4 );
				break;
			case SOUTH:
				rh.setBounds( 5, 5, 12, 11, 11, 16 );
				break;
			case UP:
				rh.setBounds( 5, 12, 5, 11, 16, 11 );
				break;
			case WEST:
				rh.setBounds( 0, 5, 5, 4, 11, 11 );
				break;
			default:
				return;
			}
			rh.renderBlock( x, y, z, renderer );

			rh.setTexture( getTexture( getCableColor() ) );
		}
		else
			rh.setTexture( getTexture( getCableColor() ) );

		switch (of)
		{
		case DOWN:
			rh.setBounds( 6, 0, 6, 10, 6, 10 );
			break;
		case EAST:
			rh.setBounds( 10, 6, 6, 16, 10, 10 );
			break;
		case NORTH:
			rh.setBounds( 6, 6, 0, 10, 10, 6 );
			break;
		case SOUTH:
			rh.setBounds( 6, 6, 10, 10, 10, 16 );
			break;
		case UP:
			rh.setBounds( 6, 10, 6, 10, 16, 10 );
			break;
		case WEST:
			rh.setBounds( 0, 6, 6, 6, 10, 10 );
			break;
		default:
			return;
		}
		rh.renderBlock( x, y, z, renderer );
	}

	protected CableBusTextures getChannelTex(int i, boolean b)
	{
		if ( !powered )
			i = 0;

		if ( b )
		{
			switch (i)
			{
			default:
				return CableBusTextures.Channels10;
			case 5:
				return CableBusTextures.Channels11;
			case 6:
				return CableBusTextures.Channels12;
			case 7:
				return CableBusTextures.Channels13;
			case 8:
				return CableBusTextures.Channels14;
			}
		}
		else
		{
			switch (i)
			{
			case 0:
				return CableBusTextures.Channels00;
			case 1:
				return CableBusTextures.Channels01;
			case 2:
				return CableBusTextures.Channels02;
			case 3:
				return CableBusTextures.Channels03;
			default:
				return CableBusTextures.Channels04;
			}
		}
	}

	public void renderCoveredConection(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer, int channels, ForgeDirection of)
	{
		TileEntity te = this.tile.worldObj.getBlockTileEntity( x + of.offsetX, y + of.offsetY, z + of.offsetZ );
		IPartHost ccph = te instanceof IPartHost ? (IPartHost) te : null;
		IGridHost ghh = te instanceof IGridHost ? (IGridHost) te : null;
		boolean isSmart = false;

		if ( ghh != null && ccph != null && ghh.getCableConnectionType( of ) == AECableType.GLASS && ccph.getPart( of.getOpposite() ) == null )
			rh.setTexture( getGlassTexture( ccph.getColor() ) );
		else if ( ccph == null && ghh != null && ghh.getCableConnectionType( of ) != AECableType.GLASS )
		{
			rh.setTexture( getCoveredTexture( getCableColor() ) );
			switch (of)
			{
			case DOWN:
				rh.setBounds( 5, 0, 5, 11, 4, 11 );
				break;
			case EAST:
				rh.setBounds( 12, 5, 5, 16, 11, 11 );
				break;
			case NORTH:
				rh.setBounds( 5, 5, 0, 11, 11, 4 );
				break;
			case SOUTH:
				rh.setBounds( 5, 5, 12, 11, 11, 16 );
				break;
			case UP:
				rh.setBounds( 5, 12, 5, 11, 16, 11 );
				break;
			case WEST:
				rh.setBounds( 0, 5, 5, 4, 11, 11 );
				break;
			default:
				return;
			}
			rh.renderBlock( x, y, z, renderer );

			rh.setTexture( getTexture( getCableColor() ) );
		}
		else if ( ghh != null && ccph != null && ghh.getCableConnectionType( of ) == AECableType.COVERED && ccph.getColor() != AEColor.Transparent )
			rh.setTexture( getCoveredTexture( ccph.getColor() ) );
		else if ( ghh != null && ccph != null && ghh.getCableConnectionType( of ) == AECableType.SMART )
		{
			isSmart = true;
			rh.setTexture( getSmartTexture( getCableColor() ) );
		}
		else
			rh.setTexture( getCoveredTexture( getCableColor() ) );

		switch (of)
		{
		case DOWN:
			rh.setBounds( 6, 0, 6, 10, 5, 10 );
			break;
		case EAST:
			rh.setBounds( 11, 6, 6, 16, 10, 10 );
			break;
		case NORTH:
			rh.setBounds( 6, 6, 0, 10, 10, 5 );
			break;
		case SOUTH:
			rh.setBounds( 6, 6, 11, 10, 10, 16 );
			break;
		case UP:
			rh.setBounds( 6, 11, 6, 10, 16, 10 );
			break;
		case WEST:
			rh.setBounds( 0, 6, 6, 5, 10, 10 );
			break;
		default:
			return;
		}

		rh.renderBlock( x, y, z, renderer );

		if ( isSmart )
		{
			setSmartConnectionRotations( of, renderer );
			Icon defa = new TaughtIcon( getChannelTex( channels, false ).getIcon(), -0.2f );
			Icon defb = new TaughtIcon( getChannelTex( channels, true ).getIcon(), -0.2f );

			if ( of == ForgeDirection.EAST || of == ForgeDirection.WEST )
			{
				AEBaseBlock blk = (AEBaseBlock) rh.getBlock();
				FlipableIcon ico = blk.getRendererInstance().getTexture( ForgeDirection.EAST );
				ico.setFlip( false, true );
			}

			Tessellator.instance.setBrightness( 15 << 20 | 15 << 5 );
			Tessellator.instance.setColorOpaque_I( getCableColor().mediumVariant );
			rh.setTexture( defa, defa, defa, defa, defa, defa );
			renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

			Tessellator.instance.setColorOpaque_I( getCableColor().whiteVariant );
			rh.setTexture( defb, defb, defb, defb, defb, defb );
			renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

			renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
		}
	}

	public void renderSmartConection(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer, int channels, ForgeDirection of)
	{
		TileEntity te = this.tile.worldObj.getBlockTileEntity( x + of.offsetX, y + of.offsetY, z + of.offsetZ );
		IPartHost ccph = te instanceof IPartHost ? (IPartHost) te : null;
		IGridHost ghh = te instanceof IGridHost ? (IGridHost) te : null;
		boolean isGlass = false;
		AEColor myColor = getCableColor();

		if ( ghh != null && ccph != null && ghh.getCableConnectionType( of ) == AECableType.GLASS && ccph.getPart( of.getOpposite() ) == null )
		{
			isGlass = true;
			rh.setTexture( getGlassTexture( myColor = ccph.getColor() ) );
		}
		else if ( ccph == null && ghh != null && ghh.getCableConnectionType( of ) != AECableType.GLASS )
		{
			rh.setTexture( getSmartTexture( myColor ) );
			switch (of)
			{
			case DOWN:
				rh.setBounds( 5, 0, 5, 11, 4, 11 );
				break;
			case EAST:
				rh.setBounds( 12, 5, 5, 16, 11, 11 );
				break;
			case NORTH:
				rh.setBounds( 5, 5, 0, 11, 11, 4 );
				break;
			case SOUTH:
				rh.setBounds( 5, 5, 12, 11, 11, 16 );
				break;
			case UP:
				rh.setBounds( 5, 12, 5, 11, 16, 11 );
				break;
			case WEST:
				rh.setBounds( 0, 5, 5, 4, 11, 11 );
				break;
			default:
				return;
			}
			rh.renderBlock( x, y, z, renderer );

			if ( true )
			{
				setSmartConnectionRotations( of, renderer );
				Icon defa = new TaughtIcon( getChannelTex( channels, false ).getIcon(), -0.2f );
				Icon defb = new TaughtIcon( getChannelTex( channels, true ).getIcon(), -0.2f );

				if ( of == ForgeDirection.EAST || of == ForgeDirection.WEST )
				{
					AEBaseBlock blk = (AEBaseBlock) rh.getBlock();
					FlipableIcon ico = blk.getRendererInstance().getTexture( ForgeDirection.EAST );
					ico.setFlip( false, true );
				}

				Tessellator.instance.setBrightness( 15 << 20 | 15 << 5 );
				Tessellator.instance.setColorOpaque_I( myColor.mediumVariant );
				rh.setTexture( defa, defa, defa, defa, defa, defa );
				renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

				Tessellator.instance.setColorOpaque_I( myColor.whiteVariant );
				rh.setTexture( defb, defb, defb, defb, defb, defb );
				renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

				renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
			}

			rh.setTexture( getTexture( getCableColor() ) );
		}

		else if ( ghh != null && ccph != null && ghh.getCableConnectionType( of ) != AECableType.GLASS && ccph.getColor() != AEColor.Transparent )
			rh.setTexture( getSmartTexture( myColor = ccph.getColor() ) );
		else
			rh.setTexture( getSmartTexture( getCableColor() ) );

		switch (of)
		{
		case DOWN:
			rh.setBounds( 6, 0, 6, 10, 5, 10 );
			break;
		case EAST:
			rh.setBounds( 11, 6, 6, 16, 10, 10 );
			break;
		case NORTH:
			rh.setBounds( 6, 6, 0, 10, 10, 5 );
			break;
		case SOUTH:
			rh.setBounds( 6, 6, 11, 10, 10, 16 );
			break;
		case UP:
			rh.setBounds( 6, 11, 6, 10, 16, 10 );
			break;
		case WEST:
			rh.setBounds( 0, 6, 6, 5, 10, 10 );
			break;
		default:
			return;
		}

		rh.renderBlock( x, y, z, renderer );

		if ( !isGlass )
		{
			setSmartConnectionRotations( of, renderer );

			Icon defa = new TaughtIcon( getChannelTex( channels, false ).getIcon(), -0.2f );
			Icon defb = new TaughtIcon( getChannelTex( channels, true ).getIcon(), -0.2f );

			Tessellator.instance.setBrightness( 15 << 20 | 15 << 5 );
			Tessellator.instance.setColorOpaque_I( myColor.mediumVariant );
			rh.setTexture( defa, defa, defa, defa, defa, defa );
			renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

			Tessellator.instance.setColorOpaque_I( myColor.whiteVariant );
			rh.setTexture( defb, defb, defb, defb, defb, defb );
			renderAllFaces( (AEBaseBlock) rh.getBlock(), x, y, z, renderer );

			renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
		}
	}

	protected void setSmartConnectionRotations(ForgeDirection of, RenderBlocks renderer)
	{
		switch (of)
		{
		case UP:
		case DOWN:
			renderer.uvRotateTop = 0;
			renderer.uvRotateBottom = 0;
			renderer.uvRotateSouth = 3;
			renderer.uvRotateEast = 3;
			break;
		case NORTH:
		case SOUTH:
			renderer.uvRotateTop = 3;
			renderer.uvRotateBottom = 3;
			renderer.uvRotateNorth = 1;
			renderer.uvRotateSouth = 2;
			renderer.uvRotateWest = 1;
			break;
		case EAST:
		case WEST:
			renderer.uvRotateEast = 2;
			renderer.uvRotateWest = 1;
			renderer.uvRotateBottom = 2;
			renderer.uvRotateTop = 1;
			renderer.uvRotateSouth = 3;
			renderer.uvRotateNorth = 0;
			break;
		default:
			break;

		}
	}

	protected void renderAllFaces(AEBaseBlock blk, int x, int y, int z, RenderBlocks renderer)
	{
		renderer.renderFaceXNeg( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.WEST ) );
		renderer.renderFaceXPos( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.EAST ) );
		renderer.renderFaceZNeg( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.NORTH ) );
		renderer.renderFaceZPos( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.SOUTH ) );
		renderer.renderFaceYNeg( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.DOWN ) );
		renderer.renderFaceYPos( blk, x, y, z, blk.getRendererInstance().getTexture( ForgeDirection.UP ) );
	}

	@Override
	public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer)
	{
		boolean useCovered = false;

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			IPart p = getHost().getPart( dir );
			if ( p != null && p instanceof IGridHost )
			{
				IGridHost igh = (IGridHost) p;
				AECableType type = igh.getCableConnectionType( dir.getOpposite() );
				if ( type == AECableType.COVERED || type == AECableType.SMART )
				{
					useCovered = true;
					break;
				}
			}
		}

		if ( useCovered )
		{
			rh.setTexture( getCoveredTexture( getCableColor() ) );
			rh.setBounds( 5, 5, 5, 11, 11, 11 );
			rh.renderBlock( x, y, z, renderer );
		}
		else
		{
			rh.setTexture( getTexture( getCableColor() ) );
			rh.setBounds( 6, 6, 6, 10, 10, 10 );
			rh.renderBlock( x, y, z, renderer );
		}

		IPartHost ph = getHost();
		for (ForgeDirection of : EnumSet.complementOf( connections ))
		{
			IPart bp = ph.getPart( of );
			if ( bp instanceof IGridHost )
			{
				int len = bp.cableConnectionRenderTo();
				if ( len < 8 )
				{
					switch (of)
					{
					case DOWN:
						rh.setBounds( 6, len, 6, 10, 6, 10 );
						break;
					case EAST:
						rh.setBounds( 10, 6, 6, 16 - len, 10, 10 );
						break;
					case NORTH:
						rh.setBounds( 6, 6, len, 10, 10, 6 );
						break;
					case SOUTH:
						rh.setBounds( 6, 6, 10, 10, 10, 16 - len );
						break;
					case UP:
						rh.setBounds( 6, 10, 6, 10, 16 - len, 10 );
						break;
					case WEST:
						rh.setBounds( len, 6, 6, 6, 10, 10 );
						break;
					default:
						continue;
					}
					rh.renderBlock( x, y, z, renderer );
				}
			}
		}

		for (ForgeDirection of : connections)
		{
			rendereGlassConection( x, y, z, rh, renderer, of );
		}

		rh.setTexture( null );
	}

	@Override
	public boolean changeColor(AEColor newColor)
	{
		if ( getCableColor() != newColor )
		{
			is.setItemDamage( newColor.ordinal() );
			markForUpdate();
			return true;
		}
		return false;
	}

	@Override
	public void setValidSides(EnumSet<ForgeDirection> sides)
	{
		proxy.setValidSides( sides );
	}

	protected boolean nonLinear(EnumSet<ForgeDirection> sides)
	{
		return (sides.contains( ForgeDirection.EAST ) && sides.contains( ForgeDirection.WEST ))
				|| (sides.contains( ForgeDirection.NORTH ) && sides.contains( ForgeDirection.SOUTH ))
				|| (sides.contains( ForgeDirection.UP ) && sides.contains( ForgeDirection.DOWN ));
	}

}