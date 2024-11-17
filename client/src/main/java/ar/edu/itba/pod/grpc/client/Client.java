package ar.edu.itba.pod.grpc.client;

import ar.edu.itba.pod.grpc.Query;
import ar.edu.itba.pod.grpc.Query1;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ArgumentParser;
import utils.Arguments;

public class Client {
  private static Logger logger = LoggerFactory.getLogger(Client.class);
  private static final String GROUP_NAME = "g5";
  private static final String GROUP_PASSWORD = "g5-pass";

  public static void main(String[] args) {
    logger.info("Setting up client");

    // Client Config
    ClientConfig clientConfig = new ClientConfig();
    Arguments arguments = ArgumentParser.parse(args);

    // Group Config
    GroupConfig groupConfig = new GroupConfig().setName(GROUP_NAME).setPassword(GROUP_PASSWORD);
    clientConfig.setGroupConfig(groupConfig);

    // Client Network Config
    ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
    clientNetworkConfig.addAddress(arguments.getAddresses());
    clientConfig.setNetworkConfig(clientNetworkConfig);

    HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

    Query query;
    switch (arguments.getQuery()) {
      case 1 -> query = new Query1();
      default -> {
        logger.error("Unknown query: " + arguments.getQuery());
        return;
      }
    }

    query.execute(hazelcastInstance, arguments);

    HazelcastClient.shutdownAll();
  }
}
