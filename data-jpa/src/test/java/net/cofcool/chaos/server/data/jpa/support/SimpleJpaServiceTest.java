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
package net.cofcool.chaos.server.data.jpa.support;

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试 {@link SimpleJpaService}
 *
 * @author CofCool
 */
@SpringBootTest
@SpringBootApplication(scanBasePackages = "net.cofcool.chaos.server.data.jpa.support")
@AutoConfigureDataJpa
@EnableJpaRepositories
@EntityScan
class SimpleJpaServiceTest {

    @Resource
    PersonService personService;

    Person person;

    @BeforeEach
    void setUp() {
        person = new Person()
                .setId(UUID.randomUUID().toString())
                .setName("Frodo")
                .setAddress("The Shire")
                .setDesc("The Fellowship of the Ring ");
        insert();
    }

    void insert() {
        assertTrue(personService.insert(person).successful());
    }

    @AfterEach
    void delete() {
        assertEquals(ResultState.SUCCESSFUL, personService.delete(person));
    }

    @Test
    void query() {
        assertEquals(1, personService.query(Page.emptyPageForRequest(), new Person().setName(person.getId())).page().getContent().size());
    }

    @Test
    void update() {
        person.setDesc("update");
        assertTrue(personService.update(person).successful());
    }

    @Test
    void queryAll() {
        assertEquals(1, personService.queryAll(new Person().setId(person.getId())).entity().size());
    }

    @Test
    void queryById() {
        assertEquals(person.getName(), personService.queryById(person).entity().getName());
    }

    @Test
    void getEntityId() {
        String id = personService.getEntityId(person);
        assertEquals(person.getId(), id);
    }

    @Configuration
    static class AppConf {

        @Bean
        ConfigurationSupport configurationSupport() {
            return ConfigurationSupport
                    .builder()
                    .exceptionCodeManager(new ExceptionCodeManager(new SimpleExceptionCodeDescriptor()))
                    .isDebug(true)
                    .customizer(new DefaultConfigurationCustomizer())
                    .build();
        }

    }

}