package com.example.application.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Vaadin CRM")
public class LoginView extends VerticalLayout implements BeforeEnterListener {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        // add css class
        addClassName("login-view");
        // set it to full screen
        setSizeFull();
        // align items to center
        setAlignItems(Alignment.CENTER);
        // and justify contents to center
        setJustifyContentMode(JustifyContentMode.CENTER);
        // the login form should post to login path
        loginForm.setAction("login");
        // add title to page
        add(
                new H1("Vaadin CRM tutorial"),
                loginForm
        );
    }
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // check if the url has an error first
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) { // there is an error
            loginForm.setError(true);

        }
    }
}
