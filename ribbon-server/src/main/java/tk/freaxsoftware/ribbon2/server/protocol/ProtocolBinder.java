package tk.freaxsoftware.ribbon2.server.protocol;

import java.util.List;

import tk.freaxsoftware.ribbon2.server.protocol.impl.connection.RibbonNctlInit;

/**
 * List of all Ribbon protocol commands to use.
 * @author Stanislav Nepochatov
 */
public class ProtocolBinder {
    public static List<RibbonCommand> commands = List.of(
        new RibbonNctlInit()
    );
}
