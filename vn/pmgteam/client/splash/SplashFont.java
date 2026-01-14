package vn.pmgteam.client.splash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class SplashFont {

    private final FontRenderer fontRenderer;

    public SplashFont(Minecraft mc) {
        // Unicode true để render tất cả ký tự
        fontRenderer = new FontRenderer(new GameSettings(), new ResourceLocation("textures/font/ascii.png"),
                mc.getTextureManager(), true);
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
