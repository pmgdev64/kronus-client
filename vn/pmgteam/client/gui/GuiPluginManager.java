package vn.pmgteam.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import vn.pmgteam.client.plugin.PluginLoader;
import vn.pmgteam.client.plugin.PluginSystem;

import java.io.IOException;

public class GuiPluginManager extends GuiScreen {
    private final GuiScreen parent;

    public GuiPluginManager(GuiScreen parent) {
        this.parent = parent;
    }

 // Trong class GuiPluginManager
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, "Back"));
        // Nút Reload
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 60, "Reload Plugins"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Plugin Manager", this.width / 2, 20, 0xFF55FF55);

        int startY = 50;
        for (PluginLoader.LoadedPluginData data : PluginLoader.loadedPlugins) {
            // Vẽ khung cho mỗi Plugin
            drawRect(this.width / 2 - 150, startY, this.width / 2 + 150, startY + 35, 0x60000000);
            
            // Vẽ tên và phiên bản
            this.fontRenderer.drawStringWithShadow(data.name + " §7v" + data.version, this.width / 2 - 145, startY + 5, 0xFFFFFFFF);
            
            // Vẽ tác giả
            this.fontRenderer.drawString("Author: §a" + data.author, this.width / 2 - 145, startY + 20, 0xFFCCCCCC);
            
            // Trạng thái (luôn là Loaded vì JAR đã load rồi)
            this.fontRenderer.drawString("§eStatus: Loaded", this.width / 2 + 80, startY + 12, -1);

            startY += 40;
        }

        if (PluginLoader.loadedPlugins.isEmpty()) {
            this.drawCenteredString(this.fontRenderer, "§cNo plugins found in /Plugins folder", this.width / 2, 100, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(parent);
        }
        if (button.id == 0) {
            this.mc.displayGuiScreen(parent);
        } else if (button.id == 1) {
            // Gọi lệnh reload
            PluginSystem.getInstance().reload();
        }
    }
}