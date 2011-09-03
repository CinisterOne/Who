package cinister.massiverpg;

import net.craftstars.general.General;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GeneralsHook {
    private final Who plugin;
    private General generalP;
    public boolean enabled;

    public GeneralsHook(Who plugin) {       this.plugin = plugin;   enabled = false;    }
    public void onPluginDisable() {         enabled = false;                            }
    public void onPluginEnable() {          enabled = true;      grabGeneralsPlugin();  }
    public boolean isAway(Player who) {     return generalP.isAway(who);                }

    private void grabGeneralsPlugin() {
        final Plugin grab = plugin.getServer().getPluginManager().getPlugin("General");
        if (grab != null) {
            generalP = (General) grab;
            plugin.log("Now hooking into Generals Plugin.");
        } else {
            plugin.log("Unable to locate {Generals} - not showing afk.");
            generalP = null;
            enabled = false;
        }
    }

}
