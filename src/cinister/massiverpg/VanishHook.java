package cinister.massiverpg;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import to.joe.vanish.VanishManager;
import to.joe.vanish.VanishPlugin;

public class VanishHook {

    private final Who plugin;
    private VanishManager vanishNP;
    public boolean enabled;

    public VanishHook(Who plugin) {		this.plugin = plugin;	enabled = false;		}
	public void onPluginDisable() {		enabled = false;	plugin.showall = true;		}
    public void onPluginEnable() {		enabled = true;	grabVanishPlugin();		}
	public boolean isHidden(Player player) {	return vanishNP.isVanished(player);	}
	public boolean isHidden(String str) {		return vanishNP.isVanished(str);		}
	public boolean canSeeAll(Player player) {	return checkPerm(player, "vanish.see" );    }
	private boolean checkPerm(Player player, String node){	return plugin.permissionsHook.has(player, node);	}
	
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