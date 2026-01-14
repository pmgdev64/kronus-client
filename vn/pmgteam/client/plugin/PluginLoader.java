package vn.pmgteam.client.plugin;

import vn.pmgteam.client.plugin.annotation.ClientPlugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PluginLoader {
    // Lưu danh sách các instance plugin đã load để GUI và PluginSystem truy cập
    public static final List<LoadedPluginData> loadedPlugins = new ArrayList<>();

    // Class phụ trợ để lưu thông tin hiển thị
    public static class LoadedPluginData {
        public String name, author, version;
        public IClientPlugin plugin;

        public LoadedPluginData(String name, String author, String version, IClientPlugin plugin) {
            this.name = name;
            this.author = author;
            this.version = version;
            this.plugin = plugin;
        }
    }

    public static void loadPlugins(File dir, PluginContext context) {
        if (!dir.exists()) dir.mkdirs();

        File[] files = dir.listFiles(f -> f.getName().endsWith(".jar"));
        if (files == null) return;

        for (File jarFile : files) {
            try (JarFile jar = new JarFile(jarFile)) {
                URLClassLoader cl = new URLClassLoader(
                        new URL[]{jarFile.toURI().toURL()},
                        PluginLoader.class.getClassLoader()
                );

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    
                    if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                        continue;

                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    try {
                        Class<?> clazz = cl.loadClass(className);

                        if (clazz.isAnnotationPresent(ClientPlugin.class)) {
                            if (IClientPlugin.class.isAssignableFrom(clazz)) {
                                ClientPlugin info = clazz.getAnnotation(ClientPlugin.class);
                                
                                // Khởi tạo instance của Plugin
                                IClientPlugin plugin = (IClientPlugin) clazz.getDeclaredConstructor().newInstance();

                                System.out.println("[PMG-Plugin] Loading: " + info.name() + " [" + info.version() + "]");
                                
                                // 1. Gọi onLoad để Plugin thực hiện đăng ký Module
                                plugin.onLoad(context);

                                // 2. FIX: Thêm vào danh sách để PluginSystem.size() không bằng 0
                                loadedPlugins.add(new LoadedPluginData(
                                    info.name(), 
                                    info.author(), 
                                    info.version(), 
                                    plugin
                                ));
                            }
                        }
                    } catch (Exception e) {
                        // Tránh lỗi một class làm hỏng toàn bộ quá trình quét JAR
                    }
                }
            } catch (Throwable t) {
                System.err.println("[Plugin] Error loading jar: " + jarFile.getName());
                t.printStackTrace();
            }
        }
    }
}