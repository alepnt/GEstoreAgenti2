package com.example.client.service;

import com.example.client.command.CommandExecutor;
import com.example.client.command.CommandHistoryCaretaker;
import com.example.client.command.CreateArticleCommand;
import com.example.client.command.CreateContractCommand;
import com.example.client.command.CreateCustomerCommand;
import com.example.client.command.CreateInvoiceCommand;
import com.example.client.command.DeleteArticleCommand;
import com.example.client.command.DeleteContractCommand;
import com.example.client.command.DeleteCustomerCommand;
import com.example.client.command.DeleteInvoiceCommand;
import com.example.client.command.LoadArticlesCommand;
import com.example.client.command.LoadContractsCommand;
import com.example.client.command.LoadCustomersCommand;
import com.example.client.command.LoadInvoicesCommand;
import com.example.client.command.RegisterInvoicePaymentCommand;
import com.example.client.command.UpdateArticleCommand;
import com.example.client.command.UpdateContractCommand;
import com.example.client.command.UpdateCustomerCommand;
import com.example.client.command.UpdateInvoiceCommand;
import com.example.client.model.DataChangeEvent;
import com.example.client.model.DataChangeType;
import com.example.client.model.DocumentHistorySearchCriteria;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.ArticleDTO;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.CustomerDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.observer.NotificationCenter;
import com.example.common.observer.Observer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servizio client che orchestra le operazioni CRUD tramite Command pattern.
 */
public class DataCacheService {

    private final BackendGateway backendGateway;
    private final CommandHistoryCaretaker caretaker = new CommandHistoryCaretaker();
    private final NotificationCenter<DataChangeEvent> dataChangeCenter = new NotificationCenter<>();
    private final CommandExecutor executor;
    private final Map<Integer, AgentStatisticsDTO> agentStatsCache = new ConcurrentHashMap<>();
    private final Map<Integer, TeamStatisticsDTO> teamStatsCache = new ConcurrentHashMap<>();
    private final Map<String, DocumentHistoryPageDTO> historyCache = new ConcurrentHashMap<>();

    public DataCacheService() {
        this(new BackendGateway());
    }

    public DataCacheService(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;
        this.executor = new CommandExecutor(backendGateway, caretaker);
    }

    public List<InvoiceDTO> getInvoices() {
        return executor.execute(new LoadInvoicesCommand()).value();
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        InvoiceDTO result = executor.execute(new CreateInvoiceCommand(invoiceDTO)).value();
        invalidateStatistics();
        invalidateHistory();
        publishChange(DataChangeType.INVOICE);
        return result;
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        InvoiceDTO result = executor.execute(new UpdateInvoiceCommand(id, invoiceDTO)).value();
        invalidateStatistics();
        invalidateHistory();
        publishChange(DataChangeType.INVOICE);
        return result;
    }

    public void deleteInvoice(Long id) {
        executor.execute(new DeleteInvoiceCommand(id));
        invalidateStatistics();
        invalidateHistory();
        publishChange(DataChangeType.INVOICE);
    }

    public InvoiceDTO registerPayment(Long id, InvoicePaymentRequest paymentRequest) {
        InvoiceDTO result = executor.execute(new RegisterInvoicePaymentCommand(id, paymentRequest)).value();
        invalidateStatistics();
        invalidateHistory();
        publishChange(DataChangeType.INVOICE);
        return result;
    }

    public List<ContractDTO> getContracts() {
        return executor.execute(new LoadContractsCommand()).value();
    }

    public ContractDTO createContract(ContractDTO contractDTO) {
        ContractDTO result = executor.execute(new CreateContractCommand(contractDTO)).value();
        invalidateHistory();
        publishChange(DataChangeType.CONTRACT);
        return result;
    }

    public ContractDTO updateContract(Long id, ContractDTO contractDTO) {
        ContractDTO result = executor.execute(new UpdateContractCommand(id, contractDTO)).value();
        invalidateHistory();
        publishChange(DataChangeType.CONTRACT);
        return result;
    }

    public void deleteContract(Long id) {
        executor.execute(new DeleteContractCommand(id));
        invalidateHistory();
        publishChange(DataChangeType.CONTRACT);
    }

