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

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message.MessageWrapped;
import net.cofcool.chaos.server.common.core.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

/**
 * 测试 {@link ResponseBodyMessageConverter}
 * @author CofCool
 */
class ResponseBodyMessageConverterTest {

    ResponseBodyMessageConverter converter;

    @BeforeEach
    void setup() {
        converter = new ResponseBodyMessageConverter();
        converter.setConfiguration(
            ConfigurationSupport
                .builder()
                .exceptionCodeManager(
                    new ExceptionCodeManager(SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR))
                .customizer(new ConfigurationSupport.DefaultConfigurationCustomizer())
                .build()
        );
    }

    @Test
    void write() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(new Example("test"), MediaType.APPLICATION_JSON, outputMessage);
        Assertions.assertNotNull(JsonPath.parse(outputMessage.getBodyAsString()).read("$.data", Example.class));
    }

    @Test
    void writeNoWrapped() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(new ExampleNoWrapped("test"), MediaType.APPLICATION_JSON, outputMessage);
        Assertions.assertThrows(
            PathNotFoundException.class,
            () -> JsonPath.parse(outputMessage.getBodyAsString()).read("$.data", Example.class)
        );
    }

    @Test
    void canWrite() {
        Assertions.assertTrue(converter.canWrite(Result.class, MediaType.APPLICATION_JSON));
    }

    @MessageWrapped
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Example {
        private String desc;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ExampleNoWrapped {
        private String desc;
    }
}