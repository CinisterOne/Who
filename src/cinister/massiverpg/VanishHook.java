package cinister.massiverpg;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import to.joe.vanish.VanishManager;
import to.joe.vanish.VanishPlugin;

public class VanishHook {

    private final Who plugin;
    private VanishManager vanishNP;
    public boolean enabled;

    public VanishHook(Who plugin) {     this.plugin = plugin;   enabled = false;        }
    public void onPluginDisable() {     enabled = false;        plugin.showall = true;  }
    public void onPluginEnable() {      enabled = true;         grabVanishPlugin();     }
    public boolean isHidden(Player player) {    return vanishNP.isVanished(player);     }
    public boolean isHidden(String str) {       return vanishNP.isVanished(str);        }

    public boolean canSeeAll(Player player) {  // for some reason this borks when getting shunted to the permisisonsHook
        if(plugin.permissionsHandler != null){ // trying it this way instead /shrug
            return plugin.permissionsHandler.has(player, "vanish.see" );	
        } else {
            return player.hasPermission("vanish.see") || player.isOp();
        }
    }
//	private boolean checkPerm(Player player, String node){	return plugin.permissionsHook.has(player, node);	}
	
    private void grabVanishPlugin() {
        final Plugin grab = plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
        if (grab != null) {
            vanishNP = ((VanishPlugin) grab).getManager();
            plugin.log("Now hooking into VanishNoPacket.");
        }
        else{
            vanishNP = null;
            enabled = false;
            plugin.log("Unable to locate {VanishNoPacket} - defaulting to 'ShowAll'.");
        }
    }

}