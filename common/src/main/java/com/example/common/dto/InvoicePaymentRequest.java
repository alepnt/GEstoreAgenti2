package com.example.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload per registrare un pagamento di fattura.
 */
public class InvoicePaymentRequest {

    private LocalDate paymentDate;
    private BigDecimal amountPaid;

    public InvoicePaymentRequest() {
    }

    public InvoicePaymentRequest(LocalDate paymentDate, BigDecimal amountPaid) {
        this.paymentDate = paymentDate;
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
}
