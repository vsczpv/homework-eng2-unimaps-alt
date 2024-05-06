package br.univali.eng2.santissimo.unimaps_compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme

class ServiceActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val serviceId = intent.extras!!.getInt("service")
		val service   = ServiceControl.fetchServiceById(serviceId)
		setContent {
			ServiceUI(this, service)
		}
	}
}

fun serviceCatergoryName(type: Service.ServiceCatergory): String {
	return when (type) {
		Service.ServiceCatergory.Food -> "Alimentação"
		else -> "Inválido"
	}
}

fun serviceTypeToName(type: Service.ServiceType): String {
	return when (type) {
		Service.ServiceType.Pizzaplace -> "Pizzaria"
	}
}

fun serviceItineraryToString(service: Service): String {
	return service.openTime.toString() + " até " + service.closedTime.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ServiceUI(atv: ServiceActivity = ServiceActivity(), service: Service = ServiceControl.fetchServiceById(0)) {
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
	UNIMAPSComposeTheme {
		// A surface container using the 'background' color from the theme
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			Scaffold (
				modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
				topBar = {
					LargeTopAppBar(
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor    = MaterialTheme.colorScheme.primaryContainer,
							titleContentColor = MaterialTheme.colorScheme.onPrimary
						),
						title = {
							Text(
								service.name,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis
							)
						},
						navigationIcon = {
							IconButton(onClick = {
								atv.finish()
							}) {
								Icon(
									imageVector        = Icons.Filled.ArrowBack,
									contentDescription = null
								)
							}
						},
						scrollBehavior = scrollBehavior
					)
				}
			) {paddingValues ->
				Column (
					modifier = Modifier
						.padding(paddingValues)
						.padding(horizontal = 16.dp)
						.padding(top = 16.dp)
				) {
					val entries = listOf(
						Pair("Categoria", serviceCatergoryName(service.catergory)),
						Pair("Tipo", serviceTypeToName(service.type)),
						Pair("Horário", serviceItineraryToString(service))
					)
					Widgets.ServiceInfoList(entries)
					Row (
						horizontalArrangement = Arrangement.SpaceBetween,
						modifier = Modifier
							.fillMaxWidth()
							.padding(end = 4.dp)
					) {
						Widgets.ServiceInfoBit(
							label = "Pico",
							info = service.peakTime.toString()
						)
						Widgets.ServiceInfoBit(
							label = "Capacidade",
							info = service.capacity.toString()
						)
					}

					OutlinedButton(
						onClick = { Toast.makeText(atv.baseContext,"TBI", Toast.LENGTH_SHORT).show() },
						modifier = Modifier
							.padding(top = 16.dp)
					) {
						Text(
							text = "Catálogo",
							style = MaterialTheme.typography.titleLarge,
							color = Color.Black,
							modifier = Modifier
								.padding(end = 4.dp)
						)
						Icon(
							imageVector = Icons.Filled.KeyboardArrowRight,
							contentDescription = null,
							tint = Color.Black,
							modifier = Modifier
								.size(24.dp)
						)
					}

					Text(
						text = "Local:",
						style = MaterialTheme.typography.titleLarge,
						modifier = Modifier
							.padding(top = 16.dp, start = 8.dp)
					)

					Text(
						text = "${service.location}: Próximo à sala ${service.complement}.",
						style = MaterialTheme.typography.titleMedium,
						modifier = Modifier
							.padding(start = 8.dp)
					)

					Widgets.MapCardButton(background = R.drawable.map_placeholder)

					OutlinedButton(
						onClick = { Toast.makeText(atv.baseContext,"TBI", Toast.LENGTH_SHORT).show() },
						modifier = Modifier
							.padding(top = 16.dp)
					) {
						Text(
							text = "Comentários",
							style = MaterialTheme.typography.titleLarge,
							color = Color.Black,
							modifier = Modifier
								.padding(end = 4.dp)
						)
						Icon(
							imageVector = Icons.Filled.KeyboardArrowRight,
							contentDescription = null,
							tint = Color.Black,
							modifier = Modifier
								.size(24.dp)
						)
					}
				}
			}
		}
	}
}