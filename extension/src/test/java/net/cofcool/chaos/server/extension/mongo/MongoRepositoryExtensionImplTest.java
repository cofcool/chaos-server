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

import javax.annotation.Resource;
import net.cofcool.chaos.server.extension.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest(properties = "application-mongo.properties")
@SpringBootConfiguration
@EnableMongoRepositories(repositoryBaseClass = MongoRepositoryExtensionImpl.class)
@ExtendWith(SpringExtension.class)
class MongoRepositoryExtensionImplTest {

    @Resource
    PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.insert(new Person(null, "117", "Earth UNSC", "test data"));
    }


    @Test
    void findAll() {
        Assertions.assertTrue(personRepository.findAll().size() > 0);
    }

    @Test
    void testFindAll() {
    }

    @Test
    void update() {
    }

    @Test
    void testUpdate() {
    }

    @Test
    void updateById() {
    }

    @Test
    void testUpdateById() {
    }
}