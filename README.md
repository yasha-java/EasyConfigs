# How to
Do this:
```pom
<dependencies>
    <dependency>
        <groupId>org.codec58</groupId>
        <artifactId>EasyConfigsAPI</artifactId>
        <version>0.1_BETA</version>
        <scope>provided</scope> <!-- IMPORTANT -->
        <!-- if you don't have this library installed in your local maven -->
        <!-- <systemPath>${basedir}/libs/EasyConfigsAPI.jar</systemPath> -->
    </dependency>
</dependencies>
```
This:
```java
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;

@Config(name = "simple_config")
public class SimpleConfig {
    @ConfigVariable(name = "chat.hello")
    public static String HELLO = "Default value";
}
```
This:
```java
RegisteredServiceProvider<ConfigRegistry> registry =
        Bukkit.getServicesManager().getRegistration(ConfigRegistry.class);

if (registry == null) {
    throw new RuntimeException("Please install 'Configs'!");
}

registry.getProvider().addThis(this);
```
Done!

Now all classes annotated as 'Config' have received new values in their fields due to reflection. But the way, You can also use commands to change fields in real time:
    -/configedit ConfigsTest chat.hello Hello,_World! //('\_' is space)
Now it needs to be saved:
    -/configsave
Success!

You can also edit the file directly. In this config manager, configs are saved in JSON format (use /config reload to reload all plugins).

# NOTE
'Registry.AddThis()' only works if the successor is called from the singletone JavaPlugin. Therefore, when you try to register it from another class, it will cause an error.
