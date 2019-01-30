package net.cofcool.chaos.server.demo.item;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CofCool
 */
public interface PersonDao extends JpaRepository<Person, Long> {
}
