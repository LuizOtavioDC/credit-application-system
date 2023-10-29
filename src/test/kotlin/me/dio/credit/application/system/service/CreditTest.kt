package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockExtension::class)
calss creditTest{
  @MockK
  laneinit var client : CustomerService
  @MockK
  laneinit var creditRepository : CreditRepository
  @InjectMockks
  lateinit var creditService: CreditService

  @Test
  fun createCredit(){
    val: idClient = 1L
    val credit: Credit = buildCredit()
    val newCredit: Credit
    every { customerService.findById(idClient) } returns credit.customer
    every { creditRepository.save(credit) } returns credit
    newCredit = this.creditService.save(credit)
  }

  @Test
  fun invalidDay(){
    val dayInvalid: LocalDate = LocalDate.now().plusMonths(5)
    val credit: Credit = buildCredit(dayFirstInstallment = dayInvalid)
    every { creditRepository.save(credit) } answers { credit }
    Assertions.assertThatThrownBy { creditService.save(credit) }
    .isInstanceOf (BusinessException::class.java).hasMessage("Day is invalid")
    verify(exactly = 0) { creditRepository.save(any()) }
  }

  @Test
  fun returnListCredit() {
    val: idClient = 1L
    val listCredits: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
    every { creditRepository.findAllByCustomerId(idClient) } returns elistCredits
    val newListCredit: List<Credit> = creditService.findAllByCustomer(idClient)
    Assertions.assertThat(newListCredit).isNotNull
    Assertions.assertThat(newListCredit).isNotEmpty
    Assertions.assertThat(newListCredit).isSameAs(listCredits)
    verify(exactly = 1) { creditRepository.findAllByCustomerId(idClient) }
  }

  @Test
  fun validclient() {
    val: idClient = 1L
    val creditNumber: UUID = UUID.randomUUID()
    val credit: Credit = buildCredit(customer = Customer(id = idClient))
    every { creditRepository.findByCreditCode(creditNumber) } returns credit
    val actual: Credit = creditService.findByCreditCode(idClient, creditNumber)
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isSameAs(credit)
    verify(exactly = 1) { creditRepository.findByCreditCode(creditNumber) }
  }

  @Test
  fun errorFalseId() {
    val: idClient = 1L
    val creditNumber: UUID = UUID.randomUUID()
    val credit: Credit = buildCredit(customer = Customer(id = 2L))
    every { creditRepository.findByCreditCode(creditNumber) } returns credit
    Assertions.assertThatThrownBy { creditService.findByCreditCode(customerId, creditNumber) }
    .isInstanceOf(IllegalArgumentException::class.java).hasMessage("Id invalid")
    verify { creditRepository.findByCreditCode(creditNumber) }
  }

  private fun buildCustomer(Nome: String = "Luiz Otavio", Sobrenome: String = "Curtolo", Cpf: String = "99999999999", Email: String = "luiz@gmail.com", Senha: String = "abc123", Cep: String = "12345678", Rua: String = "Rua1", valor: BigDecimal = BigDecimal.valueOf(5000.0)
  ) = Customer(firstName = Nome, lastName = Sobrenome, cpf = Cpf, email = Email, password = Senha, address = Address( zipCode = Cep, street = Rua), income = Valor, id = 1L)

  private fun buildCredit( codigoDoCredito: UUID = UUID.randomUUID(), valorDeCredito: BigDecimal = BigDecimal(5000.0), PrimeiraParcela: LocalDate = LocalDate.now().plusDays(3), quantidadeDeParcelas: Int = 10, status: Status = Status.IN_PROGRESS, cliente: Customer = buildCustomer(), id: Long = 1L
  ) = Credit( creditCode = codigoDoCredito, creditValue = valorDeCredito, dayFirstInstallment = PrimeiraParcela, numberOfInstallments = quantidadeDeParcelas, status = status, customer = cliente, id = id)
}    