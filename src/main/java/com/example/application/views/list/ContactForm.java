package com.example.application.views.list;

import com.example.application.data.Company;
import com.example.application.data.Contact;
import com.example.application.data.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ContactForm extends FormLayout {


    // binder object to help us validate form fields with Contact data
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);
    private Contact contact;
    // IMPORTANT!!!
    // the variable names in this form need to be the same as the column names of Contact DB table
    // see @ Contact class
    // so we can be able to use the Vaadin binder, to bind the form to database
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("E-Mail");
    // dropdown select list for companies and status
    ComboBox<Status> status = new ComboBox<>("Status");
    ComboBox<Company> company = new ComboBox<>("Company");

    // buttons
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");

    // constructor
    public ContactForm(List<Company> companies, List<Status> statuses) {
        // let's add css class name to the form too
        addClassName("contact-form");

        // hook binder to help us populate form data from selection
        // the reason we named form fields the same as database columns is
        // so that we can easily bind them here
        binder.bindInstanceFields(this);
        // since company and status are dropdown lists
        // we add items to them
        company.setItems(companies);
        status.setItems(statuses);
        // Company and Status are objects
        // since the dropdown list has to display Strings,
        // we tell it what to take from these objects
        company.setItemLabelGenerator(Company::getName);
        status.setItemLabelGenerator(Status::getName);
        // and we add the components to the layout
        add(
                firstName,
                lastName,
                email,
                company,
                status,
                // and the add button
                creatAddButton()
        );
    }


    // set contact to an object variable
    public void setContact(Contact contact) {
        this.contact = contact;
        binder.readBean(contact);
    }

    private Component creatAddButton() {

        // we configure the buttons
        // make them appear different
        // save is primary
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // delete should stand out (red)
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // add clickEvents to buttons
        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, contact)));
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this)));

        // set onClick listeners to buttons
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        // and we return new horizontal layout
        return new HorizontalLayout(save,delete,cancel);

    }

    private void validateAndSave() {
        try{
            binder.writeBean(contact);
            // contact saved
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) { // contact was not saved
            Notification.show("Contact could not be saved : reason = " + e.getMessage());
        }
    }

    // events of contact form, base class
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {

        private final Contact contact;


        public ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    // save event
    public static class SaveEvent extends ContactFormEvent {
        public SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    // delete
    public static class DeleteEvent extends ContactFormEvent {
        public DeleteEvent(ContactForm source,Contact contact) {
            super(source, contact);
        }
    }

    // cancel event
    public static class CancelEvent extends ContactFormEvent {

        // cancel event will just be used to close contact form with no contact editing happening
        // so we don't need contact object
        public CancelEvent(ContactForm source) {
            super(source, null);
        }
    }


    // contact event form setter (registering observers)
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
