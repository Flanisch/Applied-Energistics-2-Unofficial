package appeng.me;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridConnecitonVisitor;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridVisitor;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IReadOnlyCollection;
import appeng.core.WorldSettings;
import appeng.me.pathfinding.IPathItem;
import appeng.util.ReadOnlyCollection;

public class GridNode implements IGridNode, IPathItem
{

	final static private MENetworkChannelsChanged event = new MENetworkChannelsChanged();

	final List<IGridConnection> Connections = new LinkedList();
	GridStorage myStorage = null;

	IGridBlock gridProxy;
	Grid myGrid;

	public static byte currentVisitorIterationNumber = Byte.MIN_VALUE;
	public static byte currentChannelsIterationNumber = Byte.MIN_VALUE;

	byte visitorIterationNumber = Byte.MIN_VALUE;

	// connection criteria
	AEColor myColor = AEColor.Transparent;
	EnumSet<ForgeDirection> validDirections = EnumSet.noneOf( ForgeDirection.class );

	// old power draw, used to diff
	public double previousDraw = 0.0;

	private int maxChannels = 8;
	private int channelData = 0;

	public int usedChannels()
	{
		return channelData >> 8;
	}

	public AEColor getColor()
	{
		return myColor;
	}

	public GridNode(IGridBlock what) {
		gridProxy = what;
	}

	@Override
	public void loadFromNBT(String name, NBTTagCompound nodeData)
	{
		if ( myGrid == null )
			setGridStorage( WorldSettings.getInstance().getGridStorage( nodeData.getLong( name ) ) );
		else
			throw new RuntimeException( "Loading data after part of a grid, this is invalid." );
	}

	@Override
	public void saveToNBT(String name, NBTTagCompound nodeData)
	{
		if ( myStorage != null )
			nodeData.setLong( name, myStorage.getID() );
		else
			nodeData.removeTag( name );
	}

	@Override
	public IGridBlock getGridBlock()
	{
		return gridProxy;
	}

	@Override
	public EnumSet<ForgeDirection> getConnectedSides()
	{
		EnumSet<ForgeDirection> set = EnumSet.noneOf( ForgeDirection.class );
		for (IGridConnection gc : Connections)
			set.add( gc.getDirection( this ) );
		return set;
	}

	public Class<? extends IGridHost> getMachineClass()
	{
		return getMachine().getClass();
	}

	@Override
	public IGridHost getMachine()
	{
		return gridProxy.getMachine();
	}

	@Override
	public void updateState()
	{
		EnumSet<GridFlags> set = gridProxy.getFlags();
		maxChannels = set.contains( GridFlags.CANNOT_CARRY ) ? 0 : (set.contains( GridFlags.TIER_2_CAPACITY ) ? 32 : 8);
		myColor = gridProxy.getGridColor();
		validDirections = gridProxy.getConnectableSides();
		FindConnections();
		getInternalGrid();
	}

	@Override
	public void beginVisition(IGridVisitor g)
	{
		currentVisitorIterationNumber++;

		LinkedList<GridNode> nextRun = new LinkedList();
		nextRun.add( this );

		visitorIterationNumber = currentVisitorIterationNumber;

		if ( g instanceof IGridConnecitonVisitor )
		{
			LinkedList<IGridConnection> nextConn = new LinkedList();
			IGridConnecitonVisitor gcv = (IGridConnecitonVisitor) g;

			while (!nextRun.isEmpty())
			{
				while (!nextConn.isEmpty())
					gcv.visitConnection( nextConn.poll() );

				LinkedList<GridNode> thisRun = nextRun;
				nextRun = new LinkedList();

				for (GridNode n : thisRun)
					n.visitorConnection( g, nextRun, nextConn );
			}
		}
		else
		{
			while (!nextRun.isEmpty())
			{
				LinkedList<GridNode> thisRun = nextRun;
				nextRun = new LinkedList();

				for (GridNode n : thisRun)
					n.visitorNode( g, nextRun );
			}
		}
	}

