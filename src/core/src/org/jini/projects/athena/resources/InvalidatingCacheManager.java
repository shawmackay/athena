/*
 * InvalidatingCacheManager.java
 *
 * Created on April 8, 2002, 10:17 AM
 */

package org.jini.projects.athena.resources;

/**
 * Similar to FlashableCacheManager, this will modify the contents of a cache on demand. However
 * this will remove an item from the cache, immediately. Thus the cache data will be reaquired o0n demand on d reinserted in to the Cache at a later time.
 * @author  calum
 */
public class InvalidatingCacheManager extends CacheManager {

    /** Creates a new instance of InvalidatingCacheManager */
    public InvalidatingCacheManager(boolean createThread) {
        super(createThread);
    }

    public void invalidate(Object identifier) {
        synchronized (cacheHashMap) {
            cacheHashMap.remove(identifier);
        }
    }
}
