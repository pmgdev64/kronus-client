package vn.pmgteam.client.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.pmgteam.client.category.Category;
import vn.pmgteam.client.config.ConfigManager;
import vn.pmgteam.client.settings.Setting;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private ConfigManager configManager = new ConfigManager();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    // --- ĐÂY LÀ PHẦN THIẾU ---
    // Phải có hàm này thì class con mới @Override được
    public void onUpdate() {} 

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            
            if (enabled) onEnable();
            else onDisable();
            
            // Tự động lưu mỗi khi Toggle
            if (configManager != null) {
                configManager.save();
            }
        }
    }
    
 // Thêm danh sách settings
    private final List<Setting> settings = new ArrayList<>();

    // Thêm hàm để đăng ký settings (dùng trong constructor của CpsBooster)
    protected void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    // Phương thức getter mà Panel đang gọi
    public List<Setting> getSettings() {
        return settings;
    }
    
    // Thêm hàm này vào để các module khác có thể sử dụng
    public void onRender(float partialTicks) {
        // Mặc định để trống, các module Visual sẽ override lại
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public Category getCategory() { return category; }
}