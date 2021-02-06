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

package net.cofcool.chaos.server.core.i18n;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试 ResourceExceptionCodeDescriptor
 * @author CofCool
 */
class ResourceExceptionCodeDescriptorTest {

    private ResourceExceptionCodeDescriptor codeDescriptor;

    @BeforeEach
    void setUp() {
        codeDescriptor = new ResourceExceptionCodeDescriptor();
        codeDescriptor.setMessageSource(new ResourceBundleMessageSource());
    }

    @Test
    void code() {
        assertEquals("test", codeDescriptor.code("test"));
        assertEquals("00", codeDescriptor.code("SERVER_OK"));
    }

    @Test
    void description() {
        assertEquals("test", codeDescriptor.description("test"));
        assertEquals("serverOk", codeDescriptor.description("SERVER_OK"));
    }

}