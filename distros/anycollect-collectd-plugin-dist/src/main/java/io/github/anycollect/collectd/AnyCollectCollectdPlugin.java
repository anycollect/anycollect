package io.github.anycollect.collectd;

import io.github.anycollect.AnyCollect;
import io.github.anycollect.extensions.substitution.EnvVarSubstitutor;
import org.collectd.api.Collectd;
import org.collectd.api.CollectdConfigInterface;
import org.collectd.api.CollectdInitInterface;
import org.collectd.api.OConfigItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class AnyCollectCollectdPlugin implements CollectdInitInterface, CollectdConfigInterface {
    public static final String NAME = "AnyCollect";

    public AnyCollectCollectdPlugin() {
        Collectd.registerInit(NAME, this);
    }

    @Override
    public int init() {
        return 0;
    }

    @Override
    public int config(final OConfigItem oConfigItem) {
        String config = "anycollect.yaml";
        for (OConfigItem item : oConfigItem.getChildren()) {
            if ("config".equalsIgnoreCase(item.getKey())) {
                config = item.getValues().get(0).getString();
            }
        }
        AnyCollect anyCollect;
        try {
            anyCollect = new AnyCollect(new File(config), new EnvVarSubstitutor());
        } catch (FileNotFoundException e) {
            Collectd.logError("file " + config + " not found");
            return 1;
        } catch (IOException e) {
            Collectd.logError(e.getMessage());
            return 1;
        }
        anyCollect.run();
        return 0;
    }
}