    public List<CustomerDTO> getCustomers() {
        return executor.execute(new LoadCustomersCommand()).value();
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        CustomerDTO result = executor.execute(new CreateCustomerCommand(customerDTO)).value();
        publishChange(DataChangeType.CUSTOMER);
        return result;
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        CustomerDTO result = executor.execute(new UpdateCustomerCommand(id, customerDTO)).value();
        publishChange(DataChangeType.CUSTOMER);
        return result;
    }

    public void deleteCustomer(Long id) {
        executor.execute(new DeleteCustomerCommand(id));
        publishChange(DataChangeType.CUSTOMER);
    }

    public List<ArticleDTO> getArticles() {
        return executor.execute(new LoadArticlesCommand()).value();
    }

    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        ArticleDTO result = executor.execute(new CreateArticleCommand(articleDTO)).value();
        publishChange(DataChangeType.ARTICLE);
        return result;
    }

    public ArticleDTO updateArticle(Long id, ArticleDTO articleDTO) {
        ArticleDTO result = executor.execute(new UpdateArticleCommand(id, articleDTO)).value();
        publishChange(DataChangeType.ARTICLE);
        return result;
    }

    public void deleteArticle(Long id) {
        executor.execute(new DeleteArticleCommand(id));
        publishChange(DataChangeType.ARTICLE);
    }

    public List<DocumentHistoryDTO> getInvoiceHistory(Long id) {
        return backendGateway.invoiceHistory(id);
    }

    public List<DocumentHistoryDTO> getContractHistory(Long id) {
        return backendGateway.contractHistory(id);
    }

    public CommandHistoryCaretaker getCaretaker() {
        return caretaker;
    }

    public AgentStatisticsDTO getAgentStatistics(Integer year) {
        if (year != null) {
            return agentStatsCache.computeIfAbsent(year, backendGateway::agentStatistics);
        }
        AgentStatisticsDTO statistics = backendGateway.agentStatistics(null);
        agentStatsCache.put(statistics.year(), statistics);
        return statistics;
    }

    public TeamStatisticsDTO getTeamStatistics(Integer year) {
        if (year != null) {
            return teamStatsCache.computeIfAbsent(year, backendGateway::teamStatistics);
        }
        TeamStatisticsDTO statistics = backendGateway.teamStatistics(null);
        teamStatsCache.put(statistics.year(), statistics);
        return statistics;
    }

    public void invalidateStatistics() {
        agentStatsCache.clear();
        teamStatsCache.clear();
    }

    public DocumentHistoryPageDTO searchDocumentHistory(DocumentHistorySearchCriteria criteria, int page, int size) {
        if (criteria == null) {
            return new DocumentHistoryPageDTO();
        }
        String key = criteria.cacheKey(page, size);
        return historyCache.computeIfAbsent(key,
                unused -> backendGateway.searchDocumentHistory(criteria.getDocumentType(),
                        criteria.getDocumentId(),
                        criteria.getActions(),
                        criteria.getFrom(),
                        criteria.getTo(),
                        criteria.getSearchText(),
                        page,
                        size));
    }

    public byte[] exportDocumentHistory(DocumentHistorySearchCriteria criteria) {
        if (criteria == null) {
            return new byte[0];
        }
        return backendGateway.exportDocumentHistory(criteria.getDocumentType(),
                criteria.getDocumentId(),
                criteria.getActions(),
                criteria.getFrom(),
                criteria.getTo(),
                criteria.getSearchText());
    }

    public byte[] downloadClosedInvoiceReport(java.time.LocalDate from, java.time.LocalDate to, Long agentId) {
        return backendGateway.downloadClosedInvoicesReport(from, to, agentId);
    }

    public void invalidateHistory() {
        historyCache.clear();
    }

    public void subscribeDataChanges(Observer<DataChangeEvent> observer) {
        dataChangeCenter.registerObserver(observer);
    }

    public void unsubscribeDataChanges(Observer<DataChangeEvent> observer) {
        dataChangeCenter.removeObserver(observer);
    }

    private void publishChange(DataChangeType type) {
        dataChangeCenter.notifyObservers(new DataChangeEvent(type, java.time.Instant.now()));
    }

}
