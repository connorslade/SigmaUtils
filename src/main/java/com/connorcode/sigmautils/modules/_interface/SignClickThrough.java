package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class SignClickThrough extends Module {
    public SignClickThrough() {
        super("sign_click_through", "Sign Click Through",
                "Makes clicking signs / item frames in front of clickable blocks click the block", Category.Interface);
    }
}
