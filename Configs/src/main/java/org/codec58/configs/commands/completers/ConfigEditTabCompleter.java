package org.codec58.configs.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.codec58.configs.config.PluginConfig;
import org.codec58.configs.registry.UpdatedRegistry;
import org.codec58.configs.utils.ConfigUtils;
import org.codec58.configs.utils.reflect.FieldUtils;
import org.codec58.configs.utils.reflect.exception.ReflectionError;
import org.codec58.configs.utils.reflect.exception.ReflectionNoError;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConfigEditTabCompleter implements TabCompleter {
    private final UpdatedRegistry registry;

    public ConfigEditTabCompleter(UpdatedRegistry registry) {
        this.registry = registry;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String ignored, String[] args) {
        if (args.length == 1)
            return registry.getPluginStrings().parallelStream().toList();

        if (!registry.getPluginStrings().contains(args[0]))
            return List.of(PluginConfig.INVALID_PLUGIN.formatted(args[0]));

        List<String> cNames = new ArrayList<>();
        Set<Class<?>> cClasses = registry.getCompiledRegistry()
                .get(args[0])
                .keySet();

        cClasses.forEach(cClass -> cNames.add(ConfigUtils.getConfigName(cClass)));

        if (args.length == 2)
            return cNames;

        Class<?> cClass = null;
        for (Class<?> tccClass : cClasses) {
            if (Objects.equals(ConfigUtils.getConfigName(tccClass), args[1])) {
                cClass = tccClass;
                break;
            }
        }

        if (cClass == null)
            return List.of(PluginConfig.INVALID_CONFIG.formatted(args[1]));

        List<Field> vFields = registry.getCompiledRegistry()
                .get(args[0])
                .get(cClass)
                .parallelStream()
                .toList();

        if (args.length == 3) {
            List<String> outVFields = new ArrayList<>();
            vFields.forEach(f -> outVFields.add(ConfigUtils.getConfigVariableName(f)));
            return outVFields;
        }

        Field cField = null;
        for (Field vField : vFields) {
            if (Objects.equals(ConfigUtils.getConfigVariableName(vField), args[2])) {
                cField = vField;
                break;
            }
        }

        if (cField == null)
            return List.of(PluginConfig.INVALID_FIELD.formatted(args[2]));

        if (args.length == 4) {
            ReflectionError out = FieldUtils.getStatic(cField);
            if (out instanceof ReflectionNoError value) {
                return List.of(PluginConfig.FIELD_SET_VALUE.formatted(value.getObject()));
            } else {
                return List.of(PluginConfig.TAB_COMPLETE_ERROR);
            }
        }

        return List.of(PluginConfig.NOTHING_MORE);
    }
}
