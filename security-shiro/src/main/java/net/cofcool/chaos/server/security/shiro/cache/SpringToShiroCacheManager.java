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

package net.cofcool.chaos.server.security.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

/**
 * @author CofCool
 */
public class SpringToShiroCacheManager implements CacheManager, Initializable, Destroyable {

    private static final Logger log = LoggerFactory.getLogger(SpringToShiroCacheManager.class);

    protected org.springframework.cache.CacheManager manager;

    public org.springframework.cache.CacheManager getManager() {
        return manager;
    }

    public void setManager(org.springframework.cache.CacheManager manager) {
        this.manager = manager;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Acquiring Cache instance named [" + name + "]");
        }

        try {
            org.springframework.cache.Cache cache = ensureCacheManager().getCache(name);
            if (cache == null) {
                if (log.isErrorEnabled()) {
                    log.error("Cache with name '{}' does not yet exist.  Please make sure the cache exists.", name);
                }
                throw new CacheException("Please make sure the cache exists");

            }

            return new SpringCache<>(cache);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() throws CacheException {

    }

    private org.springframework.cache.CacheManager ensureCacheManager() {
        try {
            if (this.manager == null) {
                if (log.isDebugEnabled()) {
                    log.debug(
                        "cacheManager property not set.  Constructing CacheManager instance... ");
                }

                this.manager = ContextLoader.getCurrentWebApplicationContext()
                    .getBean(org.springframework.cache.CacheManager.class);
                if (log.isTraceEnabled()) {
                    log.trace("get CacheManager instance from cached beans.");
                }

            }

            return this.manager;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
