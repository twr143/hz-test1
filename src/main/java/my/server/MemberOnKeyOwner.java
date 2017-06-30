package my.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;

import static my.client.ClientTaskOnKeyOwner.EXECUTOR_NAME;
import static my.client.ClientTaskOnKeyOwner.MAP1_NAME;

/**
 * Created by ilya on 30.06.2017.
 */
public class MemberOnKeyOwner {
  public static void main(String[] args) {
    Hazelcast.newHazelcastInstance(createConfig("1"));
  }
  private static Config createConfig(String name) {
      Config config = new Config(name);

      ExecutorConfig executorConfig = config.getExecutorConfig(EXECUTOR_NAME);
      executorConfig.setPoolSize(5);

      // map without backup

      MapConfig mapConfig1 = config.getMapConfig(MAP1_NAME);
      mapConfig1.setBackupCount(0);

      return config;
  }

}
