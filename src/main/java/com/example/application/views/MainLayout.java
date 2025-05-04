package com.example.application.views;

import com.example.application.security.SecurityService;
import com.example.application.views.list.ListView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

// IMPORTANT !!!
// we pass this to listView as route layout
public class MainLayout extends AppLayout {

    private final SecurityService service;

    public MainLayout(SecurityService service) {
        this.service = service;
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        // the drawer contains link to all our views,
        // and it is where we implement our routing
        // Route links
        RouterLink listView = new RouterLink("list", ListView.class); // navigates to ListView
        RouterLink dashboard = new RouterLink("dashboard", DashboardView.class); // navigate to dashboard
        // highlighting.
        // because we don't want the selected link to stay highlighted,
        // we set highlightConditions.sameLocation()
        listView.setHighlightCondition(HighlightConditions.sameLocation());
        // add the link to drawer in a vertical layout so that the links be aligned on top of each other
        addToDrawer(new VerticalLayout(listView, dashboard));
    }

    private void createHeader() {
        // header text
        H1 logo = new H1("CRM Tutorial");
        // add css class names
        // builtin css classes available in the vaadin lumo design system
        // text-l = large text and "m-m" = margin medium
        logo.addClassNames("text-l","m-m");
        // logout button
        Button logout = new Button("Logout", e -> service.logOut());
        logout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        // create a Horizontal layout for the logo, with the header text (logo) and a drawer
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo,logout);
        // align components to center vertically
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // expand the logo to fill any extra space
        header.expand(logo);
        // expand the header to fill the whole page
        header.setWidthFull();
        // add utility class names for padding x and padding y
        // in this case padding left and right = 0, and medium paddingTop
        header.addClassNames("py-0", "px-m");
        // add this to navBar.
        addToNavbar(header);
    }
}
