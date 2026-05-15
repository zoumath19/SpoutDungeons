package spout.server.paper.dungeons;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class DisenchantmentTableMenu implements Listener {

    private static final Component TITLE = Component.text("Disenchantment Table");
    private static final int INPUT_SLOT = 10;
    private static final int COST_SLOT = 13;
    private static final int OUTPUT_SLOT = 16;

    private final SpoutDungeonsPlugin plugin;

    DisenchantmentTableMenu( SpoutDungeonsPlugin plugin ) {
        this.plugin = plugin;
    }

    void open( Player player ) {
        Inventory inventory = Bukkit.createInventory( null, 27, TITLE );
        this.decorate( inventory );
        player.openInventory( inventory );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick( InventoryClickEvent event ) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inventory = event.getView().getTopInventory();
        if (!this.isTable( event )) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot >= inventory.getSize()) {
            return;
        }

        if (rawSlot == INPUT_SLOT || rawSlot == COST_SLOT) {
            Bukkit.getScheduler().runTask( this.plugin, () -> this.refresh( inventory ) );
            return;
        }

        event.setCancelled( true );
        if (rawSlot == OUTPUT_SLOT) {
            this.takeOutput( player, inventory );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose( InventoryCloseEvent event ) {
        if (!this.isTable( event.getView().getTopInventory(), event.getView().title() )) return;

        Inventory inventory = event.getView().getTopInventory();
        this.returnItem( event.getPlayer() instanceof Player player ? player : null, inventory.getItem( INPUT_SLOT ) );
        this.returnItem( event.getPlayer() instanceof Player player ? player : null, inventory.getItem( COST_SLOT ) );
    }

    private boolean isTable( InventoryClickEvent event ) {
        return this.isTable( event.getView().getTopInventory(), event.getView().title() );
    }

    private boolean isTable( Inventory inventory, Component title ) {
        return inventory.getSize() == 27 && title.equals( TITLE );
    }

    private void decorate( Inventory inventory ) {
        ItemStack filler = this.named( Material.GRAY_STAINED_GLASS_PANE, Component.text( " " ) );
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot != INPUT_SLOT && slot != COST_SLOT && slot != OUTPUT_SLOT) {
                inventory.setItem( slot, filler );
            }
        }
        this.refresh( inventory );
    }

    private void refresh( Inventory inventory ) {
        ItemStack input = inventory.getItem( INPUT_SLOT );
        ItemStack cost = inventory.getItem( COST_SLOT );
        ItemStack output = this.createOutput( input, cost );
        inventory.setItem( OUTPUT_SLOT, output == null ? this.named( Material.BARRIER, Component.text( "Needs enchanted item + lapis" ) ) : output );
    }

    private @Nullable ItemStack createOutput( @Nullable ItemStack input, @Nullable ItemStack cost ) {
        if (input == null || input.getType().isAir() || cost == null || cost.getType() != Material.LAPIS_LAZULI || cost.getAmount() < 1) {
            return null;
        }

        Map<Enchantment, Integer> enchants = input.getEnchantments();
        if (enchants.isEmpty()) {
            return null;
        }

        ItemStack book = new ItemStack( Material.ENCHANTED_BOOK );
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        enchants.forEach( ( enchantment, level ) -> meta.addStoredEnchant( enchantment, level, true ) );
        List<Component> lore = new ArrayList<>();
        lore.add( Component.text( "Extracted from " + this.displayName( input ) ) );
        meta.lore( lore );
        book.setItemMeta( meta );
        return book;
    }

    private void takeOutput( Player player, Inventory inventory ) {
        ItemStack output = this.createOutput( inventory.getItem( INPUT_SLOT ), inventory.getItem( COST_SLOT ) );
        if (output == null) {
            player.sendMessage( "Place an enchanted item and at least one lapis lazuli in the table." );
            return;
        }

        ItemStack input = inventory.getItem( INPUT_SLOT );
        ItemStack cost = inventory.getItem( COST_SLOT );
        if (input == null || cost == null) return;

        ItemStack stripped = input.clone();
        stripped.setAmount( 1 );
        stripped.getEnchantments().keySet().forEach( stripped::removeEnchantment );

        input.setAmount( input.getAmount() - 1 );
        cost.setAmount( cost.getAmount() - 1 );
        inventory.setItem( INPUT_SLOT, input.getAmount() <= 0 ? null : input );
        inventory.setItem( COST_SLOT, cost.getAmount() <= 0 ? null : cost );

        this.give( player, output );
        this.give( player, stripped );
        this.refresh( inventory );
    }

    private void returnItem( @Nullable Player player, @Nullable ItemStack item ) {
        if (player == null || item == null || item.getType().isAir()) return;
        this.give( player, item );
    }

    private void give( Player player, ItemStack item ) {
        player.getInventory().addItem( item ).values().forEach( leftover -> player.getWorld().dropItemNaturally( player.getLocation(), leftover ) );
    }

    private ItemStack named( Material material, Component name ) {
        ItemStack item = new ItemStack( material );
        ItemMeta meta = item.getItemMeta();
        meta.displayName( name );
        meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        item.setItemMeta( meta );
        return item;
    }

    private String displayName( ItemStack item ) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().key().asString();
    }

}
