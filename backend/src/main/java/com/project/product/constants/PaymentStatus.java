package com.project.product.constants;

public enum PaymentStatus {
    PENDING,   // Ödeme beklemede
    UNPAID,    // Ödeme yapılmadı (tamamlanmamış siparişler için)
    PAID,      // Ödeme tamamlandı
    FAILED,    // Ödeme başarısız oldu
    REFUNDED,  // Ödeme iade edildi
    CANCELLED  // Ödeme iptal edildi
}
