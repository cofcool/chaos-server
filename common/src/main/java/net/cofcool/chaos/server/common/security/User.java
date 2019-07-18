package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

/**
 * 用户信息, 包括账号, 密码, 角色等, 可通过 {@link AuthService#readCurrentUser()} 读取已登录用户数据
 * @param <T> 用户具体数据, 通过{@link #getDetail()}获取
 * @param <ID> 用户ID
 * @author CofCool
 *
 * @see UserRole
 * @see UserStatus
 */
public class User <T extends Auth, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -7130929029282810791L;

    /**
     * 用户ID
     */
    private ID userId;

    /**
     * 用户手机号码
     */
    private String mobile;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户角色
     */
    private Collection<UserRole> roles = new HashSet<>();

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 最后登录时间
     */
    private Date latestLoginTime;

    /**
     * 访问设备
     */
    private Device device;

    /**
     * 用户状态
     */
    private Collection<UserStatus> userStatuses = new HashSet<>();

    /**
     * 用户详细数据
     */
    private T detail;

    public User() {
    }

    /**
     * 创建用户
     * @param username 用户名
     * @param password 密码
     */
    public User(String username, String password) {
        this(username, password, null);
    }

    /**
     * 创建用户
     * @param username 用户名
     * @param password 密码
     * @param device 访问设备
     */
    public User(String username, String password, Device device) {
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        newUser.setUsername(username);
        newUser.setPassword(password);
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
            return username.equals(((User) obj).username);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", mobile=" + mobile +
            ", username='" + username + '\'' +
            ", avatar='" + avatar + '\'' +
            ", roles=" + roles +
            ", registerTime=" + registerTime +
            ", latestLoginTime=" + latestLoginTime +
            ", device='" + device +
            ", userStatuses=" + userStatuses +
            ", detail=" + detail +
            '}';
    }

    /**
     * 读取 <code>detail</code>, 针对在没有指定范型的情况, 常规情况请调用 {@link #getDetail()}
     * @param type <code>detail</code> 对应的 <code>Class</code>
     * @param <D> <code>detail</code> 的类型
     * @return <code>detail</code>
     *
     * @throws ClassCastException <code>detail</code> 不是 <code>type</code> 类型时抛出
     */
    @SuppressWarnings("unchecked")
    public <D> D unwrap(Class<D> type) {
        if (type.isAssignableFrom(getDetail().getClass())) {
            return (D) getDetail();
        }

        throw new ClassCastException("detail cannot cast to be " + type.getName());
    }

}