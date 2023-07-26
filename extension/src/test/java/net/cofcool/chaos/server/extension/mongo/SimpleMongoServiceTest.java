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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.annotation.Resource;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimplePage;
import net.cofcool.chaos.server.extension.Person;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 测试 {@link SimpleMongoService}
 * @author CofCool
 */
@DataMongoTest(properties = "de.flapdoodle.mongodb.embedded.version=6.3.1")
@EnableMongoRepositories(repositoryBaseClass = SimpleMongoRepositoryExtension.class)
class SimpleMongoServiceTest {

    @Resource
    PersonService personService;

    public static final String NEW_NAME = "John 117";

    Person entity;

    @BeforeEach
    void setUp() {
        entity = personService.insert(new Person(null, "117", "Earth UNSC", "test data", null)).entity();
    }


    @AfterEach
    void tearDown() {
        assertEquals(ResultState.SUCCESSFUL, personService.delete(entity));
    }

    @Test
    void insert() {
        Person person = new Person();
        person.setName("xxx");
        person.setAddress("877 Planet");
        ExecuteResult<Person> result = assertDoesNotThrow(() -> personService.insert(person));
        assertTrue(result.successful());
    }

    @Test
    void delete() {
        Person person = new Person();
        person.setId(ObjectId.get());
        assertEquals(ResultState.FAILURE, personService.delete(person));
    }

    @Test
    void update() {
        Person person = new Person();
        person.setId(entity.getId());
        person.setName(NEW_NAME);
        assertTrue(personService.update(person).successful());
        assertEquals(NEW_NAME, personService.queryById(entity).entity().getName());
    }

    @Test
    void queryAll() {
        assertTrue(personService.queryAll(new Person()).successful());
    }

    @Test
    void queryById() {
        assertTrue(personService.queryById(entity).successful());
    }

    @Test
    void queryWithPage() {
        assertEquals(1, personService.query(new SimplePage<>(0, 1), new Person()).page().getPageSize());
    }
}