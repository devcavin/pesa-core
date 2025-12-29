package devcavin.pesacore.controller

import devcavin.pesacore.dto.request.CreateAccountRequest
import devcavin.pesacore.dto.request.TransactionRequest
import devcavin.pesacore.dto.request.TransferRequest
import devcavin.pesacore.dto.response.AccountResponse
import devcavin.pesacore.dto.response.TransactionResponse
import devcavin.pesacore.dto.response.TransferResponse
import devcavin.pesacore.service.AccountService
import devcavin.pesacore.service.TransactionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val accountService: AccountService,
    private val transactionService: TransactionService
) {
    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest): ResponseEntity<AccountResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request))
    }

    @GetMapping("/all")
    fun allAccounts(): ResponseEntity<List<AccountResponse>> {
        return ResponseEntity(
            accountService.allAccounts(),
            HttpStatus.OK
        )
    }

    @GetMapping("/{id}")
    fun accountById(@PathVariable id: UUID): ResponseEntity<AccountResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(accountService.accountById(id))
    }

    @PostMapping("/{accountId}/transactions/deposit")
    fun deposit(@PathVariable accountId: UUID, @Valid @RequestBody request: TransactionRequest):
            ResponseEntity<TransactionResponse> {
        return ResponseEntity(
            transactionService.deposit(accountId, request),
            HttpStatus.OK
        )
    }

    @PostMapping("/{accountId}/transactions/withdraw")
    fun withdraw(@PathVariable accountId: UUID, @Valid @RequestBody request: TransactionRequest):
            ResponseEntity<TransactionResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.withdraw(accountId, request))
    }

    @PostMapping("/{senderAccountId}/transactions/transfer")
    fun transfer(@PathVariable senderAccountId: UUID, @Valid @RequestBody request: TransferRequest):
            ResponseEntity<TransferResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.transfer(senderAccountId, request))
    }
}