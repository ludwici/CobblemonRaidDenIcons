package com.ludwici.cri.plugins.journey;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;

import static com.ludwici.cri.CobblemonRaidIcons.MODID;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyPlugin implements IClientPlugin {

    private static IClientAPI jmApi;

    @Override
    public void initialize(IClientAPI jmClientApi) {
        jmApi = jmClientApi;
        JourneyMarkerManager.init(jmApi);
        ClientEventRegistry.MAPPING_EVENT.subscribe(MODID, this::onMappingEvent);
    }

    private void onMappingEvent(MappingEvent event) {
        if (event.getStage() == MappingEvent.Stage.MAPPING_STOPPED) {
            jmApi.removeAll(MODID);
        }
    }

    @Override
    public String getModId() {
        return MODID;
    }
}
