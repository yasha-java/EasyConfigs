package org.codec58.configs.utils;

import org.codec58.configs.utils.errors.ConfigValueError;
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigUpdaterTest extends ConfigUpdater {

    public enum TestEnum {
        SomeConstant0, SomeConstant1
    }

    @Config(name = "test")
    public static class TestConfigClass {
        @ConfigVariable(name = "test")
        public static TestEnum TEST = TestEnum.SomeConstant0;
        @ConfigVariable(name = "test2")
        public static boolean TEST2 = false;
    }

    private final JSONObject testConfig = new JSONObject(
            """
                   {
                       "test": "SomeConstant1",
                       "test2": true
                   }
                   """
    );

    @Test
    public void testPrepareEnumeration() throws Throwable {
        ConfigValueError err = ConfigUpdater.prepareEnumeration(testConfig, null, TestConfigClass.class, TestConfigClass.class.getDeclaredField("TEST"));
        assertNull(err);
        assertEquals(TestEnum.SomeConstant1, TestConfigClass.TEST);
    }

    @Test
    public void testPrepareDefaultObject() throws Throwable {
        ConfigValueError err = ConfigUpdater.prepareDefaultObject(testConfig, null, TestConfigClass.class, TestConfigClass.class.getDeclaredField("TEST2"));
        assertNull(err);
        assertTrue(TestConfigClass.TEST2);
    }
}