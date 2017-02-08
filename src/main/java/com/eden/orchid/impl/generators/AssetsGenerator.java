package com.eden.orchid.impl.generators;

import com.eden.common.json.JSONElement;
import com.eden.orchid.Orchid;
import com.eden.orchid.api.generators.OrchidGenerator;
import com.eden.orchid.api.resources.OrchidPage;
import com.eden.orchid.api.resources.OrchidResource;
import com.eden.orchid.api.registration.AutoRegister;
import com.eden.orchid.utilities.OrchidUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@AutoRegister
public class AssetsGenerator implements OrchidGenerator {

    private List<OrchidPage> assets;

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public String getName() {
        return "assets";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public JSONElement startIndexing() {
        JSONObject siteAssets = new JSONObject();

        List<OrchidResource> resources = Orchid.getContext().getResources().getResourceEntries("assets", null, true);
        assets = new ArrayList<>();

        for (OrchidResource entry : resources) {
            OrchidPage asset = new OrchidPage(entry);

            assets.add(asset);

            JSONObject index = new JSONObject();
            index.put("name", asset.getReference().getTitle());
            index.put("title", asset.getReference().getTitle());
            index.put("url", asset.getReference().toString());

            OrchidUtils.buildTaxonomy(entry, siteAssets, index);
        }

        return new JSONElement(siteAssets.optJSONObject("assets"));
    }

    @Override
    public void startGeneration() {
        for (OrchidPage asset : assets) {
            asset.renderRawContent();
        }
    }
}

