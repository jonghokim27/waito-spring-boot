package io.cleralabs.waito.core.transaction

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

fun afterCommit(action: () -> Unit) {
    if (!TransactionSynchronizationManager.isActualTransactionActive()) {
        action()
        return
    }

    TransactionSynchronizationManager.registerSynchronization(
        object : TransactionSynchronization {
            override fun afterCommit() {
                action()
            }
        },
    )
}
