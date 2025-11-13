package com.example.client.controller;

import com.example.client.command.CommandMemento;
import com.example.client.model.ContractModel;
import com.example.client.model.DocumentHistoryModel;
import com.example.client.model.InvoiceModel;
import com.example.client.service.DataCacheService;
import com.example.client.service.NotificationService;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.NotificationMessage;
import com.example.common.enums.ContractStatus;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.common.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller principale della dashboard JavaFX.
 */
public class MainViewController {

    private final DataCacheService dataCacheService = new DataCacheService();
    private final NotificationService notificationService = new NotificationService();
    private final ObservableList<InvoiceModel> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<ContractModel> contractItems = FXCollections.observableArrayList();
    private final ObservableList<DocumentHistoryModel> historyItems = FXCollections.observableArrayList();
    private final Observer<NotificationMessage> notificationObserver = this::onNotification;
    private final Observer<CommandMemento> historyObserver = this::onCommandExecuted;
    private final DateTimeFormatter historyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private DocumentType currentHistoryType;
    private Long currentHistoryId;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private TextField invoiceNumberField;
    @FXML
    private TextField invoiceCustomerField;
    @FXML
    private TextField invoiceContractField;
    @FXML
    private TextField invoiceAmountField;
    @FXML
    private DatePicker invoiceIssueDatePicker;
    @FXML
    private DatePicker invoiceDueDatePicker;
    @FXML
    private ComboBox<InvoiceStatus> invoiceStatusCombo;
    @FXML
    private DatePicker invoicePaymentDatePicker;
    @FXML
    private TextField invoicePaymentAmountField;
    @FXML
    private TextArea invoiceNotesArea;
    @FXML
    private Button invoiceSaveButton;
    @FXML
    private Button invoiceUpdateButton;
    @FXML
    private Button invoiceDeleteButton;
    @FXML
    private Button invoicePayButton;

    @FXML
    private TableView<InvoiceModel> invoiceTable;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceNumberColumn;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceCustomerColumn;
    @FXML
    private TableColumn<InvoiceModel, BigDecimal> invoiceAmountColumn;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceStatusColumn;
    @FXML
    private TableColumn<InvoiceModel, LocalDate> invoiceDueDateColumn;

    @FXML
    private PieChart invoiceStatusChart;

    @FXML
    private TableView<DocumentHistoryModel> historyTable;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyActionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyDescriptionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyTimestampColumn;

    @FXML
    private TextField contractAgentField;
    @FXML
    private TextField contractCustomerField;
    @FXML
    private TextField contractDescriptionField;
    @FXML
    private TextField contractValueField;
    @FXML
    private ComboBox<ContractStatus> contractStatusCombo;
    @FXML
    private DatePicker contractStartPicker;
    @FXML
    private DatePicker contractEndPicker;
    @FXML
    private Button contractSaveButton;
    @FXML
    private Button contractUpdateButton;
    @FXML
    private Button contractDeleteButton;

    @FXML
    private TableView<ContractModel> contractTable;
    @FXML
    private TableColumn<ContractModel, String> contractCustomerColumn;
    @FXML
    private TableColumn<ContractModel, String> contractDescriptionColumn;
    @FXML
    private TableColumn<ContractModel, BigDecimal> contractValueColumn;
    @FXML
    private TableColumn<ContractModel, String> contractStatusColumn;
    @FXML
    private TableColumn<ContractModel, LocalDate> contractStartColumn;

    @FXML
    private PieChart contractStatusChart;

    @FXML
    private Label notificationLabel;

