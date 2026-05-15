package spout.server.paper.dungeons;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;
import spout.server.paper.api.SpoutEvents;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("unused")
public final class SpoutDungeonsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap( @NotNull BootstrapContext context ) {
        if (!CheckSpout.checkSpout()) return;

        this.registerIncludedDataPack( context );
        this.registerIncludedResourcePack( context );
    }

    private void registerIncludedDataPack( @NotNull BootstrapContext context ) {
        context.getLifecycleManager().registerEventHandler(
            LifecycleEvents.DATAPACK_DISCOVERY,
            event -> {
                try {
                    event.registrar().discoverPack( Objects.requireNonNull( this.getClass().getResource( "/data_pack" ) ).toURI(), "provided" );
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException( e );
                }
            }
        );
    }

    private void registerIncludedResourcePack( @NotNull BootstrapContext context ) {
        context.getLifecycleManager().registerEventHandler(
            SpoutEvents.PLUGIN_RESOURCE_PACK_DISCOVERY,
            event -> event.register( this, context )
        );
    }

}

