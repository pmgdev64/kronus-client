package vn.pmgteam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiNekoButton extends GuiButton {
    private ResourceLocation icon;
    private int iconWidth, iconHeight;

    // Constructor cho nút có Text thông thường
    public GuiNekoButton(int id, int x, int y, int width, int height, String text) {
        super(id, x, y, width, height, text);
    }

    // Constructor cho nút có Texture (Icon)
    public GuiNekoButton(int id, int x, int y, int width, int height, String text, ResourceLocation icon, int iconW, int iconH) {
        super(id, x, y, width, height, text);
        this.icon = icon;
        this.iconWidth = iconW;
        this.iconHeight = iconH;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

        // Cập nhật progress cho hiệu ứng mượt
        if (this.hovered && this.enabled) {
            if (this.hoverProgress < 1.0F) this.hoverProgress += 0.1F;
        } else {
            if (this.hoverProgress > 0.0F) this.hoverProgress -= 0.1F;
        }
        
        float ease = 1.0F - (float)Math.pow(1.0F - this.hoverProgress, 3); // Ease Out Cubic
        float scale = 1.0F + (ease * 0.05F); // Zoom nhẹ 5%

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + width / 2F, y + height / 2F, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-(x + width / 2F), -(y + height / 2F), 0);

        // Nền nút (Modern Dark/White tùy trạng thái)
        int bg = blendColors(0xFF202020, 0xFF353535, ease);
        drawRect(x, y, x + width, y + height, bg);

        // Nếu có icon, vẽ icon
        if (this.icon != null) {
            mc.getTextureManager().bindTexture(icon);
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            // Vẽ icon ở chính giữa nút
            Gui.drawModalRectWithCustomSizedTexture(x + (width - iconWidth) / 2, y + (height - iconHeight) / 2, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
        }

        // Vẽ Text nếu có
        if (this.displayString != null && !this.displayString.isEmpty()) {
            int color = blendColors(0xFFCCCCCC, 0xFFFFFFFF, ease);
            this.drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, color);
        }

        // Vạch xanh lá đặc trưng NekoUI khi hover
        if (ease > 0) {
            int barW = (int)(width * ease);
            drawRect(x + (width/2 - barW/2), y + height - 1, x + (width/2 + barW/2), y + height, 0xFF55FF55);
        }

        GlStateManager.popMatrix();
    }
}