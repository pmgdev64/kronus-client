package vn.pmgteam.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class DeathParticleHandler {

    public static final Map<Integer, ParticleTransform> TRANSFORM_POOL
            = new HashMap<>();

    public static void spawn(EntityLivingBase entity) {

        int id = entity.getEntityId();

        ParticleTransform t = new ParticleTransform();
        AxisAlignedBB bb = entity.getEntityBoundingBox();

        t.min = new Vec3d(bb.minX, bb.minY, bb.minZ);
        t.max = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
        t.offset = new Vec3d(0, 0, 0);
        t.density = 6.0F;

        TRANSFORM_POOL.put(id, t);

        Minecraft.getMinecraft().effectRenderer.addEffect(
                new DeathControllerParticle(
                        entity.world,
                        entity.posX,
                        entity.posY + entity.height / 2,
                        entity.posZ,
                        Double.longBitsToDouble(id)
                )
        );
    }

    public static class ParticleTransform {
        public Vec3d min;
        public Vec3d max;
        public Vec3d offset;
        public float density;
    }
}
