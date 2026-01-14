package vn.pmgteam.client.plugin;

import net.minecraft.client.Minecraft;
import vn.pmgteam.client.config.ConfigManager;
import vn.pmgteam.client.module.ModuleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginSystem {
    private static PluginSystem instance;
    private final File pluginDir;
    private final PluginContext context;
    
    // Danh sách để quản lý và unload plugin khi cần
    public final List<IClientPlugin> activePlugins = new ArrayList<>();

    public PluginSystem() {
        instance = this;
        // Thư mục lưu plugin: .minecraft/ten_client/Plugins
        this.pluginDir = new File(Minecraft.getMinecraft().mcDataDir, "KronusClient/Plugins");
        this.context = new PluginContext(new ModuleManager());
    }

    public static PluginSystem getInstance() {
        return instance;
    }

    /**
     * Khởi tạo hệ thống Plugin (Gọi trong lúc Client khởi động)
     */
    public void init() {
        System.out.println("[PMG-System] Initializing Plugin System...");
        reload();
    }

    /**
     * Tính năng Hot Reload: Quét và load lại toàn bộ plugin
     */
 // Trong PluginSystem.java
    private final ConfigManager configManager = new ConfigManager();

    public void reload() {
        // 1. Lưu trạng thái trước khi xóa
        configManager.save();

        // 2. Dọn dẹp Plugin cũ
        for (IClientPlugin p : activePlugins) {
            p.onUnload();
        }
        activePlugins.clear();
        
        // 3. Quan trọng: Phải giữ lại Module mặc định và chỉ xóa Module từ Plugin 
        // Hoặc xóa hết và đăng ký lại tất cả. Ở đây ta giả định register lại toàn bộ:
        ModuleManager.getModules().clear();
        
        // Gọi hàm đăng ký các Module gốc của Client (như ClickGUI, HUD, v.v.)
        // ví dụ: PMGClient.instance.registerInternalModules();

        // 4. Load lại JAR
        PluginLoader.loadedPlugins.clear();
        PluginLoader.loadPlugins(pluginDir, context);
        PluginLoader.loadedPlugins.forEach(data -> activePlugins.add(data.plugin));

        // 5. Áp dụng lại trạng thái từ config.json
        configManager.load();
        
        System.out.println("[PMG-System] Hot-Reload complete!");
    }
    public File getPluginDir() {
        return pluginDir;
    }
}