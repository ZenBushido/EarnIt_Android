package com.firepitmedia.earnit.stickyEvent;

import com.firepitmedia.earnit.model.BlockingApp;

import java.util.List;

public class BlockingApps {

    List<BlockingApp> blockingApps;

    public List<BlockingApp> getBlockingApps() {
        return blockingApps;
    }

    public void setBlockingApps(List<BlockingApp> blockingApps) {
        this.blockingApps = blockingApps;
    }

    @Override
    public String toString() {
        return "BlockingApps{" +
                "blockingApps=" + blockingApps +
                '}';
    }
}
