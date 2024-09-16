package com.rj.sunbase.Security;

public interface UserService {

    public User registerUser(User user);

    public User authenticateUser(String username, String password);

}
