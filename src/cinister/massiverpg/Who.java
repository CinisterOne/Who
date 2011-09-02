package cinister.massiverpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;


public class Who extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	public boolean showall = false;
	public boolean enabled = false;

//	private Server server = getServer();
	public final PermissionsHook permissionsHook = new PermissionsHook(this);
	public final VanishHook vanishHook = new VanishHook(this);
	public final GeneralsHook generalsHook = new GeneralsHook(this);
	public PermissionHandler permissionsHandler = null;
	
	public void log(String message) {	        this.log.info("[Who] " + message);  }
	public boolean IsPlrHid(Player player){		return vanishHook.isHidden(player);	}
	public boolean IsPlrHid(String str){		return vanishHook.isHidden(str);	}
	public boolean checkforVanish = vanishHook.enabled;
	public boolean checkforAFK = generalsHook.enabled;

	
	public String toColorCode(String str){
		return str.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
	}
	public void onEnable(){ 
        this.vanishHook.onPluginEnable();
        this.generalsHook.onPluginEnable();
        this.permissionsHook.onPluginEnable();
        
        if(permissionsHook != null){
        	enabled = true;
        	log.info("Who command Enabled.");	
        } else {
        	log.info("Error, Unable to locate permissions, no need for command.");	
        }
	}

	public void onDisable(){ 
        this.vanishHook.onPluginDisable();
        this.generalsHook.onPluginDisable();
		log.info("Who command Disabled.");
	}

	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("who")){ // If the player typed /who then do the following...
   			boolean showall = true;
   			int playerHidden = 0;
   			Player OnlinePlayers[] = getServer().getOnlinePlayers();
   			int MaxPlayers = getServer().getMaxPlayers();

   			//add in check for Op rebuild command
   			if(args.length > 0 && args[0].equalsIgnoreCase("rebuild")){
   				if( ((Player) sender).isOp()){
   					((Player) sender).sendMessage("Rebuilding Groups Info.");
   					permissionsHook.reload((Player) sender);
   					((Player) sender).sendMessage(ChatColor.GREEN + "/who rebuild - Done.");
   				}
   				
   			}
   			
   			if(checkforVanish){  //found VanishNoPacket, only list "visible" people
   				for (Player p : OnlinePlayers) {
   					if(IsPlrHid(p)){ 
   						playerHidden++;
   					}
   				}
   				if(playerHidden > 0 && !vanishHook.canSeeAll((Player) sender)){ showall = false; }
   			}

   			//Output Players
			Map<String, List<Player>> sort = new HashMap<String, List<Player>>();
			for (Player p : OnlinePlayers) {
				if (IsPlrHid(p) && !showall) {	continue;	}
				String group = permissionsHook.getGroup(p);
				List<Player> list = sort.get(group);
				if (list == null) {
					list = new ArrayList<Player>();
					sort.put(group, list);
				}
				list.add(p);
			}

			String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			String groupColor = "";
			for (String group : groups) {
				StringBuilder groupString = new StringBuilder();
				groupColor = permissionsHook.getGroupColor(group);
				groupString.append(groupColor + group +  ChatColor.WHITE + ": ");
				List<Player> users = sort.get(group);
//				Collections.sort(users);
				boolean first = true;
				for (Player user : users) {
					if (!first) {
						groupString.append(", ");
					} else {
						first = false;
					}
					if (checkforAFK && generalsHook.isAway(user)) {
						groupString.append(ChatColor.GRAY + "[AFK]" + ChatColor.WHITE);
					}
					if (IsPlrHid(user)) {
						groupString.append(ChatColor.GRAY + "[HIDDEN]" + ChatColor.WHITE);
					}
					if( user.isOp()) {
						groupString.append(ChatColor.DARK_RED);
					}
					groupString.append(user.getDisplayName());
					groupString.append(ChatColor.WHITE);
				}
				sender.sendMessage(groupString.toString());
			}


			//Output Status line
			StringBuilder online = new StringBuilder();
			online.append(ChatColor.BLUE + "There are " + ChatColor.RED + (OnlinePlayers.length - playerHidden) );
			if (showall && playerHidden > 0) {	online.append(ChatColor.GRAY + "/" + playerHidden);		}
			online.append(ChatColor.BLUE + " out of a maximum " + ChatColor.RED + MaxPlayers + ChatColor.BLUE + " players online.");
			sender.sendMessage(online.toString());

			permissionsHook.debug_clr();			
			return true;
		} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		return false; 
	}
	
}
