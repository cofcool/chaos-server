package net.cofcool.chaos.server.common.security;

/**
 * {@link Device} 抽象实现
 *
 * @author CofCool
 */
public abstract class AbstractDevice implements Device {

    private String identifier;

    private String desc;

    private AbstractDevice() {

    }

    protected AbstractDevice(String identifier, String desc) {
        this.identifier = identifier;
        this.desc = desc;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String desc() {
        return desc;
    }

}
