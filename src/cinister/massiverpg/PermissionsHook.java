package cinister.massiverpg;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.User;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsHook {

    private final Who plugin;
    private PermissionHandler permHelper = null;
    public boolean enabled;
    private String world = "world";
//    private String world = "MIAP";

    public String[] GroupNames;
    public int gnNum = 0;
    public String[] GroupColors;
    public int gcNum = 0;
    private Player dplr = null;

    private void d(String msg){
    	if(dplr == null){ 
    		plugin.log(msg);
    	} else {
    		dplr.sendMessage(msg);
    	}
    	return;
    }

//    private String world = "MIAP";

    public PermissionsHook(Who plugin) {        this.plugin = plugin;	enabled = false;   }
	public void onPluginDisable() {             enabled = false;    }
    public void onPluginEnable() {
    	initVars();
    	grabPermissionsPlugin();
    	if(enabled){                            BuildGroups();      }
    }

    public void debug_clr(){
    	dplr = null;
    }
    
    public void reload(Player plr){
    	dplr = plr;
		d(ChatColor.BLUE + "InitVars...");
    	this.initVars();
		d(ChatColor.GREEN + "Done.");
		d(ChatColor.BLUE + "ReBuildGroups...");
    	this.BuildGroups();
		d(ChatColor.GREEN + "Done.");
    }
    
    private void initVars() {
		// TODO Auto-generated method stub
        this.GroupNames = null;	        this.gnNum = 0;
        this.GroupColors = null;        this.gcNum = 0;
	}
    
	private void grabPermissionsPlugin() {
    	final Plugin perm = this.plugin.getServer().getPluginManager().getPlugin("Permissions");
    	if (permHelper == null) {
    		if (perm != null) {
    			permHelper = ((Permissions) perm).getHandler();
    			plugin.permissionsHandler = permHelper;
    			
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
	    	enabled = true;
    	}
	}

/*  public boolean has(Player player, String node){
     	if(permHelper != null){	// can we use permissions handler?
    		return permHelper.has(player, node);	
    	}	//No? then try internal permissions, ifnot use Op status
    		return player.hasPermission(node) || player.isOp();
    }
*/
    
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

    private void BuildGroups(){
    	//Only called if hook is enabled, assume valid permHelper

    	Collection<Group> groups = permHelper.getGroups(this.world); // <--- hack! O_O
d("Groups size: " + groups.size());

		if(GroupNames == null){
d("GroupNames == null");
			GroupNames = new String[groups.size()];
d("GroupNames: " + GroupNames.toString());
		}
		for (Group group : groups) {
d("BGroup: " + group.getName());
d("GrounNames: " + GroupNames.toString());
d("GroupNames.length: " + gnNum);
			GroupNames[gnNum] = group.getName();
d("BGroup["+ gnNum +"] " + GroupNames[gnNum]);
			gnNum = gnNum + 1;
		}
		if(gnNum > 0){ // We found some grounds
			BuildGroupColors();
		}
    }
    
    public void BuildGroupColors(){
d("In BuildColors");
    	int l = gnNum;
    	if(gnNum == 0){ return; }
    	String prefix;
    	String tmp[];
    	String codes;
    	
		if(GroupColors == null){
d("GroupColors == null");
		GroupColors = new String[gnNum];
d("GroupColors: " + GroupColors);
		}

		for(;gcNum<l;gcNum++){
    		prefix = permHelper.getGroupRawPrefix(this.world, GroupNames[gcNum]);
d("BuildColors["+gcNum+":"+GroupNames[gcNum]+"].prefix: |"+plugin.toColorCode(prefix) +"| isNull: "+ (prefix == null) +" isEmpty: "+ (prefix == "") + " Len: " + (prefix.length()));
			if(prefix == null || prefix == "" || prefix.length() == 0){	//if no prefix, stick default "" and continue to next
				GroupColors[gcNum] = "";
				continue;
			}
			if(prefix.indexOf(" ") == -1){
d("' ' not found in tmp.");
				GroupColors[gcNum] = "";
				continue;
			}
    		tmp = prefix.split(" ");
d("tmp["+ tmp.length+"]->tmp[1]: |"+ tmp[1] +"|");
    		if(tmp.length > 0){
    			codes = (tmp[1]).replace("{player}","");
d("codes: |"+ codes +"|");
    			GroupColors[gcNum] = plugin.toColorCode(codes);
    		}
    	}
    }
    
    public int GetGroupNum(String group){
    	if(!enabled){ return -1; }  // if hook not enabled, return nothing
    	int l = gnNum;
d("GroupNames.length: " + gnNum);
    	for(int x=0;x<l;x++){
d("Groups[" + x + "]: |" + GroupNames[x] + "| == |" + group +"|" + (group == GroupNames[x]));
    		if(group == GroupNames[x]){ return x; }
    	}
    	return -1;    	//not found
    }
    
    public String getGroupColor(String group){
d("in GetGroupColor");
d("Enabled: " + enabled);
d("GroupNames: " + gnNum);
d("GroupColors: " + gcNum);

    	if(!enabled){ return ""; }  // if hook not enabled, return nothing
    	int x = GetGroupNum(group);
if(x == -1){d("GetGroupNum(" + x + ")");}
    	if(x != -1){ 
d("GetGroupNum(" + x + ") -> " + GroupColors[x] + group);
    		return GroupColors[x];
    	} 
    	return "";
    }

}
