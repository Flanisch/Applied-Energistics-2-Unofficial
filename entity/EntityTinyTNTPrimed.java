package appeng.entity;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import appeng.api.AEApi;
import appeng.core.CommonHelper;
import appeng.core.Configuration;
import appeng.core.features.AEFeature;
import appeng.core.sync.packets.PacketMockExplosion;
import appeng.util.Platform;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

final public class EntityTinyTNTPrimed extends EntityTNTPrimed implements IEntityAdditionalSpawnData
{

	public EntityTinyTNTPrimed(World w) {
		super( w );
		this.setSize( 0.35F, 0.35F );
	}

	public EntityTinyTNTPrimed(World w, double x, double y, double z, EntityLivingBase ignitor) {
		super( w, x, y, z, ignitor );
		this.setSize( 0.55F, 0.55F );
		this.yOffset = this.height / 2.0F;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		this.handleWaterMovement();

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.moveEntity( this.motionX, this.motionY, this.motionZ );
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if ( this.onGround )
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}

		if ( this.isInWater() && Platform.isServer() ) // put out the fuse.
		{
			EntityItem item = new EntityItem( worldObj, this.posX, this.posY, this.posZ, AEApi.instance().blocks().blockTinyTNT.stack( 1 ) );
			item.motionX = motionX;
			item.motionY = motionY;
			item.motionZ = motionZ;
			item.prevPosX = this.prevPosX;
			item.prevPosY = this.prevPosY;
			item.prevPosZ = this.prevPosZ;
			worldObj.spawnEntityInWorld( item );
			this.setDead();
		}

		if ( this.fuse-- <= 0 )
		{
			this.setDead();

			if ( !this.worldObj.isRemote )
			{
				this.explode();
			}
		}
		else
		{
			this.worldObj.spawnParticle( "smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D );
		}
	}

	// override :P
	void explode()
	{
		this.worldObj.playSoundEffect( this.posX, this.posY, this.posZ, "random.explode", 4.0F,
				(1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 32.9F );

		if ( this.isInWater() )
		{
			return;
		}

		if ( Configuration.instance.isFeatureEnabled( AEFeature.TinyTNTBlockDamage ) )
		{
			posY -= 0.25;

			for (int x = (int) (posX - 2); x <= posX + 2; x++)
			{
				for (int y = (int) (posY - 2); y <= posY + 2; y++)
				{
					for (int z = (int) (posZ - 2); z <= posZ + 2; z++)
					{
						int l = worldObj.getBlockId( x, y, z );
						Block block = Block.blocksList[l];
						if ( block != null && !block.isAirBlock( worldObj, x, y, z ) )
						{
							float strength = (float) (2.3f - (((x + 0.5f) - posX) * ((x + 0.5f) - posX) + ((y + 0.5f) - posY) * ((y + 0.5f) - posY) + ((z + 0.5f) - posZ)
									* ((z + 0.5f) - posZ)));

							float resistance = block.getExplosionResistance( this, worldObj, x, y, z, posX, posY, posZ );
							strength -= (resistance + 0.3F) * 0.11f;
							if ( strength > 0.01 )
							{
								worldObj.destroyBlock( x, y, z, true );
							}
						}
					}
				}
			}
		}

		try
		{
			CommonHelper.proxy.sendToAllNearExcept( null, posX, posY, posZ, 64, this.worldObj, (new PacketMockExplosion( posX, posY, posZ )).getPacket() );
		}
		catch (IOException e1)
		{
		}

		for (Object e : this.worldObj.getEntitiesWithinAABBExcludingEntity( this,
				AxisAlignedBB.getBoundingBox( this.posX - 1.5, this.posY - 1.5f, this.posZ - 1.5, this.posX + 1.5, this.posY + 1.5, this.posZ + 1.5 ) ))
		{
			if ( e instanceof Entity )
			{
				((Entity) e).attackEntityFrom( DamageSource.setExplosionSource( null ), 6 );
			}

		}
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeByte( fuse );
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		fuse = data.readByte();
	}

}