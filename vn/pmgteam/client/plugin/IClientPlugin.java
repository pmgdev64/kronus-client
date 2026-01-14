package vn.pmgteam.client.plugin;

public interface IClientPlugin
{
    void onLoad(PluginContext context);
    void onUnload();
}
