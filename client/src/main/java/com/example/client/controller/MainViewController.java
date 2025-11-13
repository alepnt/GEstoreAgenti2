package com.example.client.controller;

import com.example.client.command.CommandMemento;
import com.example.client.model.ContractModel;
import com.example.client.model.DataChangeEvent;
import com.example.client.model.DataChangeType;
import com.example.client.model.DocumentHistoryModel;
import com.example.client.model.DocumentHistorySearchCriteria;
import com.example.client.model.InvoiceModel;
import com.example.client.service.DataCacheService;
import com.example.client.service.NotificationService;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.AgentCommissionDTO;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.MonthlyCommissionDTO;
import com.example.common.dto.TeamCommissionDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.dto.NotificationMessage;
import com.example.common.enums.ContractStatus;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.common.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller principale della dashboard JavaFX.
 */
public class MainViewController {

    private final DataCacheService dataCacheService = new DataCacheService();
    private final NotificationService notificationService = new NotificationService();
    private final ObservableList<InvoiceModel> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<ContractModel> contractItems = FXCollections.observableArrayList();
    private final ObservableList<DocumentHistoryModel> historyItems = FXCollections.observableArrayList();
    private final ObservableList<DocumentHistoryModel> historySearchItems = FXCollections.observableArrayList();
    private final Observer<NotificationMessage> notificationObserver = this::onNotification;
    private final Observer<CommandMemento> historyObserver = this::onCommandExecuted;
    private final Observer<DataChangeEvent> dataChangeObserver = this::onDataChanged;
    private final DateTimeFormatter historyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private DocumentType currentHistoryType;
    private Long currentHistoryId;
    private boolean updatingStatsYear;
    private int historyCurrentPage;
    private long historyTotalPages;
    private DocumentHistorySearchCriteria historyCurrentCriteria = new DocumentHistorySearchCriteria();

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
    private ComboBox<DocumentType> historyTypeCombo;
    @FXML
    private ComboBox<DocumentAction> historyActionCombo;
    @FXML
    private TextField historyDocumentIdField;
    @FXML
    private TextField historySearchField;
    @FXML
    private DatePicker historyFromDatePicker;
    @FXML
    private DatePicker historyToDatePicker;
    @FXML
    private ComboBox<Integer> historyPageSizeCombo;
    @FXML
    private TextField historyAgentIdField;
    @FXML
    private TableView<DocumentHistoryModel> historySearchTable;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchTypeColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchDocumentIdColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchActionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchDescriptionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchTimestampColumn;
    @FXML
    private Button historyPrevButton;
    @FXML
    private Button historyNextButton;
    @FXML
    private Label historyPageInfoLabel;
    @FXML
    private Label historyTotalLabel;

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
    private ComboBox<Integer> statsYearCombo;

    @FXML
    private LineChart<String, Number> commissionTrendChart;

    @FXML
    private BarChart<String, Number> agentCommissionBarChart;

    @FXML
    private PieChart teamCommissionPieChart;

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

