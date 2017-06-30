package my.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.MapStore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ilya on 30.06.2017.
 */
public class Server1 {
  public static final String mapName=Server1.class.getCanonicalName();
  public static void main(String[] args) {
    Config config = createNewConfig(mapName);
    Hazelcast.newHazelcastInstance(config);
  }


  protected static Config createNewConfig(String mapName) {
      SimpleStore simpleStore = new SimpleStore();

      MapStoreConfig mapStoreConfig = new MapStoreConfig();
      mapStoreConfig.setImplementation(simpleStore);
      mapStoreConfig.setWriteDelaySeconds(1);
//        mapStoreConfig.setWriteCoalescing(false); //?

      XmlConfigBuilder configBuilder = new XmlConfigBuilder();
      Config config = configBuilder.build();
      MapConfig mapConfig = config.getMapConfig(mapName);
      mapConfig.setMapStoreConfig(mapStoreConfig);

      return config;
  }

  private static class SimpleStore implements MapStore<Integer, Integer> {

      private ConcurrentMap<Integer, Integer> store = new ConcurrentHashMap<Integer, Integer>();

      @Override
      public void store(Integer key, Integer value) {
          store.put(key, value);
      }

      @Override
      public void storeAll(Map<Integer, Integer> map) {
          System.out.println("store all: "+map.size());
          Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
          for (Map.Entry<Integer, Integer> entry : entrySet) {
              Integer key = entry.getKey();
              Integer value = entry.getValue();
              store(key, value);
          }
      }

      @Override
      public void delete(Integer key) {
      }

      @Override
      public void deleteAll(Collection<Integer> keys) {
      }

      @Override
      public Integer load(Integer key) {
          System.out.println("load: "+key);
          return store.get(key);
      }

      @Override
      public Map<Integer, Integer> loadAll(Collection<Integer> keys) {
          System.out.println("load all: "+keys.size());
          Map<Integer, Integer> map = new HashMap<Integer, Integer>();
          for (Integer key : keys) {
              Integer value = load(key);
              map.put(key, value);
          }
          return map;
      }

      @Override
      public Set<Integer> loadAllKeys() {
          return store.keySet();
      }
  }


}
