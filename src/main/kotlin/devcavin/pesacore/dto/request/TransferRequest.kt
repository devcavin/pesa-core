package devcavin.pesacore.dto.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class TransferRequest(
    @field:NotNull(message = "Target account ID is required")
    var recipientAccountId: UUID,

    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be positive")
    var amount: BigDecimal? = null
)
