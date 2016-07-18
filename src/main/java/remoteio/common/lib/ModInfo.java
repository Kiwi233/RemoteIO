package remoteio.common.lib;

public class ModInfo {
    public static final String ID = "RIO";
    public static final String NAME = "RemoteIO";
    public static final String MAJOR = "@MAJOR@";
    public static final String MINOR = "@MINOR@";
    public static final String PATCH = "@PATCH@";
    public static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;
    public static final String DEPENDENCIES = "required-after:Forge@[12.18.1.2011,);after:Waila";

    public static final String CLIENT = "remoteio.client.ClientProxy";
    public static final String SERVER = "remoteio.common.CommonProxy";

    public static final Object RESOURCE_PREFIX = "remoteio:";
}
