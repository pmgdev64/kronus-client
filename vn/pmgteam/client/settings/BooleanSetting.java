package vn.pmgteam.client.settings;

import net.minecraft.client.gui.Gui;
import vn.pmgteam.client.config.ConfigManager;

public class BooleanSetting extends Setting {
    private boolean enabled;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.enabled = defaultValue;
    }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        this.enabled = !this.enabled;
        save();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        // Không gọi save() ở đây nếu đang trong quá trình Load để tránh vòng lặp vô tận
        if (!ConfigManager.isLoading) {
            save();
        }
    }

    @Override
    public void draw(int x, int y, int width, int mouseX, int mouseY) {
        // Vẽ nền checkbox
        Gui.drawRect(x, y, x + 10, y + 10, 0xFF202020);
        if (enabled) {
            Gui.drawRect(x + 2, y + 2, x + 8, y + 8, 0xFF55FF55);
        }
        mc.fontRenderer.drawStringWithShadow(name, x + 15, y + 1, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button, int x, int y, int width) {
        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + 12) {
            this.toggle();
        }
    }
}