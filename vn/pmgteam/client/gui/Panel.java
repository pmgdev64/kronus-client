package vn.pmgteam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vn.pmgteam.client.category.Category;
import vn.pmgteam.client.module.Module;
import vn.pmgteam.client.module.ModuleManager;
import vn.pmgteam.client.settings.BooleanSetting;
import vn.pmgteam.client.settings.ModeSetting;
import vn.pmgteam.client.settings.NumberSetting;
import vn.pmgteam.client.settings.Setting;

import java.util.List;

public class Panel extends Gui {
    private final Category category;
    private int x, y, width, headerHeight;
    private boolean dragging, expanded = true;
    private int dragX, dragY;

    private float animProgress = 1.0F;
    private final float animSpeed = 0.1F;
    private Module expandedModule = null; 
    
    private Module lastHoveredModule = null;
    private Module currentHoverModule = null;
    private long hoverStartTime = 0;
    private final long TOOLTIP_DELAY = 2000L;

    public Panel(Category category, int x, int y, int width, int headerHeight) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.headerHeight = headerHeight;
    }
    
    public void drawTooltip(int mouseX, int mouseY) {
        if (lastHoveredModule != null && hoverStartTime != 0 && expanded && animProgress > 0.9F) {
            long timeElapsed = System.currentTimeMillis() - hoverStartTime;

            if (timeElapsed >= TOOLTIP_DELAY) {
                FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
                String desc = lastHoveredModule.getDescription(); 
                
                if (desc == null || desc.isEmpty()) desc = "No description provided.";

                List<String> lines = fr.listFormattedStringToWidth(desc, 150);
                int tw = 0;
                for (String s : lines) tw = Math.max(tw, fr.getStringWidth(s));
                int th = lines.size() * 10 + 6;

                Gui.drawRect(mouseX + 12, mouseY - 5, mouseX + 12 + tw + 8, mouseY - 5 + th, 0xEE101010);
                Gui.drawRect(mouseX + 12, mouseY - 5, mouseX + 13, mouseY - 5 + th, 0xFF55FF55);

                int lineY = mouseY - 2;
                for (String line : lines) {
                    fr.drawStringWithShadow(line, mouseX + 16, lineY, 0xFFDDDDDD);
                    lineY += 10;
                }
            }
        }
    }

    public void draw(int mouseX, int mouseY) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        List<Module> modules = ModuleManager.getModulesByCategory(category);

        if (expanded) {
            if (animProgress < 1.0F) animProgress += animSpeed;
        } else {
            if (animProgress > 0.0F) animProgress -= animSpeed;
        }
        animProgress = Math.min(1.0F, Math.max(0.0F, animProgress));
        float ease = 1.0F - (float) Math.pow(1.0F - animProgress, 3);
        
        int totalContentHeight = modules.isEmpty() ? 20 : 0;
        for (Module m : modules) {
            totalContentHeight += 20;
            if (expandedModule == m) {
                totalContentHeight += m.getSettings().size() * 20; // Đồng nhất 20px mỗi setting
            }
        }
        int currentContentHeight = (int) (ease * totalContentHeight);

        // HEADER
        drawRect(x, y, x + width, y + headerHeight, 0xFF121212);
        drawRect(x, y, x + width, y + 1, 0xFF55FF55);
        fr.drawString(category.name(), x + 6, y + (headerHeight / 2) - 4, -1);
        
        int arrowColor = isArrowHovered(mouseX, mouseY) ? 0xFF55FF55 : 0xFFFFFFFF;
        fr.drawString(expanded ? "v" : ">", x + width - 12, y + (headerHeight / 2) - 4, arrowColor);

        // MODULES & SETTINGS
        currentHoverModule = null;
        if (currentContentHeight > 0) {
            drawRect(x, y + headerHeight, x + width, y + headerHeight + currentContentHeight, 0x95000000);
            
            prepareScissor(x, y + headerHeight, width, currentContentHeight);
            int moduleY = y + headerHeight;
            
            for (Module m : modules) {
                boolean isHovered = mouseX >= x && mouseX <= x + width && mouseY >= moduleY && mouseY < moduleY + 20;
                boolean isOverSubArrow = mouseX >= (x + width - 20) && mouseX <= (x + width) && mouseY >= moduleY && mouseY < moduleY + 20;

                if (isHovered) {
                    currentHoverModule = m;
                    drawRect(x + 2, moduleY + 2, x + width - 2, moduleY + 18, 0x30FFFFFF);
                }
                
                if (m.isEnabled()) {
                    drawRect(x + 2, moduleY + 2, x + width - 2, moduleY + 18, 0x6055FF55);
                }

                fr.drawString(m.getName(), x + 10, moduleY + 6, m.isEnabled() ? 0xFF55FF55 : 0xFFBBBBBB);
                fr.drawString(expandedModule == m ? "-" : "+", x + width - 12, moduleY + 6, isOverSubArrow ? 0xFF55FF55 : 0xFF777777);
                
                moduleY += 20;

                if (expandedModule == m) {
                    for (Setting s : m.getSettings()) {
                        // LOGIC KÉO SLIDER: Kiểm tra nếu đang giữ chuột trái trong vùng slider
                        if (s instanceof NumberSetting && Mouse.isButtonDown(0)) {
                            if (mouseX >= x && mouseX <= x + width && mouseY >= moduleY && mouseY < moduleY + 20) {
                                s.mouseClicked(mouseX, mouseY, 0, x + 15, moduleY, width - 25);
                            }
                        }
                        
                        // VẼ SETTING
                     // Tìm đoạn vòng lặp vẽ settings trong Panel.java và thêm vào:
                        if (s instanceof BooleanSetting) {
                            ((BooleanSetting) s).draw(x + 15, moduleY, width - 25, mouseX, mouseY);
                        } else if (s instanceof NumberSetting) {
                            ((NumberSetting) s).draw(x + 15, moduleY, width - 25, mouseX, mouseY);
                        } else if (s instanceof ModeSetting) {
                            // THÊM DÒNG NÀY:
                            ((ModeSetting) s).draw(x + 15, moduleY, width - 25, mouseX, mouseY);
                        }
                        moduleY += 20; 
                    }
                }
            }
            endScissor();
        }

        // TOOLTIP UPDATE
        if (currentHoverModule != null) {
            if (currentHoverModule != lastHoveredModule) {
                hoverStartTime = System.currentTimeMillis();
                lastHoveredModule = currentHoverModule;
            }
        } else {
            lastHoveredModule = null;
            hoverStartTime = 0;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            if (isArrowHovered(mouseX, mouseY)) {
                this.expanded = !this.expanded;
                return;
            }
            if (isHeaderHovered(mouseX, mouseY)) {
                this.dragging = true;
                this.dragX = mouseX - x;
                this.dragY = mouseY - y;
                return;
            }
        }

        if (expanded && animProgress > 0.8F) {
            int moduleY = y + headerHeight;
            for (Module m : ModuleManager.getModulesByCategory(category)) {
                // Click vào Module
                if (mouseX >= x && mouseX <= x + width && mouseY >= moduleY && mouseY < moduleY + 20) {
                    if (mouseX >= (x + width - 20)) {
                        expandedModule = (expandedModule == m) ? null : m;
                    } else {
                        if (button == 0) m.toggle();
                        else if (button == 1) expandedModule = (expandedModule == m) ? null : m;
                    }
                    return;
                }
                moduleY += 20;

                // Click vào Settings
                if (expandedModule == m) {
                    for (Setting s : m.getSettings()) {
                        if (mouseY >= moduleY && mouseY < moduleY + 20) {
                            s.mouseClicked(mouseX, mouseY, button, x + 15, moduleY, width - 25);
                        }
                        moduleY += 20;
                    }
                }
            }
        }
    }

    private void prepareScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int f = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * f, (mc.displayHeight - (y + height) * f), width * f, height * f);
    }

    private void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private boolean isHeaderHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + headerHeight;
    }

    private boolean isArrowHovered(int mouseX, int mouseY) {
        return mouseX >= x + width - 20 && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight;
    }
    
    public void mouseReleased(int mouseX, int mouseY) { this.dragging = false; }
    public void updatePosition(int mouseX, int mouseY) {
        if (this.dragging) { this.x = mouseX - this.dragX; this.y = mouseY - this.dragY; }
    }
}