package vn.pmgteam.client.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatLightLaf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class GuiSkin extends GuiScreen {
    private final GuiScreen lastScreen;
    private EntityPlayerSP previewPlayer;
    private World previewWorld;

    public GuiSkin(GuiScreen parent) {
        this.lastScreen = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = width / 2;

        // Nút Back
        this.buttonList.add(new GuiNekoButton(200, 15, 12, 18, 18, "", new ResourceLocation("kronus/icons/back.png"), 12, 12));
        
        // Nút chức năng
        this.buttonList.add(new GuiNekoButton(10, centerX - 100, height / 2 - 40, 200, 20, "Reset Skin (Mặc định)"));
        this.buttonList.add(new GuiNekoButton(12, centerX - 100, height / 2 - 10, 200, 20, "Chọn Skin từ Máy tính..."));

        setupPreviewPlayer();
    }

    private void openFileChooser() {
        try {
            // Áp dụng FlatLaf cho JFileChooser trông hiện đại [2026-01-13]
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Kronus Plugin: Chọn Skin mới");
            chooser.setFileFilter(new FileNameExtensionFilter("Minecraft Skin (.png)", "png"));

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                
                // Quay lại Main Thread của Minecraft để xử lý file an toàn
                mc.addScheduledTask(() -> {
                    try {
                        String username = mc.getSession().getUsername();
                        File skinFolder = new File(mc.mcDataDir, "KronusClient/skins/" + username);
                        if (!skinFolder.exists()) skinFolder.mkdirs();

                        File destFile = new File(skinFolder, "skin.png");
                        Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        // AutoSave: Xóa cache cũ để Minecraft nạp lại file mới ngay lập tức
                        ResourceLocation loc = new ResourceLocation("kronus/skins/" + username.toLowerCase());
                        mc.getTextureManager().deleteTexture(loc);

                        // Nạp lại Preview Player để cập nhật hiển thị
                        setupPreviewPlayer();
                        System.out.println("[Kronus] Đã cập nhật skin thành công.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) mc.displayGuiScreen(lastScreen);
        if (button.id == 12) openFileChooser();
        if (button.id == 10) {
            String username = mc.getSession().getUsername();
            File skinFile = new File(mc.mcDataDir, "KronusClient/skins/" + username + "/skin.png");
            if (skinFile.exists()) skinFile.delete();
            mc.getTextureManager().deleteTexture(new ResourceLocation("kronus/skins/" + username.toLowerCase()));
            setupPreviewPlayer();
        }
    }

    private void setupPreviewPlayer() {
        if (mc.world != null && mc.player != null) {
            this.previewPlayer = mc.player;
        } else {
            try {
                // Khởi tạo World giả lập với BlockState AIR để tránh NPE Light
                WorldInfo worldInfo = new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "PreviewWorld");
                this.previewWorld = new World(new SaveHandlerMP(), worldInfo, new net.minecraft.world.WorldProviderSurface(), mc.mcProfiler, true) {
                    @Override protected net.minecraft.world.chunk.IChunkProvider createChunkProvider() { return null; }
                    @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
                    @Override public net.minecraft.block.state.IBlockState getBlockState(net.minecraft.util.math.BlockPos pos) { return net.minecraft.init.Blocks.AIR.getDefaultState(); }
                };

                // Ghost Player SP: Chặn đứng mọi truy cập vào connection đang null
                this.previewPlayer = new EntityPlayerSP(mc, this.previewWorld, null, new StatisticsManager(), new RecipeBook()) {
                    @Override public boolean hasPlayerInfo() { return false; }
                    @Override @Nullable public net.minecraft.client.network.NetworkPlayerInfo getPlayerInfo() { return null; }
                    @Override public net.minecraft.scoreboard.Team getTeam() { return null; }
                    
                    // Fix lỗi ánh sáng
                    @Override public float getBrightness() { return 1.0F; }
                    @Override public int getBrightnessForRender() { return 15728880; }

                    @Override
                    public ResourceLocation getLocationSkin() {
                        String username = mc.getSession().getUsername();
                        ResourceLocation loc = new ResourceLocation("kronus/skins/" + username.toLowerCase());
                        
                        if (mc.getTextureManager().getTexture(loc) != null) return loc;

                        File skinFile = new File(mc.mcDataDir, "KronusClient/skins/" + username + "/skin.png");
                        if (skinFile.exists()) {
                            ThreadDownloadImageData imageData = new ThreadDownloadImageData(skinFile, null, null, new ImageBufferDownload());
                            mc.getTextureManager().loadTexture(loc, imageData);
                            return loc;
                        }
                        return DefaultPlayerSkin.getDefaultSkin(this.getUniqueID());
                    }
                };
                this.previewPlayer.setEntityId(-1);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawRect(0, 0, width, height, 0x60000000);
        
        if (this.previewPlayer != null) {
            drawEntityOnScreen(width - 100, height - 50, 80, (float)(width - 100) - mouseX, (float)(height - 180) - mouseY, this.previewPlayer);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        
        // Fix nhân vật bị đen
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.enableStandardItemLighting();
        ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;

        // Vô hiệu hóa Lightmap của World giả
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        try {
            Minecraft.getMinecraft().getRenderManager().doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        } catch (Exception e) {
            // Chặn crash render cuối cùng
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}