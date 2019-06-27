package net.cofcool.chaos.server.demo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import net.cofcool.chaos.server.common.security.Device;
import net.cofcool.chaos.server.common.security.User;

/**
 * 用户信息
 */
public class GlobalUser extends User<UserData, Long> {

    private static final long serialVersionUID = -779628681552480539L;

    /**
     * 转"JSON"时忽略密码
     */
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    public GlobalUser() {
    }

    public GlobalUser(String username, String password) {
        super(username, password);
    }

    public GlobalUser(String username, String password,
        Device device) {
        super(username, password, device);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }
}