    @FXML
    public void initialize() {
        invoiceStatusCombo.getItems().setAll(InvoiceStatus.values());
        contractStatusCombo.getItems().setAll(ContractStatus.values());
        invoicePaymentAmountField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        invoiceAmountField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        contractValueField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));

        invoiceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        invoiceCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        invoiceAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        invoiceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        invoiceDueDateColumn.setCellValueFactory(param -> param.getValue().dueDateProperty());

        contractCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        contractDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contractValueColumn.setCellValueFactory(param -> param.getValue().totalValueProperty());
        contractStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractStartColumn.setCellValueFactory(param -> param.getValue().startDateProperty());

        historyActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        historyDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        historyTimestampColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getCreatedAt())));

        invoiceTable.setItems(invoiceItems);
        contractTable.setItems(contractItems);
        historyTable.setItems(historyItems);

        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                populateInvoiceForm(selected);
                loadHistory(DocumentType.INVOICE, selected.getId());
            }
        });

        contractTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                populateContractForm(selected);
                loadHistory(DocumentType.CONTRACT, selected.getId());
            }
        });

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && Objects.equals(newTab.getText(), "Contratti")) {
                ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadHistory(DocumentType.CONTRACT, selected.getId());
                } else {
                    historyItems.clear();
                }
            } else {
                InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadHistory(DocumentType.INVOICE, selected.getId());
                } else {
                    historyItems.clear();
                }
            }
        });

        dataCacheService.getCaretaker().subscribe(historyObserver);
        notificationService.subscribe(notificationObserver);

        refreshData();
    }

    @FXML
    public void refreshData() {
        refreshInvoices();
        refreshContracts();
        notificationService.publish(new NotificationMessage("refresh", "Dati aggiornati", Instant.now()));
    }

    @FXML
    public void onCreateInvoice() {
        try {
            InvoiceDTO dto = buildInvoiceFromForm(null);
            InvoiceDTO created = dataCacheService.createInvoice(dto);
            refreshInvoices();
            selectInvoice(created.getId());
            notificationService.publish(new NotificationMessage("invoice", "Fattura creata", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notificationService.publish(new NotificationMessage("error", ex.getMessage(), Instant.now()));
        }
    }

    @FXML
    public void onUpdateInvoice() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura", Instant.now()));
            return;
        }
        try {
            InvoiceDTO dto = buildInvoiceFromForm(selected.getId());
            dataCacheService.updateInvoice(selected.getId(), dto);
            refreshInvoices();
            selectInvoice(selected.getId());
            notificationService.publish(new NotificationMessage("invoice", "Fattura aggiornata", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notificationService.publish(new NotificationMessage("error", ex.getMessage(), Instant.now()));
        }
    }

    @FXML
    public void onDeleteInvoice() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura da eliminare", Instant.now()));
            return;
        }
        dataCacheService.deleteInvoice(selected.getId());
        refreshInvoices();
        historyItems.clear();
        notificationService.publish(new NotificationMessage("invoice", "Fattura eliminata", Instant.now()));
    }

    @FXML
    public void onRegisterPayment() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura", Instant.now()));
            return;
        }
        BigDecimal amountPaid = parseBigDecimal(invoicePaymentAmountField.getText()).orElse(selected.getAmount());
        LocalDate paymentDate = invoicePaymentDatePicker.getValue();
        InvoicePaymentRequest request = new InvoicePaymentRequest(paymentDate, amountPaid);
        dataCacheService.registerPayment(selected.getId(), request);
        refreshInvoices();
        selectInvoice(selected.getId());
        notificationService.publish(new NotificationMessage("invoice", "Pagamento registrato", Instant.now()));
    }

    @FXML
    public void onCreateContract() {
        try {
            ContractDTO dto = buildContractFromForm(null);
            ContractDTO created = dataCacheService.createContract(dto);
            refreshContracts();
            selectContract(created.getId());
            notificationService.publish(new NotificationMessage("contract", "Contratto creato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notificationService.publish(new NotificationMessage("error", ex.getMessage(), Instant.now()));
        }
    }

    @FXML
    public void onUpdateContract() {
        ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un contratto", Instant.now()));
            return;
        }
        try {
            ContractDTO dto = buildContractFromForm(selected.getId());
            dataCacheService.updateContract(selected.getId(), dto);
            refreshContracts();
            selectContract(selected.getId());
            notificationService.publish(new NotificationMessage("contract", "Contratto aggiornato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notificationService.publish(new NotificationMessage("error", ex.getMessage(), Instant.now()));
        }
    }

    @FXML
    public void onDeleteContract() {
        ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un contratto da eliminare", Instant.now()));
            return;
        }
        dataCacheService.deleteContract(selected.getId());
        refreshContracts();
        historyItems.clear();
        notificationService.publish(new NotificationMessage("contract", "Contratto eliminato", Instant.now()));
    }

    private void refreshInvoices() {
        List<InvoiceDTO> dtos = dataCacheService.getInvoices();
        invoiceItems.setAll(dtos.stream().map(InvoiceModel::fromDto).toList());
        updateInvoiceChart();
    }

    private void refreshContracts() {
        List<ContractDTO> dtos = dataCacheService.getContracts();
        contractItems.setAll(dtos.stream().map(ContractModel::fromDto).toList());
        updateContractChart();
    }

    private void updateInvoiceChart() {
        Map<String, Long> counts = invoiceItems.stream()
                .collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getStatus()).orElse("N/D"), Collectors.counting()));
        invoiceStatusChart.setData(FXCollections.observableArrayList(counts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toList()));
    }

    private void updateContractChart() {
        Map<String, Long> counts = contractItems.stream()
                .collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getStatus()).orElse("N/D"), Collectors.counting()));
        contractStatusChart.setData(FXCollections.observableArrayList(counts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toList()));
    }

    private InvoiceDTO buildInvoiceFromForm(Long id) {
        String customer = invoiceCustomerField.getText();
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Il cliente è obbligatorio");
        }
        BigDecimal amount = parseBigDecimal(invoiceAmountField.getText())
                .orElseThrow(() -> new IllegalArgumentException("Importo non valido"));
        LocalDate issueDate = invoiceIssueDatePicker.getValue();
        if (issueDate == null) {
            throw new IllegalArgumentException("La data di emissione è obbligatoria");
        }
        Long contractId = parseLong(invoiceContractField.getText()).orElse(null);
        InvoiceStatus status = invoiceStatusCombo.getValue() != null ? invoiceStatusCombo.getValue() : InvoiceStatus.DRAFT;
        return new InvoiceDTO(id,
                invoiceNumberField.getText(),
                contractId,
                customer,
                amount,
                issueDate,
                invoiceDueDatePicker.getValue(),
                status,
                invoicePaymentDatePicker.getValue(),
                invoiceNotesArea.getText());
    }

    private ContractDTO buildContractFromForm(Long id) {
        Long agentId = parseLong(contractAgentField.getText())
                .orElseThrow(() -> new IllegalArgumentException("Agente non valido"));
        String customer = contractCustomerField.getText();
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Il cliente è obbligatorio");
        }
        BigDecimal value = parseBigDecimal(contractValueField.getText())
                .orElseThrow(() -> new IllegalArgumentException("Valore non valido"));
        LocalDate start = contractStartPicker.getValue();
        if (start == null) {
            throw new IllegalArgumentException("La data di inizio è obbligatoria");
        }
        return new ContractDTO(id,
                agentId,
                customer,
                contractDescriptionField.getText(),
                start,
                contractEndPicker.getValue(),
                value,
                contractStatusCombo.getValue() != null ? contractStatusCombo.getValue() : ContractStatus.DRAFT);
    }

    private void populateInvoiceForm(InvoiceModel model) {
        invoiceNumberField.setText(model.getNumber());
        invoiceCustomerField.setText(model.getCustomerName());
        invoiceContractField.setText(model.getContractId() != null ? model.getContractId().toString() : "");
        invoiceAmountField.setText(model.getAmount() != null ? model.getAmount().toPlainString() : "");
        invoiceIssueDatePicker.setValue(model.getIssueDate());
        invoiceDueDatePicker.setValue(model.getDueDate());
        invoiceStatusCombo.setValue(model.getStatus() != null ? InvoiceStatus.valueOf(model.getStatus()) : null);
        invoicePaymentDatePicker.setValue(model.getPaymentDate());
        invoicePaymentAmountField.setText(model.getAmount() != null ? model.getAmount().toPlainString() : "");
        invoiceNotesArea.setText(model.getNotes());
    }

    private void populateContractForm(ContractModel model) {
        contractAgentField.setText(model.getAgentId() != null ? model.getAgentId().toString() : "");
        contractCustomerField.setText(model.getCustomerName());
        contractDescriptionField.setText(model.getDescription());
        contractValueField.setText(model.getTotalValue() != null ? model.getTotalValue().toPlainString() : "");
        contractStatusCombo.setValue(model.getStatus() != null ? ContractStatus.valueOf(model.getStatus()) : null);
        contractStartPicker.setValue(model.getStartDate());
        contractEndPicker.setValue(model.getEndDate());
    }

    private void loadHistory(DocumentType type, Long documentId) {
        if (documentId == null) {
            historyItems.clear();
            return;
        }
        currentHistoryType = type;
        currentHistoryId = documentId;
        List<DocumentHistoryDTO> entries = switch (type) {
            case INVOICE -> dataCacheService.getInvoiceHistory(documentId);
            case CONTRACT -> dataCacheService.getContractHistory(documentId);
        };
        historyItems.setAll(entries.stream().map(DocumentHistoryModel::fromDto).toList());
    }

    private void selectInvoice(Long id) {
        if (id == null) {
            return;
        }
        invoiceTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> invoiceTable.getSelectionModel().select(item));
    }

    private void selectContract(Long id) {
        if (id == null) {
            return;
        }
        contractTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> contractTable.getSelectionModel().select(item));
    }

    private void onNotification(NotificationMessage notification) {
        if (notificationLabel != null) {
            notificationLabel.setText(notification.getPayload());
        }
    }

    private void onCommandExecuted(CommandMemento memento) {
        if (memento == null || memento.getResult() == null || memento.getResult().historySnapshot().isEmpty()) {
            return;
        }
        if (Objects.equals(currentHistoryType, memento.getResult().documentType())
                && Objects.equals(currentHistoryId, memento.getResult().documentId())) {
            historyItems.setAll(memento.getResult().historySnapshot().stream()
                    .map(DocumentHistoryModel::fromDto)
                    .toList());
        }
    }

    public void shutdown() {
        notificationService.unsubscribe(notificationObserver);
        dataCacheService.getCaretaker().unsubscribe(historyObserver);
    }

    private Optional<BigDecimal> parseBigDecimal(String value) {
        try {
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(new BigDecimal(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<Long> parseLong(String value) {
        try {
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private String formatInstant(Instant instant) {
        return instant != null ? historyFormatter.format(instant) : "";
    }

    private static class BigDecimalStringConverter extends StringConverter<BigDecimal> {

        @Override
        public String toString(BigDecimal object) {
            return object == null ? "" : object.toPlainString();
        }

        @Override
        public BigDecimal fromString(String string) {
            if (string == null || string.isBlank()) {
                return null;
            }
            try {
                return new BigDecimal(string);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
