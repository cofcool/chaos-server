package net.cofcool.chaos.server.demo.data.jpa.repository;

import net.cofcool.chaos.server.demo.data.jpa.repository.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CofCool
 */
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
}
