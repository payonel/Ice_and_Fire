package com.github.alexthe666.iceandfire.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

import com.github.alexthe666.iceandfire.IceAndFire;

public class EntityDragonFire extends EntityFireball {

	public EntityDragonFire(World worldIn) {
		super(worldIn);
	}

	public EntityDragonFire(World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
		super(worldIn, posX, posY, posZ, accelX, accelY, accelZ);
	}

	public EntityDragonFire(World worldIn, EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn, shooter, accelX, accelY, accelZ);
		this.setSize(0.5F, 0.5F);
		double d0 = (double) MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationY = accelY / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationZ = accelZ / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
	}

	public void setSizes(float width, float height) {
		this.setSize(width, height);
	}

	protected boolean isFireballFiery() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		for (int i = 0; i < 6; ++i) {
			IceAndFire.PROXY.spawnParticle("dragonfire", worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}
		if (ticksExisted > 160) {
			setDead();
		}
		if (this.onGround) {
		}
	}

	@Override
	protected void onImpact(RayTraceResult movingObject) {

		if (!this.worldObj.isRemote) {

			if (movingObject.entityHit != null && !(movingObject.entityHit instanceof EntityDragonFire) && movingObject.entityHit != shootingEntity || movingObject.entityHit == null) {
				if (this.shootingEntity != null && (movingObject.entityHit == this.shootingEntity || (this.shootingEntity instanceof EntityDragonBase & movingObject.entityHit instanceof EntityTameable && ((EntityDragonBase) shootingEntity).getOwner() == ((EntityTameable) movingObject.entityHit).getOwner()))) {
					return;
				}
				FireExplosion explosion = new FireExplosion(worldObj, shootingEntity, this.posX, this.posY, this.posZ, ((EntityDragonBase)this.shootingEntity).getDragonStage(), true);
				explosion.doExplosionA();
				explosion.doExplosionB(true);
				this.setDead();

			}
			if (movingObject.entityHit != null && !(movingObject.entityHit instanceof EntityDragonFire) && movingObject.entityHit != shootingEntity) {
				if (this.shootingEntity != null && (movingObject.entityHit == this.shootingEntity || (this.shootingEntity instanceof EntityDragonBase & movingObject.entityHit instanceof EntityTameable && ((EntityDragonBase) shootingEntity).getOwner() == ((EntityTameable) movingObject.entityHit).getOwner()))) {
					return;
				}
				movingObject.entityHit.attackEntityFrom(IceAndFire.dragonFire, 1.0F);
				this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
				movingObject.entityHit.setFire(3);
				this.setDead();
			}

			if (movingObject.typeOfHit != Type.ENTITY || movingObject.entityHit != null && !(movingObject.entityHit instanceof EntityDragonFire)) {
				boolean flag = this.worldObj.getGameRules().getBoolean("mobGriefing");
				this.setDead();
			}
		}
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	public void setAim(Entity fireball, Entity entity, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
		float f = -MathHelper.sin(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
		float f1 = -MathHelper.sin(p_184547_2_ * 0.017453292F);
		float f2 = MathHelper.cos(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
		this.setThrowableHeading(fireball, (double) f, (double) f1, (double) f2, p_184547_5_, p_184547_6_);
		fireball.motionX += entity.motionX;
		fireball.motionZ += entity.motionZ;

		if (!entity.onGround) {
			fireball.motionY += entity.motionY;
		}
	}

	public void setThrowableHeading(Entity fireball, double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		fireball.motionX = x;
		fireball.motionY = y;
		fireball.motionZ = z;
		float f1 = MathHelper.sqrt_double(x * x + z * z);
		fireball.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
		fireball.rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
		fireball.prevRotationYaw = fireball.rotationYaw;
		fireball.prevRotationPitch = fireball.rotationPitch;
	}

	public float getCollisionBorderSize() {
		return 0F;
	}

}