package vn.pmgteam.client.config;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import vn.pmgteam.client.module.Module;
import vn.pmgteam.client.module.ModuleManager;
import vn.pmgteam.client.settings.*;

import java.io.*;
import java.util.List;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configDir = new File(Minecraft.getMinecraft().mcDataDir, "KronusClient");
    private final File configFile = new File(configDir, "config.json");
    
    public static boolean isLoading = false;

    public ConfigManager() {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void save() {
        // CHỐNG RESET: 
        // 1. Không save khi đang load dữ liệu cũ.
        // 2. TỐI QUAN TRỌNG: Không save nếu ModuleManager chưa có module nào (tránh ghi đè file rỗng).
        List<Module> modules = ModuleManager.getModules();
        if (isLoading || modules == null || modules.isEmpty()) {
            return;
        }

        try {
            JsonObject json = new JsonObject();
            JsonObject modulesJson = new JsonObject();

            for (Module m : modules) {
                JsonObject mData = new JsonObject();
                mData.addProperty("enabled", m.isEnabled());

                // Lưu Settings
                JsonObject sJson = new JsonObject();
                for (Setting s : m.getSettings()) {
                    if (s instanceof BooleanSetting) {
                        sJson.addProperty(s.getName(), ((BooleanSetting) s).isEnabled());
                    } else if (s instanceof NumberSetting) {
                        sJson.addProperty(s.getName(), ((NumberSetting) s).getValue());
                    } else if (s instanceof ModeSetting) {
                        sJson.addProperty(s.getName(), ((ModeSetting) s).getMode());
                    }
                    
                }
                mData.add("settings", sJson);
                modulesJson.add(m.getName(), mData);
            }

            json.add("modules", modulesJson);

            // Ghi file tạm rồi mới đổi tên hoặc ghi đè trực tiếp một cách an toàn
            try (Writer writer = new FileWriter(configFile)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!configFile.exists()) return;
        isLoading = true; 

        try (Reader reader = new FileReader(configFile)) {
            JsonElement root = GSON.fromJson(reader, JsonElement.class);
            if (root == null || !root.isJsonObject()) return;

            JsonObject json = root.getAsJsonObject();
            if (!json.has("modules")) return;
            JsonObject modulesJson = json.getAsJsonObject("modules");

            // Duyệt qua danh sách Module hiện có của Client
            for (Module m : ModuleManager.getModules()) {
                String moduleName = m.getName();
                
                if (modulesJson.has(moduleName)) {
                    JsonObject mData = modulesJson.getAsJsonObject(moduleName);

                    // 1. Load trạng thái Enabled an toàn
                    if (mData.has("enabled")) {
                        boolean shouldBeEnabled = mData.get("enabled").getAsBoolean();
                        
                        // Chỉ can thiệp nếu trạng thái hiện tại khác với cấu hình
                        if (m.isEnabled() != shouldBeEnabled) {
                            // Gọi trực tiếp hàm xử lý bật/tắt của Module
                            // Đảm bảo hàm này kích hoạt onEnable()/onDisable()
                            m.setEnabled(shouldBeEnabled); 
                        }
                    }

                    // 2. Load Settings (Fix lỗi ghi đè nhầm)
                    if (mData.has("settings")) {
                        JsonObject sJson = mData.getAsJsonObject("settings");
                        for (Setting s : m.getSettings()) {
                            if (sJson.has(s.getName())) {
                                JsonElement val = sJson.get(s.getName());
                                if (s instanceof BooleanSetting) {
                                    ((BooleanSetting) s).setEnabled(val.getAsBoolean());
                                } else if (s instanceof NumberSetting) {
                                    ((NumberSetting) s).setValue(val.getAsDouble());
                                } else if (s instanceof ModeSetting) {
                                    ((ModeSetting) s).setMode(val.getAsString());
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("[Kronus] Config loaded and synchronized.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isLoading = false;
        }
    }
}