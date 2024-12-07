package com.lst.auto_l;

import com.lst.auto_l.modules.AutoL;
import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;

// Required @ExtensionInfo annotation
@ExtensionInfo(name = "AutoL",author = "lesetong",version = "1.1")
public class LLL extends Extension {
    public static OpenAPI openAPI;

    @Override
    public void initialize(OpenAPI openAPI) {
        LLL.openAPI = openAPI;
        openAPI.registerFeature(new AutoL());
    }
}
