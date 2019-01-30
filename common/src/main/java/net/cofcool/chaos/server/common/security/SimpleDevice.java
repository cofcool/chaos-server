package net.cofcool.chaos.server.common.security;

/**
 * Device 简单实现
 *
 * @author CofCool
 */
public class SimpleDevice extends AbstractDevice {

    private static final long serialVersionUID = -2925551374324782406L;

    public static final String IDENTIFIER_BROWSER= "0";
    public static final String IDENTIFIER_CLIENT = "1";

    public static final Device BROWSER = new SimpleDevice(IDENTIFIER_BROWSER, "Browser");

    public static final Device CLIENT = new SimpleDevice(IDENTIFIER_CLIENT, "Client");

    private SimpleDevice(String identifier, String desc) {
        super(identifier, desc);
    }

}
