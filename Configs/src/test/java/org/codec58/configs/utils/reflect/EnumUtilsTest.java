package org.codec58.configs.utils.reflect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EnumUtilsTest {
    enum TestEnum {
        SomeConstant0, SomeConstant1, SomeConstant2
    }

    @Test
    public void getEnumConstant() {
        Object constant = EnumUtils.getEnumConstantByName("SomeConstant0", TestEnum.class);
        Assertions.assertEquals(TestEnum.SomeConstant0, constant);
    }
}