/*
 * FlashableCacheManager.java
 *
 * Created on April 8, 2002, 10:21 AM
 */

package org.jini.projects.athena.resources;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jini.projects.athena.command.Command;

/**
 * Manages a set of Flashable Object instances. The manager can be told
 * to update certain objects. The criteria for object selection and midification is
 * a Regular Expression_type matched against object keys. Any items that match the pattern
 * are <i>'flashed'</i> i.e. told to update.
 *
 * @author  calum
 */
public class FlashableCacheManager extends CacheManager {
    
    private Logger log = Logger.getLogger("org.jini.projects.athena.resources");
    /** Creates a new instance of FlashableCacheManager */
    public FlashableCacheManager(boolean createThread) {
        super(createThread);
    }

    /**
     * Update the single object with the given identifier
     */
    public void flashCache(Object identifier) {
        Flashable flashOb = (Flashable) cacheHashMap.get(identifier);
        flashOb.applyFlash();
    }

    /**
     * Update all the objects in the cache which match the given pattern.
     * Returns the number of objects that were updated. The pattern must be a valid regular expression.
     * @see java.util.regex.Pattern
     */

    public int flashPatternCache(String pattern) {
        log.log(Level.FINE, "Received pattern " + pattern + " for update");
        java.util.Set keySet = cacheHashMap.keySet();
        java.util.Iterator keys = keySet.iterator();
        int numflashed = 0;
        while (keys.hasNext()) {
            Object key = keys.next();

            if (key instanceof String) {
                String name = String.valueOf(key);
                log.log(Level.FINE, "Flashing caches with pattern: " + pattern);

                //    String cacheKey="TESTP12\\d\\dR$policy_no";

               log.log(Level.FINE, "Found a matching cache Object:  " + name);
                FlashCachedObject fco = (FlashCachedObject) cacheHashMap.get(key);
                if (fco.isExpired()) {
                    cacheHashMap.remove(key);

                } else {
                    Command comm = (Command) fco.getInitiator();

                    TreeMap map = comm.getParameters();
                    Set entrset = map.entrySet();
                    Iterator iter = entrset.iterator();
                    StringBuffer x = new StringBuffer(32);
                    while (iter.hasNext()) {
                        Map.Entry attr = (Map.Entry) iter.next();
                        Pattern p1 = Pattern.compile("\\$\\{" + attr.getKey() + "\\}");
                        Matcher m1 = p1.matcher(pattern);
                        boolean result = m1.find();
                        while (result) {
                            m1.appendReplacement(x, attr.getValue().toString());
                            result = m1.find();
                        }
                    }
                    log.log(Level.FINE, "Buffer: " + x);
                    Pattern p = Pattern.compile(x.toString());
                    Matcher m = p.matcher(key.toString());

                    if (m.matches()) {
                        log.log(Level.FINE, "Matches");
                        fco.applyFlash();
                        numflashed++;
                    } else
                        log.log(Level.FINE, "Doesn't Match");

                }
            }
        }
        return numflashed;
    }

}
