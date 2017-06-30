package my.client;

import com.hazelcast.core.*;
import com.hazelcast.durableexecutor.DurableExecutorService;
import my.Util;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by ilya on 30.06.2017.
 */
public class ClientTaskOnKeyOwner {
  public static final String MAP1_NAME = "map1";
  public static final int TASK_QUANTITY = 1000;
  public static final String EXECUTOR_NAME = "exe";


  public static void main(String[] args) {
         run();
  }
  static void run() {
    HazelcastInstance client = Util.createClient();
    // first not synced executor service
    testPlan(client, false);
    // now durable executor service
    System.out.println("durable");
    testPlan(client, true);




    client.shutdown();
  }
  private static void testPlan(HazelcastInstance client, boolean durable){
    ExecutorService executorService =  getExecService(client,durable,EXECUTOR_NAME);
    IMap<Integer, Integer> map1 = client.getMap(MAP1_NAME);
    map1.put(0, 0);
    map1.put(1, 0);
    map1.put(2, 0);
    map1.put(3, 0);
    map1.put(4, 0);
    Partition partition = null;

    int key=0,i=0;
    while (i++ < TASK_QUANTITY) {

      key=(i-1)%5;
       IMapTask task = new IMapTask(key,i);
      Future<Long> f=durable?((DurableExecutorService)executorService).submitToKeyOwner(task, key):
              ((IExecutorService)executorService).submitToKeyOwner(task, key);
    }

    try {
      Thread.sleep(1500);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
  private static ExecutorService getExecService(HazelcastInstance client, boolean durable, String name){
      return durable?client.getDurableExecutorService(name):client.getExecutorService(name);
  }
  public static class IMapTask implements Callable<Long>, Serializable, HazelcastInstanceAware {

      private HazelcastInstance hazelcastInstance;

      private int key = -1;

      private int version;

      private static long startTime;


      public IMapTask(int key, int version) {
        this.key = key;
        this.version=version;
      }
    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
      this.hazelcastInstance = hazelcastInstance;
    }
    @Override
    public Long call() {
        if (version==1)
          startTime = System.currentTimeMillis();
      IMap<Integer, Integer> map1 = hazelcastInstance.getMap(MAP1_NAME);
      //            map1.lock(key);
      System.out.printf("%d\t entry\t key:%d\t th:%d\t node:%s%n", version,key, Thread.currentThread().getId(), hazelcastInstance.getCluster().getLocalMember().getUuid());
      //            try {

                      int value1 = map1.get(key);


                      map1.put(key, value1+1);

      //            } finally {
      System.out.printf("%d\t exit\t key:%d\t th:%d\t node:%s%n",version, key, Thread.currentThread().getId(), hazelcastInstance.getCluster().getLocalMember().getUuid());
      //                map1.unlock(key);
      //            }
      if (version == TASK_QUANTITY) {
        System.out.println(" done for : " + String.format("%8.3f", (double) (System.currentTimeMillis() - startTime) / 1000));
      }
      return 0L;
    }
  }
}