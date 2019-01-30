package net.cofcool.chaos.server.demo.item;

import net.cofcool.chaos.server.common.security.Auth;

/**
 * @author CofCool
 */
public class UserData implements Auth<String, String> {

    private static final long serialVersionUID = 2651213471622715024L;

    private String id;

    private String data;

    public UserData() {
    }

    public UserData(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getData() {
        return data;
    }
}
