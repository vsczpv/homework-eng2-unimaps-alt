package br.univali.eng2.santissimo.unimaps_compose

import java.time.LocalTime
import kotlin.math.min

object ServiceControl {
	// TODO
	fun fetchServiceById(id: Int): Service {
		return Service(
			id         = id,
			name       = "Tentação do Mate",
			location   = "C2",
			complement = "110",
			openTime   = LocalTime.of(17, 0),
			closedTime = LocalTime.of(min(17 + id, 23), 30),
			peakTime   = LocalTime.of(23, 15),
			catergory  = Service.ServiceCatergory.Food,
			type       = Service.ServiceType.Pizzaplace,
			capacity   = 20,
			rating     = 8
		)
	}
}

class Service(
	val id: Int,
	val name: String,
	val location: String,
	val complement: String,
	val openTime: LocalTime,
	val closedTime: LocalTime,
	val peakTime: LocalTime,
	val status: ServiceStatus = if
	(
		openTime   >= LocalTime.now() &&
		closedTime > LocalTime.now()
	)
	ServiceStatus.Open else ServiceStatus.Closed,
	val catergory: ServiceCatergory,
	val type: ServiceType,
	val capacity: Int,
	val rating: Int
) {

	enum class ServiceStatus
	{
		Open,
		Closed
	}

	enum class ServiceCatergory
	{
		Food,
		Sanitary,
		Other
	}

	enum class ServiceType
	{
		Pizzaplace
	}

}