package vn.pmgteam.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeathShardParticle extends Particle {

    public DeathShardParticle(World world,
                              double x, double y, double z,
                              double mx, double my, double mz) {
        super(world, x, y, z, mx, my, mz);

        this.particleGravity = 0.04F;
        this.particleMaxAge = 20 + rand.nextInt(10);
        this.particleScale = 0.6F + rand.nextFloat() * 0.4F;

        // màu trắng hơi xanh (SAO style)
        this.particleRed   = 0.8F;
        this.particleGreen = 0.9F;
        this.particleBlue  = 1.0F;

        this.canCollide = false;
        
        this.setParticleTexture(
        	    Minecraft.getMinecraft()
        	        .getTextureMapBlocks()
        	        .getAtlasSprite("minecraft:particles/generic")
        	);

    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // fade out
        this.particleAlpha = 1.0F - (float) particleAge / particleMaxAge;

        // giảm tốc dần
        this.motionX *= 0.96;
        this.motionY *= 0.92;
        this.motionZ *= 0.96;
    }

    @Override
    public int getFXLayer() {
        return 1; // particle texture sheet
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity,
                               float partialTicks,
                               float rotationX, float rotationZ,
                               float rotationYZ, float rotationXY,
                               float rotationXZ) {

        super.renderParticle(buffer, entity,
                partialTicks, rotationX, rotationZ,
                rotationYZ, rotationXY, rotationXZ);
    }
}
