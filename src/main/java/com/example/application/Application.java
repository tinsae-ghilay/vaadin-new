package com.example.application;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "flowcrmtutorial")
// this program will be installable web app. and we can customise its look and feel
// name of the app, shortName = name that appears below the icon, and offlineResources would be cached
// we will probably have to add that logo image, but let's set it to what exists for now
@NpmPackage(value = "line-awesome",version = "1.3.0")
@PWA(
        name = "Vaadin CRM",
        shortName = "CRM",
        offlinePath="offline.html",
        offlineResources = { "./images/offline.png"}
)

public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
