package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

/**
 * 登陆数据
 *
 * @author CofCool
 */
public abstract class AbstractLogin implements Serializable {


    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 请求设备
     */
    private Device device;


    public AbstractLogin() {
    }

    public AbstractLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 解析请求设备
     * @param servletRequest 请求
     */
    public void parseDevice(HttpServletRequest servletRequest) {
        setDevice(checkRequestAgent(servletRequest));
    }

    /**
     * 解析请求设备
     * @param servletRequest 请求
     * @return 设备数据
     */
    protected abstract Device checkRequestAgent(HttpServletRequest servletRequest);

    public Device getDevice() {
        return device;
    }

    protected void setDevice(Device device) {
        this.device = device;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    /**
     * 默认登陆实现
     */
    public static class DefaultLogin extends AbstractLogin {

        private static final long serialVersionUID = -1025027878325152656L;

        public DefaultLogin(String username, String password) {
            super(username, password);
        }

        public DefaultLogin() {
        }

        @Override
        protected Device checkRequestAgent(HttpServletRequest servletRequest) {
            return SimpleDevice.BROWSER;
        }

    }


}
