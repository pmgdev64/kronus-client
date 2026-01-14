package vn.pmgteam.client.gui;

import net.minecraft.client.gui.GuiScreen;
import vn.pmgteam.client.category.Category;

import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends GuiScreen
{
    private final List<Panel> panels = new ArrayList<>();

    @Override
    public void initGui()
    {
        panels.clear();

        int x = 40;
        for (Category c : Category.values())
        {
            panels.add(new Panel(c, x, 40, 120, 18));
            x += 130;
        }

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Panel p : panels) {
            p.updatePosition(mouseX, mouseY);
            p.draw(mouseX, mouseY);
        }
        
        // PHẢI GỌI SAU CÙNG: Để Tooltip đè lên mọi Panel
        for (Panel p : panels) {
            p.drawTooltip(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (Panel p : panels)
            p.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (Panel p : panels)
            p.mouseReleased(mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
}
