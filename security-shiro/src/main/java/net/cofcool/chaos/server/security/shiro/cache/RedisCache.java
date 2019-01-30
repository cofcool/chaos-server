package net.cofcool.chaos.server.security.shiro.cache;

import java.util.Collection;
import java.util.Set;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache.ValueWrapper;

/**
 * 参考 {@link org.apache.shiro.cache.ehcache.EhCache} 实现
 *
 * @author CofCool
 */
public class RedisCache<K, V> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

    private org.springframework.cache.Cache cache;

    public RedisCache(org.springframework.cache.Cache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.cache = cache;
    }


    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) throws CacheException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Getting object from cache [" + cache.getName() + "] for key [" + key + "]");
            }
            if (key == null) {
                return null;
            } else {
                ValueWrapper value = cache.get(key);
                if (value != null) {
                    return (V) value.get();
                } else {
                    return null;
                }
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Putting object in cache [" + cache.getName() + "] for key [" + key + "]");
        }

        try {
            V oldValue = get(key);
            cache.put(key, value);
            return oldValue;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Removing object from cache [" + cache.getName() + "] for key [" + key + "]");
        }

        try {
            V previous = get(key);
            cache.evict(key);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public void clear() throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Clearing all objects from cache [" + cache.getName() + "]");
        }
        try {
            cache.clear();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }
}
