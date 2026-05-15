package spout.server.paper.dungeons;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.List;

final class DungeonsCommand implements BasicCommand {

    private final DisenchantmentTableListener listener;

    DungeonsCommand( DisenchantmentTableListener listener ) {
        this.listener = listener;
    }

    @Override
    public void execute( CommandSourceStack source, String[] args ) {
        this.handle( source.getSender(), "dungeons", args );
    }

    @Override
    public Collection<String> suggest( CommandSourceStack source, String[] args ) {
        if (args.length == 1) return List.of( "table", "light" ).stream()
            .filter( option -> option.startsWith( args[0].toLowerCase() ) )
            .toList();
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "spout-dungeons.command";
    }

    private void handle( CommandSender sender, String label, String[] args ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage( "Only players can use this command." );
            return;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase( "table" )) {
            this.listener.giveTable( player );
            sender.sendMessage( "You received a Disenchantment Table." );
            return;
        }
        if (args[0].equalsIgnoreCase( "light" )) {
            Block target = player.getTargetBlockExact( 8 );
            if (target == null) {
                sender.sendMessage( "Look at a block within 8 blocks." );
                return;
            }
            sender.sendMessage( "Block: " + target.getType().key() );
            sender.sendMessage( "Emission: " + target.getBlockData().getLightEmission() );
            sender.sendMessage( "Light here: " + target.getLightLevel() + " (block " + target.getLightFromBlocks() + ", sky " + target.getLightFromSky() + ")" );
            sender.sendMessage( "Above: " + target.getRelative( BlockFace.UP ).getLightLevel() + " (block " + target.getRelative( BlockFace.UP ).getLightFromBlocks() + ")" );
            return;
        }
        sender.sendMessage( "Usage: /" + label + " table" );
    }

}