        if (historyTypeCombo != null) {
            historyTypeCombo.getItems().setAll(DocumentType.values());
        }
        if (historyActionCombo != null) {
            historyActionCombo.getItems().setAll(DocumentAction.values());
        }
        if (historyPageSizeCombo != null) {
            historyPageSizeCombo.getItems().setAll(25, 50, 100);
            historyPageSizeCombo.setValue(25);
        }
        if (historySearchTable != null) {
            historySearchTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
            historySearchDocumentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
            historySearchActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
            historySearchDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            historySearchTimestampColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getCreatedAt())));
            historySearchTable.setItems(historySearchItems);
        }

        invoiceTable.setItems(invoiceItems);
        contractTable.setItems(contractItems);
        historyTable.setItems(historyItems);

        updateHistoryPagination(null);

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

        if (statsYearCombo != null) {
            statsYearCombo.setOnAction(event -> {
                if (!updatingStatsYear) {
                    refreshStatistics();
                }
            });
        }

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
        dataCacheService.subscribeDataChanges(dataChangeObserver);
        notificationService.subscribe(notificationObserver);

        refreshData();
    }

    @FXML
    public void refreshData() {
        refreshInvoices();
        refreshContracts();
        refreshStatistics();
        refreshHistorySearch(true);
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

    @FXML
    public void onApplyHistoryFilters() {
        refreshHistorySearch(true);
        notificationService.publish(new NotificationMessage("history", "Filtri applicati", Instant.now()));
    }

    @FXML
    public void onResetHistoryFilters() {
        if (historyTypeCombo != null) {
            historyTypeCombo.setValue(null);
        }
        if (historyActionCombo != null) {
            historyActionCombo.setValue(null);
        }
        if (historyDocumentIdField != null) {
            historyDocumentIdField.clear();
        }
        if (historySearchField != null) {
            historySearchField.clear();
        }
        if (historyFromDatePicker != null) {
            historyFromDatePicker.setValue(null);
        }
        if (historyToDatePicker != null) {
            historyToDatePicker.setValue(null);
        }
        if (historyAgentIdField != null) {
            historyAgentIdField.clear();
        }
        if (historyPageSizeCombo != null) {
            historyPageSizeCombo.setValue(25);
        }
        refreshHistorySearch(true);
        notificationService.publish(new NotificationMessage("history", "Filtri ripristinati", Instant.now()));
    }

    @FXML
    public void onHistoryPrevPage() {
        if (historyCurrentPage > 0) {
            historyCurrentPage--;
            refreshHistorySearch(false);
        }
    }

    @FXML
    public void onHistoryNextPage() {
        if (historyCurrentPage + 1 < historyTotalPages) {
            historyCurrentPage++;
            refreshHistorySearch(false);
        }
    }

    @FXML
    public void onHistoryExportCsv() {
        DocumentHistorySearchCriteria criteria = historyCurrentCriteria != null ? historyCurrentCriteria : buildHistoryCriteria();
        byte[] csv = dataCacheService.exportDocumentHistory(criteria);
        saveToFile(csv, "storico-documenti.csv", "csv", "File CSV");
    }

    @FXML
    public void onHistoryDownloadPdf() {
        Long agentId = parseLong(historyAgentIdField != null ? historyAgentIdField.getText() : null).orElse(null);
        LocalDate from = historyFromDatePicker != null ? historyFromDatePicker.getValue() : null;
        LocalDate to = historyToDatePicker != null ? historyToDatePicker.getValue() : null;
        byte[] pdf = dataCacheService.downloadClosedInvoiceReport(from, to, agentId);
        saveToFile(pdf, "report-fatture-chiuse.pdf", "pdf", "Documento PDF");
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

    private void refreshHistorySearch(boolean resetPage) {
        if (historySearchTable == null) {
            return;
        }
        if (resetPage) {
            historyCurrentPage = 0;
        }
        DocumentHistorySearchCriteria criteria = buildHistoryCriteria();
        historyCurrentCriteria = criteria;
        int size = historyPageSizeCombo != null && historyPageSizeCombo.getValue() != null ? historyPageSizeCombo.getValue() : 25;
        DocumentHistoryPageDTO page = dataCacheService.searchDocumentHistory(criteria, historyCurrentPage, size);
        if (page == null || page.getItems() == null) {
            historySearchItems.clear();
            updateHistoryPagination(null);
            return;
        }
        historyCurrentPage = page.getPage();
        historySearchItems.setAll(page.getItems().stream().map(DocumentHistoryModel::fromDto).toList());
        updateHistoryPagination(page);
    }

    private void refreshStatistics() {
        AgentStatisticsDTO agentStats = dataCacheService.getAgentStatistics(statsYearCombo != null ? statsYearCombo.getValue() : null);
        if (statsYearCombo != null) {
            updatingStatsYear = true;
            statsYearCombo.getItems().setAll(agentStats.years());
            statsYearCombo.setValue(agentStats.year());
            updatingStatsYear = false;
        }
        updateCommissionTrendChart(agentStats);
        updateAgentBarChart(agentStats);
        TeamStatisticsDTO teamStats = dataCacheService.getTeamStatistics(agentStats.year());
        updateTeamPieChart(teamStats);
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

    private void updateCommissionTrendChart(AgentStatisticsDTO statistics) {
        if (commissionTrendChart == null) {
            return;
        }
        commissionTrendChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Provvigioni");
        statistics.monthlyTotals().stream()
                .sorted((a, b) -> Integer.compare(a.month(), b.month()))
                .forEach(entry -> series.getData().add(new XYChart.Data<>(monthLabel(entry.month()), entry.commission().doubleValue())));
        commissionTrendChart.getData().add(series);
    }

    private void updateAgentBarChart(AgentStatisticsDTO statistics) {
        if (agentCommissionBarChart == null) {
            return;
        }
        agentCommissionBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Provvigioni");
        for (AgentCommissionDTO aggregate : statistics.agentTotals()) {
            String label = aggregate.teamName() != null && !aggregate.teamName().isBlank()
                    ? aggregate.agentName() + " (" + aggregate.teamName() + ")"
                    : aggregate.agentName();
            series.getData().add(new XYChart.Data<>(label, aggregate.commission().doubleValue()));
        }
        agentCommissionBarChart.getData().add(series);
    }

    private void updateTeamPieChart(TeamStatisticsDTO statistics) {
        if (teamCommissionPieChart == null) {
            return;
        }
        teamCommissionPieChart.setData(FXCollections.observableArrayList(statistics.teamTotals().stream()
                .map(team -> new PieChart.Data(team.teamName() != null && !team.teamName().isBlank() ? team.teamName() : "Senza team",
                        team.commission().doubleValue()))
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

    private void onDataChanged(DataChangeEvent event) {
        if (event == null) {
            return;
        }
        if (event.type() == DataChangeType.INVOICE) {
            refreshStatistics();
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
        dataCacheService.unsubscribeDataChanges(dataChangeObserver);
        dataCacheService.getCaretaker().unsubscribe(historyObserver);
    }

    private DocumentHistorySearchCriteria buildHistoryCriteria() {
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();
        if (historyTypeCombo != null) {
            criteria.setDocumentType(historyTypeCombo.getValue());
        }
        if (historyDocumentIdField != null) {
            criteria.setDocumentId(parseLong(historyDocumentIdField.getText()).orElse(null));
        }
        if (historyActionCombo != null && historyActionCombo.getValue() != null) {
            criteria.setActions(List.of(historyActionCombo.getValue()));
        } else {
            criteria.setActions(List.of());
        }
        criteria.setFrom(startOfDay(historyFromDatePicker != null ? historyFromDatePicker.getValue() : null));
        criteria.setTo(endOfDay(historyToDatePicker != null ? historyToDatePicker.getValue() : null));
        if (historySearchField != null) {
            String text = historySearchField.getText();
            criteria.setSearchText(text != null && !text.isBlank() ? text.trim() : null);
        }
        return criteria;
    }

    private void updateHistoryPagination(DocumentHistoryPageDTO page) {
        if (historyPageInfoLabel == null || historyTotalLabel == null) {
            return;
        }
        if (page == null) {
            historyTotalPages = 0;
            historyPageInfoLabel.setText("Pagina 0 di 0");
            historyTotalLabel.setText("0 risultati");
            if (historyPrevButton != null) {
                historyPrevButton.setDisable(true);
            }
            if (historyNextButton != null) {
                historyNextButton.setDisable(true);
            }
            return;
        }
        historyTotalPages = Math.max(page.getTotalPages(), 1);
        historyPageInfoLabel.setText(String.format("Pagina %d di %d", page.getPage() + 1, page.getTotalPages()));
        historyTotalLabel.setText(page.getTotalElements() + " risultati");
        if (historyPrevButton != null) {
            historyPrevButton.setDisable(!page.hasPrevious());
        }
        if (historyNextButton != null) {
            historyNextButton.setDisable(!page.hasNext());
        }
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

    private Instant startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }

    private Instant endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    private void saveToFile(byte[] data, String defaultFileName, String extension, String description) {
        if (data == null || data.length == 0) {
            notificationService.publish(new NotificationMessage("warn", "Nessun dato da esportare", Instant.now()));
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva " + description);
        chooser.setInitialFileName(defaultFileName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, "*." + extension));
        File target = chooser.showSaveDialog(mainTabPane != null && mainTabPane.getScene() != null ? mainTabPane.getScene().getWindow() : null);
        if (target != null) {
            try {
                Files.write(Path.of(target.toURI()), data);
                notificationService.publish(new NotificationMessage("export", "File salvato: " + target.getName(), Instant.now()));
            } catch (IOException ex) {
                notificationService.publish(new NotificationMessage("error", "Errore nel salvataggio del file", Instant.now()));
            }
        }
    }

    private String formatInstant(Instant instant) {
        return instant != null ? historyFormatter.format(instant) : "";
    }

    private String monthLabel(int month) {
        try {
            return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault());
        } catch (Exception ex) {
            return Integer.toString(month);
        }
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
