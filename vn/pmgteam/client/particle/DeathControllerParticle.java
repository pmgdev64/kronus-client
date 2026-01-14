package vn.pmgteam.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeathControllerParticle extends Particle {

    private int life = 2;

    public DeathControllerParticle(World world,
                                   double x, double y, double z,
                                   double data) {
        super(world, x, y, z, 0, 0, 0);

        int id = (int) Double.doubleToLongBits(data);
        DeathParticleHandler.ParticleTransform t =
                DeathParticleHandler.TRANSFORM_POOL.remove(id);

        if (t != null) {
            spawn(world, x, y, z, t);
        }
    }

    private void spawn(World world, double x, double y, double z,
                       DeathParticleHandler.ParticleTransform t) {

        Vec3d size = t.max.subtract(t.min);
        float step = 1.0F / t.density;

        int cx = (int) (size.x / step);
        int cy = (int) (size.y / step);
        int cz = (int) (size.z / step);

        Vec3d center = t.min.addVector(
                size.x * 0.5,
                size.y * 0.5,
                size.z * 0.5
        );

        for (int i = 0; i <= cx; i++) {
            for (int j = 0; j <= cy; j++) {
                for (int k = 0; k <= cz; k++) {

                    Vec3d pos = new Vec3d(
                            t.min.x + size.x * i / cx,
                            t.min.y + size.y * j / cy,
                            t.min.z + size.z * k / cz
                    );

                    Vec3d dir = pos.subtract(center);
                    double len = dir.lengthVector();

                    if (len != 0)
                        dir = dir.normalize();

                    double speed =
                            (0.6 + Math.random() * 0.6)
                                    / Math.exp(len * 0.4);

                    Vec3d motion = dir.scale(speed);

                    Vec3d finalPos = pos
                            .addVector(t.offset.x, t.offset.y, t.offset.z)
                            .addVector(x, y, z);

                    Minecraft.getMinecraft().effectRenderer.addEffect(
                            new DeathShardParticle(
                                    world,
                                    finalPos.x,
                                    finalPos.y,
                                    finalPos.z,
                                    motion.x,
                                    motion.y * 0.5,
                                    motion.z
                            )
                    );
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (++particleAge >= life)
            setExpired();
    }

    @Override
    public int getFXLayer() {
        return 3; // no render
    }
}
