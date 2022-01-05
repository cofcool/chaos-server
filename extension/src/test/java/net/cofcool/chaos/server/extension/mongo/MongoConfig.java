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

package net.cofcool.chaos.server.extension.mongo;

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 测试配置
 * @author CofCool
 */
@SpringBootConfiguration
public class MongoConfig {

    @Bean
    public PersonService personService() {
        return new PersonService();
    }

    @Bean
    public ConfigurationSupport configurationSupport() {
        return ConfigurationSupport
            .builder()
            .exceptionCodeManager(new ExceptionCodeManager(SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR))
            .isDebug(true)
            .customizer(new DefaultConfigurationCustomizer())
            .build();
    }

}
