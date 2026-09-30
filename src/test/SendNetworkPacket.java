package test;

import ecs.EcsSystem;
import ecs.World;

public class SendNetworkPacket implements EcsSystem {

    public void run(World world) {
        System.out.println("Send Network Packet");
    }
}
