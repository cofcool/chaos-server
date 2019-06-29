package net.cofcool.chaos.server.demo.api;

import net.cofcool.chaos.server.common.security.AbstractDevice;


public class Device extends AbstractDevice {

    private static final long serialVersionUID = 335881722377426359L;

    protected Device(String identifier, String desc) {
        super(identifier, desc);
    }

    public static final String IDENTIFIER_BROWSER = "0";
    public static final String IDENTIFIER_WECHAT = "2";

    public static final Device BROWSER = new Device(IDENTIFIER_BROWSER, "BROWSER");
    public static final Device WECHAT = new Device(IDENTIFIER_WECHAT, "WECHAT");

    public String getIdentifier() {
        return identifier();
    }
}
