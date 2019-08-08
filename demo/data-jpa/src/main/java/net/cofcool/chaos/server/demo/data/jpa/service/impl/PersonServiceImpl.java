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

package net.cofcool.chaos.server.demo.data.jpa.service.impl;

import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.data.jpa.support.Paging;
import net.cofcool.chaos.server.demo.api.BaseServiceImpl;
import net.cofcool.chaos.server.demo.data.jpa.repository.PersonRepository;
import net.cofcool.chaos.server.demo.data.jpa.repository.entity.Person;
import net.cofcool.chaos.server.demo.item.PersonService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * @author CofCool
 */
@Service
public class PersonServiceImpl extends BaseServiceImpl<Person, Long, PersonRepository> implements
    PersonService<Person> {

    @Override
    protected Long getEntityId(Person entity) {
        return entity.getId();
    }

    @Override
    protected Object queryWithSp(Specification<Person> sp, Page<Person> condition, Person entity) {
        return getJpaRepository().findAll(sp, Paging.getPageable(condition));
    }
}
