package ar.edu.itba.pod.grpc.server;

import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static final String GROUP_NAME = "g5";
    private static final String GROUP_PASSWORD = "g5-pass";

    public static void main(String[] args) {
        String networkInterface = null;
        boolean managementCenter = false;
        List<String> memberAddresses = new ArrayList<>();
        String publicAddress = null;

        // Search for arguments
        for (String arg : args) {
            if (arg.startsWith("-Daddress=")) {
                networkInterface = arg.substring("-Daddress=".length());
            }
            if (arg.startsWith("-DmanagementCenter=")) {
                managementCenter = Boolean.parseBoolean(arg.substring("-DmanagementCenter=".length()));
            }
            if (arg.startsWith("-Dmembers=")) {
                String members = arg.substring("-Dmembers=".length());
                memberAddresses.addAll(Arrays.asList(members.split(";")));
            }
            if (arg.startsWith("-DpublicAddress=")) {
                publicAddress = arg.substring("-DpublicAddress=".length());
            }
        }

        // Verifies if the network interface was provided
        if (networkInterface == null) {
            logger.error("No network interface provided. Use -Daddress=\"...\"");
            throw new IllegalArgumentException("No network interface provided");
        }

        logger.info("Network interface: {}", networkInterface);
        logger.info("Setting up server");

        // Config
        Config config = new Config();

        // Group config
        GroupConfig groupConfig = new GroupConfig().setName(GROUP_NAME).setPassword(GROUP_PASSWORD);
        config.setGroupConfig(groupConfig);


        // **** Network config **** //
        // Multicast config
        MulticastConfig multicastConfig = new MulticastConfig();
        JoinConfig joinConfig;
        TcpIpConfig tcpIpConfig;
        if (!memberAddresses.isEmpty()) {
            multicastConfig.setEnabled(false);
            tcpIpConfig = new TcpIpConfig().setEnabled(true);
            memberAddresses.forEach(tcpIpConfig::addMember);
            joinConfig = new JoinConfig().setMulticastConfig(multicastConfig).setTcpIpConfig(tcpIpConfig);
        }
        else {
            multicastConfig.setEnabled(true);
            joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);
        }
        // Interfaces config
        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(Collections.singletonList(networkInterface))
                .setEnabled(true);
        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);
        if (publicAddress != null) {
            networkConfig.setPublicAddress(publicAddress);
        }
        config.setNetworkConfig(networkConfig);

        // **** End Network config **** //

        // Start cluster
        Hazelcast.newHazelcastInstance(config);
    }
}
