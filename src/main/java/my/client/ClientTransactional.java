package my.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;
import my.server.Server1;

/**
 * Created by ilya on 30.06.2017.
 */
public class ClientTransactional {
  public static void main(String[] args) throws Exception {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.getNetworkConfig().addAddress("169.254.51.225:5701","169.254.51.225:5702");
    HazelcastInstance node = HazelcastClient.newHazelcastClient(clientConfig);
    node.executeTransaction(new TransactionalTask<Object>() {
        @Override
        public Object execute(TransactionalTaskContext context) throws TransactionException {
            TransactionalMap<Integer, Integer> map = context.getMap(Server1.mapName);
            map.put(1, 1);
            map.put(2, 1);
            map.put(3, 1);
            map.put(4, 1);
            map.put(5, 1);
            System.out.println("tr write done");
            return null;
        }
    });
    Thread.sleep(2000);
    node.executeTransaction(new TransactionalTask<Object>() {
        @Override
        public Object execute(TransactionalTaskContext context) throws TransactionException {
            TransactionalMap<Integer, Integer> map = context.getMap(Server1.mapName);
            map.get(1);
            map.get(2);
            System.out.println("tr read done");
            return null;
        }
    });
    Thread.sleep(2000);
    node.executeTransaction(new TransactionalTask<Object>() {
        @Override
        public Object execute(TransactionalTaskContext context) throws TransactionException {
            TransactionalMap<Integer, Integer> map = context.getMap(Server1.mapName);
            map.put(1,2);
            map.put(2,2);
            System.out.println("tr write 2 done");
            return null;
        }
    });
    Thread.sleep(2000);
    try {
        node.executeTransaction(new TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Integer, Integer> map = context.getMap(Server1.mapName);
                map.remove(1);
                throw new TransactionException("wanna rollback");
//                System.out.println("evict done");
//                return null;
            }
        });
    }catch (Exception e){
        e.printStackTrace();
    }
    Thread.sleep(2000);
    node.executeTransaction(new TransactionalTask<Object>() {
        @Override
        public Object execute(TransactionalTaskContext context) throws TransactionException {
            TransactionalMap<Integer, Integer> map = context.getMap(Server1.mapName);
            System.out.println("map size:"+map.size());
            System.out.printf("map values:%s%n", map.values());

            return null;
        }
    });
    Thread.sleep(2000);

    System.out.println("Finished");
    node.shutdown();

  }
}
