package appeng.core.sync;

import java.io.DataInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import appeng.core.sync.packets.PacketConfigButton;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.core.sync.packets.PacketLightning;
import appeng.core.sync.packets.PacketLocalizedChatMsg;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.core.sync.packets.PacketMatterCannon;
import appeng.core.sync.packets.PacketMockExplosion;
import appeng.core.sync.packets.PacketMultiPart;
import appeng.core.sync.packets.PacketPartPlacement;

public class AppEngPacketHandlerBase
{

	public static Map<Class, PacketTypes> reverseLookup = new HashMap<Class, AppEngPacketHandlerBase.PacketTypes>();

	public enum PacketTypes
	{
		PACKET_INVENTORY_ACTION(PacketInventoryAction.class),

		PACKET_ME_INVENTORY_UPDATE(PacketMEInventoryUpdate.class),

		PACKET_CONFIG_BUTTON(PacketConfigButton.class),

		PACKET_MULTIPART(PacketMultiPart.class),

		PACKET_PARTPLACEMENT(PacketPartPlacement.class),

		PACKET_LIGHTNING(PacketLightning.class),

		PACKET_MATTERCANNON(PacketMatterCannon.class),

		PACKET_MOCKEXPLOSION(PacketMockExplosion.class),

		PACKET_LOCALIZED_CHATMSG(PacketLocalizedChatMsg.class);

		final public Class pc;
		final public Constructor con;

		private PacketTypes(Class c) {
			pc = c;

			Constructor x = null;
			try
			{
				x = pc.getConstructor( DataInputStream.class );
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (SecurityException e)
			{
			}

			con = x;
			AppEngPacketHandlerBase.reverseLookup.put( pc, this );

			if ( con == null )
				throw new RuntimeException( "Invalid Packet Class, must be constructable on DataInputStream" );
		}

		AppEngPacket parsePacket(DataInputStream in) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			return (AppEngPacket) con.newInstance( in );
		}

		public static PacketTypes getPacket(int id)
		{
			return (values())[id];
		}

		public static PacketTypes getID(Class<? extends AppEngPacket> c)
		{
			return AppEngPacketHandlerBase.reverseLookup.get( c );
		}

	};

}