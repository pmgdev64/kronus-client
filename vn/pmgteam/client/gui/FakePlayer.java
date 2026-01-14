package vn.pmgteam.client.gui;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.World;

public class FakePlayer extends EntityOtherPlayerMP {
    public FakePlayer(World worldIn, GameProfile profileIn) {
        super(worldIn, profileIn);
    }

    // Ghi đè để tránh check world bị null khi thực hiện một số hành động logic
    @Override
    public boolean isEntityAlive() {
        return true;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}