	private void visitorConnection(IGridVisitor g, LinkedList<GridNode> nextRun, LinkedList<IGridConnection> nextConnections)
	{
		if ( g.visitNode( this ) )
		{
			for (IGridConnection gc : getConnections())
			{
				GridNode gn = (GridNode) gc.getOtherSide( this );
				GridConnection gcc = (GridConnection) gc;

				if ( gcc.visitorIterationNumber != currentVisitorIterationNumber )
				{
					gcc.visitorIterationNumber = currentChannelsIterationNumber;
					nextConnections.add( gc );
				}

				if ( currentVisitorIterationNumber == gn.visitorIterationNumber )
					continue;

				gn.visitorIterationNumber = currentVisitorIterationNumber;

				nextRun.add( gn );
			}
		}
	}

	private void visitorNode(IGridVisitor g, LinkedList<GridNode> nextRun)
	{
		if ( g.visitNode( this ) )
		{
			for (IGridConnection gc : getConnections())
			{
				GridNode gn = (GridNode) gc.getOtherSide( this );

				if ( currentVisitorIterationNumber == gn.visitorIterationNumber )
					continue;

				gn.visitorIterationNumber = currentVisitorIterationNumber;

				nextRun.add( gn );
			}
		}
	}

	public void FindConnections()
	{
		if ( !gridProxy.isWorldAccessable() )
			return;

		DimensionalCoord dc = gridProxy.getLocation();
		for (ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
		{
			IGridHost te = findGridHost( dc.getWorld(), dc.x + f.offsetX, dc.y + f.offsetY, dc.z + f.offsetZ );
			if ( te != null )
			{
				GridNode node = (GridNode) te.getGridNode( f.getOpposite() );
				if ( node == null )
					continue;

				boolean isValidConnection = this.canConnect( node, f ) && node.canConnect( this, f.getOpposite() );

				IGridConnection con = null; // find the connection for this
											// direction..
				for (IGridConnection c : getConnections())
				{
					if ( c.getDirection( this ) == f )
					{
						con = c;
						break;
					}
				}

				if ( con != null )
				{
					IGridNode os = (IGridNode) con.getOtherSide( this );
					if ( os == node )
					{
						// if this connection is no longer valid, destroy it.
						if ( !isValidConnection )
							con.destroy();
					}
					else
					{
						throw new GridException( "invalid state found, encountered connection to phantom block." );
					}
				}
				else if ( isValidConnection )
				{
					// construct a new connection between these two nodes.
					new GridConnection( node, this, f.getOpposite() );
				}

			}
		}
	}

	private IGridHost findGridHost(World world, int x, int y, int z)
	{
		if ( world.blockExists( x, y, z ) )
		{
			TileEntity te = world.getBlockTileEntity( x, y, z );
			if ( te instanceof IGridHost )
				return (IGridHost) te;
		}
		return null;
	}

	public void addConnection(IGridConnection gridConnection)
	{
		Connections.add( gridConnection );
		if ( gridConnection.hasDirection() )
			gridProxy.onGridNotification( GridNotification.ConnectionsChanged );
	}

	public void removeConnection(IGridConnection gridConnection)
	{
		Connections.remove( gridConnection );
		if ( gridConnection.hasDirection() )
			gridProxy.onGridNotification( GridNotification.ConnectionsChanged );
	}

	@Override
	public IReadOnlyCollection<IGridConnection> getConnections()
	{
		return new ReadOnlyCollection<IGridConnection>( Connections );
	}

	public boolean hasConnection(IGridNode otherside)
	{
		for (IGridConnection gc : Connections)
		{
			if ( gc.a() == otherside || gc.b() == otherside )
				return true;
		}
		return false;
	}

	public boolean canConnect(GridNode from, ForgeDirection dir)
	{
		if ( !validDirections.contains( dir ) )
			return false;

		if ( !from.getColor().matches( getColor() ) )
			return false;

		return true;
	}

	@Override
	public IGrid getGrid()
	{
		return myGrid;
	}

	public Grid getInternalGrid()
	{
		if ( myGrid == null )
			myGrid = new Grid( this );

		return myGrid;
	}

	public void setGrid(Grid grid)
	{
		if ( myGrid == grid )
			return;

		if ( myGrid != null )
		{
			myGrid.remove( this );

			if ( myGrid.isEmpty() )
			{
				myGrid.saveState();

				for (IGridCache c : grid.caches.values())
					c.onJoin( myGrid.myStorage );
			}
		}

		myGrid = grid;
		myGrid.add( this );
	}

	public void validateGrid()
	{
		GridSplitDetector gsd = new GridSplitDetector( getInternalGrid().getPivot() );
		beginVisition( gsd );
		if ( !gsd.pivotFound )
		{
			GridPropagator gp = new GridPropagator( new Grid( this ) );
			beginVisition( gp );
		}
	}

	@Override
	public void destroy()
	{
		while (!Connections.isEmpty())
		{
			// not part of this network for real anymore.
			if ( Connections.size() == 1 )
				setGridStorage( null );

			IGridConnection c = Connections.listIterator().next();
			GridNode otherSide = (GridNode) c.getOtherSide( this );
			otherSide.getInternalGrid().pivot = otherSide;
			c.destroy();
		}

		if ( myGrid != null )
			myGrid.remove( this );
	}

	@Override
	public World getWorld()
	{
		return gridProxy.getLocation().getWorld();
	}

	@Override
	public boolean meetsChannelRequirements()
	{
		return (!getGridBlock().getFlags().contains( GridFlags.REQURE_CHANNEL ) || getUsedChannels() > 0);
	}

	@Override
	public boolean isActive()
	{
		IGrid g = getGrid();
		if ( g != null )
		{
			IPathingGrid pg = g.getCache( IPathingGrid.class );
			IEnergyGrid eg = g.getCache( IEnergyGrid.class );
			return meetsChannelRequirements() && eg.isNetworkPowered() && !pg.isNetworkBooting();
		}
		return false;
	}

	@Override
	public boolean canSupportMoreChannels()
	{
		return getUsedChannels() < maxChannels;
	}

	@Override
	public IReadOnlyCollection<IPathItem> getPossibleOptions()
	{
		return (ReadOnlyCollection) getConnections();
	}

	public int getLastUsedChannels()
	{
		return (channelData >> 8) & 0xff;
	}

	public int getUsedChannels()
	{
		return channelData & 0xff;
	}

	@Override
	public void incrementChannelCount(int usedChannels)
	{
		channelData += usedChannels;
	}

	public void setGridStorage(GridStorage s)
	{
		myStorage = s;
		channelData = 0;
	}

	public GridStorage getGridStorage()
	{
		return myStorage;
	}

	@Override
	public EnumSet<GridFlags> getFlags()
	{
		return getGridBlock().getFlags();
	}

	@Override
	public void finalizeChannels()
	{
		if ( getFlags().contains( GridFlags.CANNOT_CARRY ) )
			return;

		if ( getLastUsedChannels() != getUsedChannels() )
		{
			channelData = (channelData & 0xff);
			channelData |= channelData << 8;

			if ( getInternalGrid() != null )
				getInternalGrid().postEventTo( this, event );
		}
	}

	@Override
	public IPathItem getControllerRoute()
	{
		if ( Connections.isEmpty() || getFlags().contains( GridFlags.CANNOT_CARRY ) )
			return null;

		return (IPathItem) Connections.get( 0 );
	}

	@Override
	public void setControllerRoute(IPathItem fast, boolean zeroOut)
	{
		if ( zeroOut )
			channelData &= ~0xff;

		int idx = Connections.indexOf( fast );
		if ( idx > 0 )
		{
			Connections.remove( fast );
			Connections.add( 0, (IGridConnection) fast );
		}
	}

}