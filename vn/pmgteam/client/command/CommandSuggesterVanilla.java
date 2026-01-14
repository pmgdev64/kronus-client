package vn.pmgteam.client.command;

import net.minecraft.command.ICommand;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandSuggesterVanilla {

    public static List<String> suggest(String input) {
        List<String> result = new ArrayList<>();

        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().getIntegratedServer() == null)
            return result;

        Map<String, ICommand> commands = Minecraft.getMinecraft()
                .getIntegratedServer()
                .getCommandManager()
                .getCommands();

        for (ICommand cmd : commands.values()) { // <- note .values()
            String name = cmd.getName();
            if (!isHardcore(cmd) && name.startsWith(input)) {
                result.add(name);
            }
        }

        return result;
    }

    private static boolean isHardcore(ICommand cmd) {
        String name = cmd.getName();
        // ví dụ loại bỏ các lệnh "hardcore"
        return name.equals("difficulty") || name.equals("stop") || name.equals("seed");
    }
}
