package devcavin.pesacore.service

import devcavin.pesacore.dto.request.TransactionRequest
import devcavin.pesacore.dto.request.TransferRequest
import devcavin.pesacore.dto.response.TransactionResponse
import devcavin.pesacore.dto.response.TransferResponse
import devcavin.pesacore.dto.response.toAccountResponse
import devcavin.pesacore.dto.response.toRecipientResponse
import devcavin.pesacore.dto.response.toTransactionResponse
import devcavin.pesacore.entity.Transaction
import devcavin.pesacore.entity.TransactionStatus
import devcavin.pesacore.entity.TransactionType
import devcavin.pesacore.exception.InsufficientFundsException
import devcavin.pesacore.exception.InvalidAmountException
import devcavin.pesacore.exception.ResourceNotFoundException
import devcavin.pesacore.repository.AccountRepository
import devcavin.pesacore.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    @Transactional(noRollbackFor = [InvalidAmountException::class])
    fun deposit(accountId: UUID, request: TransactionRequest): TransactionResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { ResourceNotFoundException("Account not found") }

        val amount = request.amount ?: throw InvalidAmountException("Amount is required")

        if (amount <= BigDecimal.ZERO) {
            val failedDeposit = Transaction(
                account = account,
                type = TransactionType.DEPOSIT,
                status = TransactionStatus.FAILED,
                amount = amount,
                reason = "Amount is less than or equal to zero"
            )
            transactionRepository.save(failedDeposit)
            throw InvalidAmountException("Amount must be greater than zero")
        }

        val currentBalance = account.balance ?: BigDecimal.ZERO
        account.balance = currentBalance.add(amount)

        accountRepository.save(account)

        val transaction = Transaction(
            account = account,
            type = TransactionType.DEPOSIT,
            status = TransactionStatus.SUCCESS,
            amount = amount,
            reason = "Deposit transaction"
        )

        return transactionRepository.save(transaction).toTransactionResponse()
    }

    @Transactional(noRollbackFor = [InsufficientFundsException::class, InvalidAmountException::class])
    fun withdraw(accountId: UUID, request: TransactionRequest): TransactionResponse {
        val account = accountRepository.findById(accountId)
            .orElseThrow { ResourceNotFoundException("Account not found") }
        
        val amount = request.amount ?: throw InvalidAmountException("Amount is required")

        if (amount <= BigDecimal.ZERO) {
            val failedWithdrawal = Transaction(
                account = account,
                type = TransactionType.WITHDRAW,
                status = TransactionStatus.FAILED,
                amount = amount,
                reason = "Amount must be positive"
            )
            transactionRepository.save(failedWithdrawal)
            throw InvalidAmountException("Amount must be greater than zero")
        }

        val currentBalance = account.balance ?: BigDecimal.ZERO

        if (currentBalance < amount) {
            val failedWithdrawal = Transaction(
                account = account,
                type = TransactionType.WITHDRAW,
                status = TransactionStatus.FAILED,
                amount = amount,
                reason = "Insufficient funds"
            )
            transactionRepository.save(failedWithdrawal)
            throw InsufficientFundsException("Insufficient funds to complete transaction")
        }

        account.balance = currentBalance.subtract(amount)
        accountRepository.save(account)

        val transaction = Transaction(
            account = account,
            type = TransactionType.WITHDRAW,
            status = TransactionStatus.SUCCESS,
            amount = amount,
            reason = "Withdrawal transaction"
        )

        return transactionRepository.save(transaction).toTransactionResponse()
    }

    @Transactional(noRollbackFor = [InvalidAmountException::class, InsufficientFundsException::class])
    fun transfer(senderAccountId: UUID, request: TransferRequest): TransferResponse {
        if (senderAccountId == request.recipientAccountId) {
            throw InvalidAmountException("Cannot transfer money to the same account")
        }

        val sender = accountRepository.findById(senderAccountId)
            .orElseThrow { ResourceNotFoundException("Sender account not found") }

        val recipient = accountRepository.findById(request.recipientAccountId)
            .orElseThrow { ResourceNotFoundException("Receiver account not found") }

        val senderBalance = sender.balance ?: BigDecimal.ZERO
        val amount = request.amount!!

        if (senderBalance < amount) {
            val failedTransfer = Transaction(
                account = sender,
                type = TransactionType.TRANSFER,
                status = TransactionStatus.FAILED,
                amount = amount,
                reason = "Insufficient funds to complete transaction"
            )
            transactionRepository.save(failedTransfer)

            throw InsufficientFundsException("Insufficient funds to transfer")
        }

        sender.balance = senderBalance.subtract(amount)
        recipient.balance = recipient.balance?.add(amount)

        val senderRecord = Transaction(
            account = sender,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.SUCCESS,
            amount = amount.negate(),
            reason = "Transferred $amount to ${recipient.accountNumber}"
        )

        val recipientRecord = Transaction(
            account = recipient,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.SUCCESS,
            amount = amount,
            reason = "Received $amount from ${sender.accountNumber}"
        )

        transactionRepository.save(senderRecord)
        transactionRepository.save(recipientRecord)

        return TransferResponse(
            transactionId = senderRecord.id!!,
            status = senderRecord.status,
            amount = amount,
            currency = sender.currency,
            timestamp = senderRecord.createdAt ?: Instant.now(),
            sender = sender.toAccountResponse(),
            receiver = recipient.toRecipientResponse()
        )
    }
}