package my;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Created by ilya on 30.06.2017.
 */
public class Util {

  public static ClientConfig createClientConfig(){
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.getNetworkConfig().addAddress("169.254.51.225:5701","169.254.51.225:5702","169.254.51.225:5703");
    return clientConfig;
  }
  public static HazelcastInstance createClient(){
      return HazelcastClient.newHazelcastClient(createClientConfig());
  }


}
