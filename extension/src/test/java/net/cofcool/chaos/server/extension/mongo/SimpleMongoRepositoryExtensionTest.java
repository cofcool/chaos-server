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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;
import net.cofcool.chaos.server.extension.Paging;
import net.cofcool.chaos.server.extension.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest(properties = "application-mongo.properties")
@EnableMongoRepositories(repositoryBaseClass = SimpleMongoRepositoryExtension.class)
@ExtendWith(SpringExtension.class)
class SimpleMongoRepositoryExtensionTest {

    public static final String NEW_NAME = "John 117";

    @Resource
    PersonRepository personRepository;

    Person entity;

    @BeforeEach
    void setUp() {
        entity = personRepository.insert(new Person(null, "117", "Earth UNSC", "test data", null));
    }

    @AfterEach
    void tearDown() {
        personRepository.delete(entity);
    }


    @Test
    void findAll() {
        assertFalse(personRepository.findAll().isEmpty());
    }

    @Test
    void findAllWithPage() {
        assertEquals(1, personRepository.findAll(new Query(), Paging.getPageable(0, 10)).getTotalPages());
    }

    @Test
    void update() {
        Person condition = new Person();
        condition.setId(entity.getId());
        assertTrue(personRepository.update(condition, new Update().set("name", NEW_NAME)));
        assertEquals(NEW_NAME, personRepository.findById(entity.getId()).get().getName());
    }

    @Test
    void updateWithQuery() {
        assertTrue(personRepository.update(new Query(Criteria.where("_id").is(entity.getId())), new Update().set("name", NEW_NAME)));
        assertEquals(NEW_NAME, personRepository.findById(entity.getId()).get().getName());
    }

    @Test
    void updateById() {
        assertTrue(personRepository.updateById(entity.getId(), new Update().set("name", NEW_NAME)));
        assertEquals(NEW_NAME, personRepository.findById(entity.getId()).get().getName());
    }

    @Test
    void updateByIdWithEntity() {
        Person newEntity = new Person();
        newEntity.setName(NEW_NAME);
        newEntity.setId(entity.getId());
        newEntity.setParent(new Person());
        assertTrue(personRepository.updateById(newEntity));
        Person findPerson = personRepository.findById(entity.getId()).get();
        assertEquals(NEW_NAME, findPerson.getName());
        assertNull(findPerson.getParent());
    }
}