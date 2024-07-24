package org.codec58.configs.utils;

import org.bukkit.plugin.Plugin;
import org.codec58.configs.Convertor;
import org.codec58.configs.utils.errors.ConfigValueError;
import org.codec58.configs.utils.reflect.EnumUtils;
import org.codec58.configs.utils.reflect.FieldUtils;
import org.codec58.configs.utils.reflect.exception.ReflectionError;
import org.codec58.configs.utils.reflect.exception.ReflectionNoError;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigUpdater {
    public static List<ConfigValueError> updateValues(Plugin plugin, JSONObject config, Class<?> configClass) {
        List<ConfigValueError> errors = new ArrayList<>();

        Set<Field> fields = ConfigUtils.getConfigFields(configClass);
        for (Field variable : fields) {
            Class<?> variableType = variable.getType();

            ConfigValueError cvError;
            if (variableType.isEnum())
                cvError = prepareEnumeration(config, plugin, configClass, variable);
            else
                cvError = prepareDefaultObject(config, plugin, configClass, variable);

            if (cvError != null)
                errors.add(cvError);
        }

        return errors;
    }

    protected static ConfigValueError prepareEnumeration(JSONObject config, Plugin plugin, Class<?> configClass, Field variable) {
        String cvName = ConfigUtils.getConfigVariableName(variable);
        if (!config.has(cvName))
            return new ConfigValueError("The config file does not have a value", plugin, configClass, variable);

        String cfValue = config.getString(cvName);

        Object eValue = EnumUtils.getEnumConstantByName(cfValue, variable.getType());
        if (eValue == null)
            return new ConfigValueError("Error while parsing enumeration", plugin, configClass, variable);

        ReflectionError setError = FieldUtils.setStatic(variable, eValue);
        if (setError instanceof ReflectionNoError)
            return null;
        else return new ConfigValueError(setError.getMessage(), plugin, configClass, variable);
    }

    protected static ConfigValueError prepareDefaultObject(JSONObject config, Plugin plugin, Class<?> configClass, Field variable) {
        String cvName = ConfigUtils.getConfigVariableName(variable);
        if (!config.has(cvName))
            return new ConfigValueError("The config file does not have a value", plugin, configClass, variable);

        Object oValue = Convertor.parseObject(config.get(cvName), variable.getType());
        if (oValue == null)
            return new ConfigValueError("Error while parsing object", plugin, configClass, variable);

        ReflectionError setError = FieldUtils.setStatic(variable, oValue);
        if (setError instanceof ReflectionNoError)
            return null;
        else return new ConfigValueError(setError.getMessage(), plugin, configClass, variable);
    }
}
