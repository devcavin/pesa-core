package devcavin.pesacore.dto.response

import devcavin.pesacore.entity.Account
import devcavin.pesacore.entity.TransactionStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransferResponse(
    val transactionId: UUID,
    val status: TransactionStatus,
    val amount: BigDecimal,
    val currency: String,
    val timestamp: Instant,
    val sender: AccountResponse,
    val receiver: RecipientResponse
)

data class RecipientResponse(
    val accountId: UUID?,
    val accountNumber: String,
    val ownerId: String
)

fun Account.toRecipientResponse(): RecipientResponse {
    return RecipientResponse(
        accountId = this.id,
        accountNumber = this.accountNumber,
        ownerId = this.ownerId
    )
}
