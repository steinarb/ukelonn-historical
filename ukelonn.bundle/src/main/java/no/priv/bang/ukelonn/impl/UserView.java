package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.getJobTypesFromTransactionTypes;
import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.getTransactionTypesFromUkelonnDatabase;
import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.registerNewJobInDatabase;

import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;

public class UserView extends VerticalLayout implements View {
    private static final long serialVersionUID = 3003586932131097778L;

    public UserView(Account account) {
        // Display the greeting
        Component greeting = new Label("Hei " + account.getFirstName());
        greeting.setStyleName("h1");
        addComponent(greeting);

        FormLayout balanceAndNewJob = new FormLayout();
        // Display the current balance
        ObjectProperty<Double> balance = new ObjectProperty<Double>(account.getBalance());
        TextField balanceDisplay = new TextField("Til gode:");
        balanceDisplay.setPropertyDataSource(balance);
        balanceDisplay.addStyleName("inline-label");
        balanceAndNewJob.addComponent(balanceDisplay);

        Map<Integer, TransactionType> transactionTypes = getTransactionTypesFromUkelonnDatabase(getClass());
        List<TransactionType> paymentTypes = getJobTypesFromTransactionTypes(transactionTypes.values());
        BeanItemContainer<TransactionType> paymentTypesContainer = new BeanItemContainer<TransactionType>(TransactionType.class, paymentTypes);
        ComboBox jobtypeSelector = new ComboBox("Velg jobb", paymentTypesContainer);
        jobtypeSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        jobtypeSelector.setItemCaptionPropertyId("transactionTypeName");
        jobtypeSelector.setNullSelectionAllowed(true);
        balanceAndNewJob.addComponent(jobtypeSelector);
        ObjectProperty<Double> newJobAmount = new ObjectProperty<Double>(0.0);
        TextField newAmountDisplay = new TextField(newJobAmount);
        newAmountDisplay.setReadOnly(true);
        balanceAndNewJob.addComponent(newAmountDisplay);
        jobtypeSelector.addValueChangeListener(new ValueChangeListener() {
                private static final long serialVersionUID = 3145027593224884343L;
                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (jobtypeSelector.getValue() == null) {
                        newJobAmount.setValue(0.0);
                    } else {
                        newJobAmount.setValue(((TransactionType) jobtypeSelector.getValue()).getTransactionAmount());
                    }
                }
            });

        // Have a clickable button
        balanceAndNewJob.addComponent(new Button("Registrer jobb",
                                                 new Button.ClickListener() {
                                                     private static final long serialVersionUID = 2723190031041985566L;

                                                     @Override
                                                     public void buttonClick(ClickEvent e) {
                                                         TransactionType jobType = (TransactionType) jobtypeSelector.getValue();
                                                         if (jobType != null) {
                                                             registerNewJobInDatabase(getClass(), account, jobType.getId(), jobType.getTransactionAmount());
                                                             balance.setValue(account.getBalance());
                                                             jobtypeSelector.setValue(null);
                                                         }
                                                     }
                                                 }));
        addComponent(balanceAndNewJob);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
