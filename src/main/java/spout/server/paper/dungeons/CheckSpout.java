package spout.server.paper.dungeons;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public final class CheckSpout {

    private CheckSpout() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkSpout() {
        try {
            Class.forName( "spout.server.paper.api.SpoutMarker" );
            return true;
        } catch (ClassNotFoundException ignored) {
            ComponentLogger.logger().warn( "Spout Dungeons requires Spout: https://github.com/ModernSpout/Spout" );
            return false;
        }
    }

}

