package com.github.flotskiy.bookshop.security;

import com.github.flotskiy.bookshop.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import org.springframework.context.ApplicationEvent;

public class OnChangeUserDataEvent extends ApplicationEvent {

    private String url;
    private transient UserDto userDto;
    private transient ChangeUserDataConfirmPayload payload;

    public OnChangeUserDataEvent(UserDto userDto, String url, ChangeUserDataConfirmPayload payload) {
        super(userDto);
        this.url = url;
        this.userDto = userDto;
        this.payload = payload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public ChangeUserDataConfirmPayload getPayload() {
        return payload;
    }

    public void setPayload(ChangeUserDataConfirmPayload payload) {
        this.payload = payload;
    }
}
