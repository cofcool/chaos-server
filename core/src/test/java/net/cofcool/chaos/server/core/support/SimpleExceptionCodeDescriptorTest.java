/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.core.support;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试 SimpleExceptionCodeDescriptor
 * @author CofCool
 */
class SimpleExceptionCodeDescriptorTest {

    private final SimpleExceptionCodeDescriptor codeDescriptor = new CustomizeTest();

    @BeforeEach
    void setup() throws Exception {
        codeDescriptor.afterPropertiesSet();
    }

    @Test
    void code() {
        assertEquals(SimpleExceptionCodeDescriptor.AUTH_ERROR_VAL, codeDescriptor.code(ExceptionCodeDescriptor.AUTH_ERROR));
        assertEquals("NONE", codeDescriptor.code("NONE"));
    }

    @Test
    void description() {
        assertEquals(SimpleExceptionCodeDescriptor.AUTH_ERROR_DESC_VAL, codeDescriptor.description(ExceptionCodeDescriptor.AUTH_ERROR));
        assertEquals("NONE", codeDescriptor.code("NONE"));
    }

    @Test
    void customize() {
        assertEquals("testCode", codeDescriptor.code("test"));
        assertEquals("testDesc", codeDescriptor.description("test"));
    }


    private static class CustomizeTest extends SimpleExceptionCodeDescriptor {
        @Override
        protected Map<String, CodeMessage> customize() {
            return Map.of("test", ExceptionCodeDescriptor.message("testCode", "testDesc"));
        }
    }
}