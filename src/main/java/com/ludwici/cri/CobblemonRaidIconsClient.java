package com.ludwici.cri;

import net.fabricmc.api.ClientModInitializer;

public class CobblemonRaidIconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChunkScanListener.register();
    }
}
