package cinister.massiverpg;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.User;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsHook {

    private final Who plugin;
    private PermissionHandler permHelper;
    public boolean enabled;
//    private String world = "world";
    private String world = "MIAP";

    public PermissionsHook(Who plugin) {		this.plugin = plugin;	enabled = false;		}
	public void onPluginDisable() {				enabled = false;	}
    public void onPluginEnable() {
    	grabPermissionsPlugin();
    	if(enabled){							BuildGroups();    	}
    }

    private void grabPermissionsPlugin() {
    	final Plugin perm = this.plugin.getServer().getPluginManager().getPlugin("Permissions");
    	if (permHelper == null) {
    		if (perm != null) {
    			permHelper = ((Permissions) perm).getHandler();
    	    	enabled = true;
    			plugin.log("Permissions system detected!");
    		} else {
    			permHelper = null;
    			enabled = false;
    			new File("plugins/Who.jar").deleteOnExit();
    			plugin.log("Permissions system not detected!");
    			plugin.log("The plugin will be deleted as soon as the server goes offline!");
    		}
    	} else {
    		plugin.log("The permission handler is already intialized!");
    	}
	}

    public boolean has(Player player, String node){
    	if(!enabled){	//If not enabled, try internal permissions, ifnot use Op status 
    		return player.hasPermission(node) || player.isOp();
    	} // else use permissions handler
    	return permHelper.has(player, node);
    }
    
    public String getGroup(Player player) {
    	if(!enabled){ return ""; }
    	
    	User user = permHelper.getUserObject(player.getWorld().getName(), player.getName());
        LinkedHashSet<Entry> group = user.getParents();
        String str = "";
        for (Entry g : group) {
        	str = g.getName();
        }
    	return str;
    }

    public String[] GroupNames;
    public int gnNum = 0;
    public String[] GroupColors;
    public int gcNum = 0;

    public void BuildGroups(){
    	//Only called if hook is enabled, assume valid permHelper
    	Collection<Group> groups = permHelper.getGroups(this.world); // <--- hack! O_O
plugin.log("Groups size: " + groups.size());

		if(GroupNames == null){
plugin.log("GroupNames == null");
			GroupNames = new String[groups.size()];
plugin.log("GroupNames: " + GroupNames);
		}
		for (Group group : groups) {
plugin.log("BGroup: " + group.getName());
plugin.log("GrounNames: " + GroupNames);
plugin.log("GroupNames.length: " + gnNum);
			GroupNames[gnNum] = group.getName();
plugin.log("BGroup["+ gnNum +"] " + GroupNames[gnNum]);
			gnNum = gnNum + 1;
		}
		if(gnNum > 0){ // We found some grounds
			BuildGroupColors();
		}
    }
    
    public void BuildGroupColors(){
plugin.log("In BuildColors");
    	int l = gnNum;
    	if(gnNum == 0){ return; }
    	String prefix;
    	String tmp[];
    	String codes;
    	
		if(GroupColors == null){
plugin.log("GroupColors == null");
		GroupColors = new String[gnNum];
plugin.log("GroupColors: " + GroupColors);
		}

		for(;gcNum<l;gcNum++){
    		prefix = permHelper.getGroupRawPrefix(this.world, GroupNames[gcNum]);
plugin.log("BuildColors["+gcNum+":"+GroupNames[gcNum]+"].prefix: |"+prefix +"| isNull: "+ (prefix == null) +" isEmpty: "+ (prefix == "") + " Len: " + (prefix.length()));
			if(prefix == null || prefix == "" || prefix.length() == 0){	//if no prefix, stick default "" and continue to next
				GroupColors[gcNum] = "";
				continue;
			}
    		tmp = prefix.split(" ");
plugin.log("tmp["+ tmp.length+"]->tmp[1]: |"+ tmp[1] +"|");
    		if(tmp.length > 0){
    			codes = (tmp[1]).replace("{player}","");
plugin.log("codes: |"+ codes +"|");
    			GroupColors[gcNum] = plugin.toColorCode(codes);
    		}
    	}
    }
    
    public int GetGroupNum(String group){
    	if(!enabled){ return -1; }  // if hook not enabled, return nothing
    	int l = gnNum;
plugin.log("GroupNames.length: " + gnNum);
    	for(int x=0;x<=l;x++){
plugin.log("Groups[" + x + "]: " + GroupNames[x] + " == " + group);
    		if(group == GroupNames[x]){ return x; }
    	}
    	return -1;    	//not found
    }
    
    public String getGroupColor(String group){
plugin.log("in GetGroupColor");
plugin.log("Enabled: " + enabled);
plugin.log("GroupNames: " + gnNum);
plugin.log("GroupColors: " + gcNum);

    	if(!enabled){ return ""; }  // if hook not enabled, return nothing
    	int x = GetGroupNum(group);
plugin.log("GetGroupNum(" + x + ")");
    	if(x != -1){ return GroupColors[x]; } 
    	return "";
    }

}
