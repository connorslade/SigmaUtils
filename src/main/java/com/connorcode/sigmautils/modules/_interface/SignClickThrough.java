package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class SignClickThrough extends BasicModule {
    public SignClickThrough() {
        super("sign_click_through", "Sign Click Through",
                "Makes clicking signs / item frames in front of clickable blocks click the block", Category.Interface);
    }
}
