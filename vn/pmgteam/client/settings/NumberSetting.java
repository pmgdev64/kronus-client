package vn.pmgteam.client.settings;

import net.minecraft.client.gui.Gui;

public class NumberSetting extends Setting {
    private double value, min, max, increment;

    public NumberSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public double getValue() { return value; }
    
    public void setValue(double newValue) {
        double precision = 1.0 / increment;
        this.value = Math.round(Math.max(min, Math.min(max, newValue)) * precision) / precision;
        save(); // [cite: 2026-01-02] Tự động lưu khi đổi giá trị
    }

    @Override
    public void draw(int x, int y, int width, int mouseX, int mouseY) {
        double renderWidth = (width) * (value - min) / (max - min);
        mc.fontRenderer.drawStringWithShadow(name + ": " + value, x, y, 0xFFDDDDDD);
        Gui.drawRect(x, y + 12, x + width, y + 14, 0xFF202020);
        Gui.drawRect(x, y + 12, x + (int)renderWidth, y + 14, 0xFF55FF55);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button, int x, int y, int width) {
        // Tính toán lại giá trị dựa trên vị trí chuột
        double diff = Math.min(width, Math.max(0, mouseX - x));
        setValue(min + (diff / width) * (max - min));
    }
}