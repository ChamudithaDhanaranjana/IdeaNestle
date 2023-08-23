package com.blog.IdeaNestle.payload.request;

import com.blog.IdeaNestle.model.User;

public class UserStateUpdateRequest {
    private User.UserState state;

    public User.UserState getState() {
        return state;
    }

    public void setState(User.UserState state) {
        this.state = state;
    }
}
