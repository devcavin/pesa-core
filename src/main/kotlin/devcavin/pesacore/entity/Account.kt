package devcavin.pesacore.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Digits
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "accounts")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    var accountNumber: String = "ACCOUNT_NUMBER-${UUID.randomUUID()}",

    var customerId: String = "CUSTOMER-${UUID.randomUUID()}",

    @Digits(integer = 15, fraction = 4)
    @Column(precision = 19, scale = 4)
    var balance: BigDecimal,

    var currency: String = "KES"
    ) {

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: Instant? = null

    @UpdateTimestamp
    var updatedAt: Instant? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString() =
        "Account(id=$id, accountNumber='$accountNumber', customerId=$customerId, balance=$balance, currency='$currency', " +
                "createdAt=$createdAt, updatedAt=$updatedAt)"
}