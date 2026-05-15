package spout.server.paper.dungeons;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

final class DisenchantmentTableListener implements Listener {

    private final SpoutDungeonsPlugin plugin;
    private final @Nullable Material tableMaterial;
    private final DisenchantmentTableMenu menu;

    DisenchantmentTableListener( SpoutDungeonsPlugin plugin, @Nullable Material tableMaterial, DisenchantmentTableMenu menu ) {
        this.plugin = plugin;
        this.tableMaterial = tableMaterial;
        this.menu = menu;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract( PlayerInteractEvent event ) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != this.tableMaterial) return;

        event.setCancelled( true );
        this.menu.open( event.getPlayer() );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin( PlayerJoinEvent event ) {
        this.giveTable( event.getPlayer() );
    }

    void giveTable( Player player ) {
        ItemType itemType = Registry.ITEM.get( SpoutDungeonsPlugin.DISENCHANTMENT_TABLE_KEY );
        if (itemType != null && Arrays.stream( player.getInventory().getContents() )
            .noneMatch( stack -> stack != null && stack.getType().asItemType() == itemType )) {
            player.give( itemType.createItemStack( 1 ) );
        }
    }

}
