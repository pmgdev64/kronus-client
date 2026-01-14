package vn.pmgteam.client.module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import vn.pmgteam.client.category.Category;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void register(Module module) {
        // Kiểm tra tránh đăng ký trùng lặp module cùng tên khi Reload Plugin
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(module.getName())) {
                return; 
            }
        }
        modules.add(module);
    }

    // Trả về toàn bộ danh sách để ConfigManager sử dụng
    public static List<Module> getModules() {
        return modules;
    }

    public static List<Module> getModulesByCategory(Category c) {
        return modules.stream()
                .filter(m -> m.getCategory() == c)
                .collect(Collectors.toList());
    }
    
    // Xóa toàn bộ module (Dùng khi Hot-Reload hệ thống plugin)
    public static void clear() {
        modules.clear();
    }
}