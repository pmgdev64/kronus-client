package vn.pmgteam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiClickableString extends GuiButton {
    public GuiClickableString(int buttonId, int x, int y, String buttonText) {
        // Chiều cao set là 15 để vùng nhấn (hitbox) thoải mái hơn
        super(buttonId, x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(buttonText), 15, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            // Kiểm tra hover dựa trên hitbox đã tính ở constructor
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            
            // Hiệu ứng màu giống Minecraft Bedrock: Xanh lá khi hover, Trắng/Xám khi không
            int color = this.hovered ? 0xFF55FF55 : 0xFF333333;
            mc.fontRenderer.drawString(this.displayString, this.x, this.y, color);
        }
    }
}