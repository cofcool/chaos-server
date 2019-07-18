package net.cofcool.chaos.server.demo.data.jpa.service.impl;

import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.data.jpa.support.Paging;
import net.cofcool.chaos.server.demo.api.BaseServiceImpl;
import net.cofcool.chaos.server.demo.item.PersonService;
import net.cofcool.chaos.server.demo.data.jpa.repository.PersonRepository;
import net.cofcool.chaos.server.demo.data.jpa.repository.entity.Person;
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
