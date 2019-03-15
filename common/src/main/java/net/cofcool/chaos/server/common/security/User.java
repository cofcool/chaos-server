package net.cofcool.chaos.server.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

/**
 * 用户信息
 * @param <T> 用户具体数据, 通过{@link #getDetail()}获取
 * @author CofCool
 *
 * @see UserRole
 * @see UserStatus
 */
public class User <T extends Auth<D, ID>, D extends Serializable, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -7130929029282810791L;

    private ID userId;

    private String mobile;

    private String userName;

    private String nickName;

    private String avatar;

    @JsonIgnore
    private String loginPwd;

    private Collection<UserRole> roles = new HashSet<>();

    private Date registerTime;

    private Date latestLoginTime;

    private Device device;

    private Collection<UserStatus> userStatuses = new HashSet<>();

    /**
     * 用户具体数据, 根据用户级别获取对应类型的用户数据
     */
    private T detail;

    public User() {
    }

    public User(String userName, String loginPwd) {
        this(userName, loginPwd, SimpleDevice.BROWSER);
    }

    public User(String userName, String loginPwd,
        Device device) {
        this.userName = userName;
        this.loginPwd = loginPwd;
        this.device = device;
    }

    /**
     * 读取用户数据
     * @return 用户数据
     */
    public T getDetail() {
        return detail;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void addUserStatus(UserStatus status) {
        userStatuses.add(status);
    }

    public void addUserStatuses(Collection<UserStatus> statuses) {
        userStatuses.addAll(statuses);
    }

    public Collection<UserStatus> getUserStatuses() {
        return Collections.unmodifiableCollection(userStatuses);
    }

    public ID getUserId() {
        return userId;
    }

    public void setUserId(ID userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public Collection<UserRole> getRoles() {
        return Collections.unmodifiableCollection(roles);
    }

    protected void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }

    protected void setUserStatuses(
        Collection<UserStatus> userStatuses) {
        this.userStatuses = userStatuses;
    }

    public void addRole(UserRole userRole) {
        this.roles.add(userRole);
    }

    public void addRoles(Collection<UserRole> userRoles) {
        this.roles.addAll(userRoles);
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(Date latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * 创建新User
     * @param newUser newUser
     * @return 新User
     */
    @SuppressWarnings("unchecked")
    public User cloneUser(User newUser) {
        newUser.setAvatar(avatar);
        newUser.setUserId(userId);
        newUser.setMobile(mobile);
        newUser.setNickName(nickName);
        newUser.setUserName(userName);
        newUser.setLoginPwd(loginPwd);
        newUser.setRoles(roles);
        newUser.setRegisterTime(registerTime);
        newUser.setLatestLoginTime(latestLoginTime);
        newUser.setDetail(detail);
        newUser.setDevice(device);
        newUser.setUserStatuses(userStatuses);

        return newUser;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return userName.equals(((User) obj).userName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", mobile=" + mobile +
            ", userName='" + userName + '\'' +
            ", nickName='" + nickName + '\'' +
            ", avatar='" + avatar + '\'' +
            ", roles=" + roles +
            ", registerTime=" + registerTime +
            ", latestLoginTime=" + latestLoginTime +
            ", device='" + device +
            ", userStatuses=" + userStatuses +
            ", detail=" + detail +
            '}';
    }
}