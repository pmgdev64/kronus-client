package vn.pmgteam.client.settings;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ModeSetting extends Setting {
    private String activeMode;
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.activeMode = defaultMode;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
    }

    public String getMode() {
        return activeMode;
    }
    
 // --- HÀM THIẾU CỦA BẠN ĐÂY ---
    public void setMode(String mode) {
        if (modes.contains(mode)) {
            this.activeMode = mode;
            this.index = modes.indexOf(mode);
            
            // [2026-01-02] Tự động lưu mỗi khi mode được set (từ Config hoặc Logic)
            save(); 
        }
    }

    // Chuyển sang mode tiếp theo khi click vào GUI
    public void cycle() {
        if (index < modes.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        activeMode = modes.get(index);
        
        // [AutoSave] Lưu ngay khi đổi mode [cite: 2026-01-02]
        save();
    }

    @Override
    public void draw(int x, int y, int width, int mouseX, int mouseY) {
        net.minecraft.client.gui.FontRenderer fr = mc.fontRenderer;
        
        // 1. Vẽ tên Setting ở bên trái (màu xám nhạt)
        fr.drawStringWithShadow(this.name, x, y + 6, 0xFFBBBBBB);
        
        // 2. Tính toán vị trí cho giá trị Mode ở bên phải
        String modeText = "[" + activeMode + "]";
        int textWidth = fr.getStringWidth(modeText);
        
        // Tính toán X sao cho text không bao giờ đè lên tên setting nếu Panel quá hẹp
        int renderX = x + width - textWidth;
        
        // Kiểm tra nếu chuột đang hover vào vùng Mode để đổi màu (phản hồi người dùng)
        boolean hovered = mouseX >= renderX && mouseX <= x + width && mouseY >= y && mouseY < y + 20;

        // 3. Vẽ giá trị Mode (màu xanh lá khi được chọn/hover)
        fr.drawStringWithShadow(modeText, renderX, y + 6, hovered ? 0xFF55FF55 : 0xFF00AA00);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button, int x, int y, int width) {
        // Chỉ xử lý click khi chuột nằm trong phạm vi chiều cao của setting (20px)
        if (mouseY >= y && mouseY < y + 20 && mouseX >= x && mouseX <= x + width) {
            if (button == 0) { // Click chuột trái
                this.cycle(); // Hàm cycle() đã gọi save() bên trong [cite: 2026-01-02]
            }
        }
    }
}