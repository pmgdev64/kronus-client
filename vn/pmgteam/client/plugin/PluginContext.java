package vn.pmgteam.client.plugin;

import vn.pmgteam.client.module.ModuleManager;
import vn.pmgteam.client.module.Module;

public class PluginContext
{
    private final ModuleManager moduleManager;

    public PluginContext(ModuleManager manager)
    {
        this.moduleManager = manager;
    }

    public void registerModule(Module module)
    {
        moduleManager.register(module);
    }
}
