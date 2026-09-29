package test;

import ecs.EcsSystem;

public class SendNetworkPacket implements EcsSystem {

    @Override
    public void run() {
        System.out.println("Send Network Packet");
    }
}
