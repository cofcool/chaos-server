/*
 *
 *  * Copyright 2019 cofcool
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package net.cofcool.chaos.server.data.jpa.support;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * "person" 对应的 Repository
 *
 * @author CofCool
 */
public interface PersonRepository extends JpaRepository<Person, String> {

}