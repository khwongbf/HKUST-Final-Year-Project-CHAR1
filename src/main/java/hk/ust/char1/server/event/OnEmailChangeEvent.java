package hk.ust.char1.server.event;

import hk.ust.char1.server.model.User;
import org.springframework.context.ApplicationEvent;

public class OnEmailChangeEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private String appUrl;

    private User user;

    public OnEmailChangeEvent(User user, String appUrl) {
        super(user);
        this.appUrl = appUrl;
        this.user = user;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
