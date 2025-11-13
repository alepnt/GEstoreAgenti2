package com.example.client.controller;

import com.example.client.model.ContractModel;
import com.example.client.model.InvoiceModel;
import com.example.client.service.DataCacheService;
import com.example.client.service.NotificationService;
import com.example.common.dto.NotificationMessage;
import com.example.common.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Controller principale della dashboard JavaFX.
 */
public class MainViewController {

    private final DataCacheService dataCacheService = new DataCacheService();
    private final NotificationService notificationService = new NotificationService();
    private final ObservableList<InvoiceModel> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<ContractModel> contractItems = FXCollections.observableArrayList();
    private final Observer<NotificationMessage> notificationObserver = this::onNotification;

    @FXML
    private TableView<InvoiceModel> invoiceTable;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceIdColumn;
    @FXML
    private TableColumn<InvoiceModel, BigDecimal> invoiceAmountColumn;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceStatusColumn;

    @FXML
    private TableView<ContractModel> contractTable;
    @FXML
    private TableColumn<ContractModel, String> contractIdColumn;
    @FXML
    private TableColumn<ContractModel, String> contractAgentColumn;
    @FXML
    private TableColumn<ContractModel, String> contractDescriptionColumn;

    @FXML
    private Label notificationLabel;

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        invoiceAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        invoiceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contractAgentColumn.setCellValueFactory(new PropertyValueFactory<>("agentId"));
        contractDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        invoiceTable.setItems(invoiceItems);
        contractTable.setItems(contractItems);

        notificationService.subscribe(notificationObserver);
        refreshData();
    }

    @FXML
    public void refreshData() {
        invoiceItems.setAll(dataCacheService.getInvoices().stream().map(InvoiceModel::fromDto).toList());
        contractItems.setAll(dataCacheService.getContracts().stream().map(ContractModel::fromDto).toList());
        notificationService.publish(new NotificationMessage("refresh", "Dati aggiornati", Instant.now()));
    }

    private void onNotification(NotificationMessage notification) {
        if (notificationLabel != null) {
            notificationLabel.setText(notification.getPayload());
        }
    }

    public void shutdown() {
        notificationService.unsubscribe(notificationObserver);
    }
}
