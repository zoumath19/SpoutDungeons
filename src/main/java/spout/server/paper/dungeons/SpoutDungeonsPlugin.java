package spout.server.paper.dungeons;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class SpoutDungeonsPlugin extends JavaPlugin {

    static final Key DISENCHANTMENT_TABLE_KEY = Key.key( "spout_dungeons:disenchantment_table" );

    @Override
    public void onEnable() {
        if (!CheckSpout.checkSpout()) return;

        Material tableMaterial = Material.matchMaterial( DISENCHANTMENT_TABLE_KEY.asString(), false );
        if (tableMaterial == null) {
            this.getLogger().warning( "Could not resolve spout_dungeons:disenchantment_table as a Bukkit Material." );
        }

        DisenchantmentTableMenu menu = new DisenchantmentTableMenu( this );
        DisenchantmentTableListener listener = new DisenchantmentTableListener( this, tableMaterial, menu );
        this.getServer().getPluginManager().registerEvents( listener, this );
        this.getServer().getPluginManager().registerEvents( menu, this );
        this.registerCommand( "dungeons", "Spout Dungeons test commands", new DungeonsCommand( listener ) );
    }

}

