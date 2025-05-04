package com.example.application.views;

import com.example.application.services.CRMService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

// DashboardView extends verticalLayout
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Vaadin CRM")
@PermitAll
public class DashboardView extends VerticalLayout {

    // service that we delegate for data connection
    private final CRMService service;

    public DashboardView(CRMService service) {
        this.service = service;
        // css class name for styling
        addClassNames("dashboard-view");
        // horizontal alignment set to center
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        // add components
        add(getContacts(), getCompanies());
    }

    // pie chart to display company statistics
    private Component getCompanies() {

        // let's try this pro version if it works as a trial
        Chart pie = new Chart(ChartType.PIE);
        // data for the pie chart
        DataSeries dataSeries = new DataSeries();
        // count employees of each company and add the data to dataSeries
        // shouldn't we do this in a background thread?
        // because as it is now, this is blocking.
        // so probably better if the data is big
        service.findAllCompanies() .forEach(c ->
                dataSeries.add(
                        new DataSeriesItem(c.getName(), c.employeesCount())
                )
        );
        // configure pie chart to use the data series
        pie.getConfiguration().setSeries(dataSeries);
        return pie;
    }

    private Component getContacts() {
        // span containing how many contacts there are
        Span stats = new Span(service.countContacts() +" contacts in total");
        // set text size to extra large, and margin top to medium
        stats.addClassNames("text-xl","mt-m");
        return stats;
    }
}
