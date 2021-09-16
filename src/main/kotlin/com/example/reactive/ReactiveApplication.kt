package com.example.reactive

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.logging.LogFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration.ofSeconds
import kotlin.random.Random

@SpringBootApplication
class ReactiveApplication

fun main(args: Array<String>) {
	runApplication<ReactiveApplication>(*args)
}

@Controller
class CarRegoController(private val carRegoService: CarRegoService) {
	@MessageMapping("cars")
	fun cars() = carRegoService.streamOfCars()
}

@Component
class CargoRunner(private val carRegoService: CarRegoService) : ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		carRegoService.streamOfCars().log().subscribe()
	}

}

@Service
class CarRegoService() {
	private val log = LogFactory.getLog(javaClass)

	fun streamOfCars(): Flux<Car> {
		return Flux.interval(ofSeconds(3))
			.map{ Car(
				RandomStringUtils.randomAlphabetic(6).uppercase(),
				"image" + Random.nextInt(1, 5) + ".png")}
			.doOnSubscribe { log.info("New Subscription")}
			.share()
	}
}

data class Car(val rego: String, val image: String)
