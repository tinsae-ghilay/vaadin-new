package com.example.application.views.list;

import com.example.application.data.Contact;
import com.example.application.services.CRMService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.Servlet;

import java.util.Collections;

@PageTitle("Contacts | Vaadin CRM")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class ListView extends VerticalLayout {

    // grid /table
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();

    // add field contact form
    ContactForm form;

    // crm service for accessing database
    private final CRMService crmService;

    // this is where we bootstrap our views / start creating views
    // all that we have defined above can be added to the page here
    public ListView(CRMService service) {
        this.crmService = service;
        // we add css class name (for a custom css styling)
        addClassNames("list-view");
        // make this view fill the page.
        // by default, list view would wrap to content
        setSizeFull();
        // configure the contacts list (grid)
        configureGrid();
        // configure contact form
        configureForm();
        add(
                // we add toolbar first
                getToolBar(),
                // we add contents (grid and form)
                getContent()
        );

        // update list items at first start
        updateList();

        // when we first start the application, form should be hidden
        closeForm();

    }

    // this updates grid items by getting search parameter from filterText
    private void updateList() {
        grid.setItems(crmService.findAllContacts(filterText.getValue()));
    }

    private Component getContent() {
        // this content will hold the grid and ContactForm in a horizontal layout
        HorizontalLayout content = new HorizontalLayout(grid,form);
        // tell layout how components are spaced
        content.setFlexGrow(2,grid);
        content.setFlexGrow(1,form);
        content.setClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        // define form.
        // we pass companies list and statuses list from crmService
        form = new ContactForm(crmService.findAllCompanies(), crmService.findAllStatuses());
        // set width to form
        form.setWidth("25em");
        // form
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CancelEvent.class ,cancelEvent -> closeForm());
    }

    private void deleteContact(ContactForm.DeleteEvent deleteEvent) {
        crmService.deleteContact(deleteEvent.getContact());
        Notification.show(deleteEvent.getContact().getFirstName()+" deleted successfully");
        updateList();
        closeForm();
    }

    // save contact
    private void saveContact(ContactForm.SaveEvent saveEvent) {
        crmService.saveContact(saveEvent.getContact());
        Notification.show(saveEvent.getContact().getFirstName()+" saved successfully");
        updateList();
        closeForm();

    }



    // toolBar is a component, and we will have to return one
    private Component getToolBar() {
        // add label to text input
        filterText.setPlaceholder("Filter by name...");
        // show clear button to clear existing text
        filterText.setClearButtonVisible(true);
        // so, every time we type a letter, we will update our search string.
        // but this may be costly if we have to access database on every keystroke
        // so we set the onValueChanged trigger to wait for user to pause typing for a while
        // so that database access happens only when user pauses typing.
        // values are LAZY, ON_CHANGE, EAGER, ON_BLUR, TIMEOUT
        // LAZY will prevent unnecessary data fetching
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        // hook valueChanged listener to update list
        filterText.addValueChangeListener(event -> updateList());
        // we also add necessary buttons
        // and we return the component
        return getHorizontalLayout();
    }

    private HorizontalLayout getHorizontalLayout() {
        // Add contact button
        Button addContact = new Button("Add Contact");
        // make button stand out
        addContact.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // add onClick listener to add a contact
        addContact.addClickListener(event -> addContact());
        // we set a layout for the two components
        // by default the components in a listView will be stacked vertically
        // if we want to set them align horizontally,
        // we create a HorizontalLayout and add the components to it.
        HorizontalLayout toolBar = new HorizontalLayout(filterText,addContact);
        // we set class name for css to the toolbar
        toolBar.setClassName("toolbar");
        return toolBar;
    }

    // adds contact to DB
    private void addContact() {
        // clear the form so that a new contact can be added
        grid.asSingleSelect().clear();
        // and open contact form with a new contact
        editContact(new Contact());
    }

    private void configureGrid() {
        // add a css class
        grid.addClassNames("contact-grid");
        // let's make it tak all the available space
        grid.setSizeFull();
        // let's define what columns to show
        // by default, it will just show all the available columns in the contact class
        // those columns will be shown as they are
        grid.setColumns("firstName", "lastName","email");
        // we can also define a custom column.
        // in this case, we have a company with multiple fields,
        // and we may need only the company name. so we add a column for that like so
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        // do the same for the status
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        // make all columns automatically resize
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        // item selection event for the grid
        // because we only want to select one item, we set asSingleSelect()
        // grid also supports asMultiSelect() for multiple selection
        grid.asSingleSelect().addValueChangeListener(
                event -> editContact(event.getValue())
        );
    }

    // opens contact form and enables adding a new contact to list(DB)
    private void editContact(Contact contact) {
        // make sure that contact is not null
        if(contact == null){ // contact is null
            // hide form
            closeForm();
            return;
        }
        // set form contact
        form.setContact(contact);
        // open form by setting it visible
        form.setVisible(true);
        // add css class name for styling
        // this will help in animation or other css tricks if we want
        addClassName("editing");

    }

    // hides and clears contact form
    private void closeForm() {
        // clear contact from contact form
        form.setContact(null);
        // hide contact form
        form.setVisible(false);
        // remove CSS class
        removeClassName("editing");
    }

}
