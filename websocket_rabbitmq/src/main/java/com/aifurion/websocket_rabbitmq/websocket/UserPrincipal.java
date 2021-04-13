package com.aifurion.websocket_rabbitmq.websocket;

import java.security.Principal;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/12 10:58
 */

public class UserPrincipal implements Principal {

    private final String name;

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
