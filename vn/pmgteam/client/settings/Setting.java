package vn.pmgteam.client.settings;

import net.minecraft.client.Minecraft;
import vn.pmgteam.client.config.ConfigManager;

public abstract class Setting {
    protected String name;
    protected Minecraft mc = Minecraft.getMinecraft();
    protected ConfigManager configManager = new ConfigManager();

    public Setting(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract void draw(int x, int y, int width, int mouseX, int mouseY);
    
    // Xử lý Click chuột
    public abstract void mouseClicked(int mouseX, int mouseY, int button, int x, int y, int width);

    // Gọi lưu cấu hình tự động [cite: 2026-01-02]
    protected void save() {
        configManager.save();
    }
}