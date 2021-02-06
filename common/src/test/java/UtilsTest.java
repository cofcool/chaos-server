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

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void configurationSupportTest() {
        ConfigurationSupport.builder()
            .exceptionCodeManager(new ExceptionCodeManager(
                new ExceptionCodeDescriptor() {
                    @Override
                    public String code(String type) {
                        return null;
                    }

                    @Override
                    public String description(String type) {
                        return null;
                    }
                })
            )
            .customizer(new DefaultConfigurationCustomizer())
            .build();

        Assertions.assertNotNull(ConfigurationSupport.getConfiguration());
    }

}
