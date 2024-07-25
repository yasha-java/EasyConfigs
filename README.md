# How to use EasyConfigs
Import EasyConfigsAPI:
```xml
<dependencies>
    <dependency>
        <groupId>org.codec58</groupId>
        <artifactId>EasyConfigsAPI</artifactId>
        <version>0.2.45_BETA</version>
        <scope>provided</scope> <!-- IMPORTANT -->
        <!-- if you don't have this library installed in your local maven -->
        <!-- <systemPath>${basedir}/libs/EasyConfigsAPI.jar</systemPath> -->
    </dependency>
</dependencies>
```

After that we can make our config class:
```java
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;

@Config(name = "OurConfig") //without whitespace
public class OurConfig {
    @ConfigVariable(name = "chat.permission") //without whitespace
    public static String PERM_CHAT_SEND = "org.codec58.configtest.send"; //default value
    @ConfigVariable(name = "chat.allow_setting")
    public static TestEnumeration_AllowedPlayers ALLOW_SETTING = TestEnumeration_AllowedPlayers.OP;


    @ConfigVariable(name = "value.native_boolean")
    public static boolean NATIVE_BOOLEAN = true;
    @ConfigVariable(name = "value.boolean")
    public static Boolean BOOLEAN = true;

    @ConfigVariable(name = "value.native_integer")
    public static int NATIVE_INTEGER = 1;
    @ConfigVariable(name = "value.integer")
    public static Integer INTEGER = 1;

    @ConfigVariable(name = "value.native_float")
    public static float NATIVE_FLOAT = 1f;
    @ConfigVariable(name = "value.float")
    public static Float FLOAT = 1f;

    @ConfigVariable(name = "value.native_double")
    public static double NATIVE_DOUBLE = 1d;
    @ConfigVariable(name = "value.double")
    public static Double DOUBLE = 1d;

    @ConfigVariable(name = "value.native_short")
    public static short NATIVE_SHORT = 1;
    @ConfigVariable(name = "value.short")
    public static short SHORT = 1;
}
```

TestEnumeration_AllowedPlayers.class:
```java
import org.bukkit.entity.Player;

public enum TestEnumeration_AllowedPlayers {
    OP {
        public boolean isAllow(Player p) {
            return p.isOp();
        }
    },
    PERMISSION {
        public boolean isAllow(Player p) {
            return p.hasPermission(SimpleConfig.PERM_CHAT_SEND);
        }
    };

    public abstract boolean isAllow(Player p);
}
```

Then you need to initialize your plugin in EasyConfig registry:
```java
@Override
public void onEnable() {
    RegisteredServiceProvider<ConfigRegistry> registry =
            Bukkit.getServicesManager().getRegistration(ConfigRegistry.class);

    if (registry == null) {
        throw new RuntimeException("Please install 'EasyConfigs'!");
    }

    registry.getProvider().addThis(this);
}
```
### Done!
Now you can use your config class:
```java
@EventHandler
public void onPlayerChatting(AsyncPlayerChatEvent evt) {
    if (!SimpleConfig.ALLOW_SETTING.isAllow(evt.getPlayer()))
        evt.setCancelled(true);
}
```
# Note
1. All the manipulations of the plugin in your classes are simple reflection, not magic. Therefore, the config classes do not need to be passed to registry directly.
2. ConfigRegistry.addThis(Plugin p) and ConfigRegistry.removeThis(Plugin p) use ClassPathUtils.getCallerClass() to prevent malicious plugins, therefore, you need to register the plugin from the main class.
3. This is an unstable build, contains many bugs, but most likely they have already been fixed in new versions ;)
