package br.univali.eng2.santissimo.unimaps_compose

import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.univali.eng2.santissimo.unimaps_compose.Widgets.Companion.ItemCard
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme
import java.time.LocalTime

class CatalogActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val service = ServiceControl.fetchServiceById(intent.extras!!.getInt("service"))!!
		setContent {
			CatalogUI(this, service)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CatalogUI(atv: CatalogActivity = CatalogActivity(), service: Service = Service(9001, "TestPlace", "C1", "101", LocalTime.parse("01:00:00"), LocalTime.parse("23:00:00"), LocalTime.parse("12:00:00"), Service.ServiceStatus.Open, Service.ServiceCatergory.Food, Service.ServiceType.Pizzaplace, 32, 10 , 0)) {

	val items = remember { service.items.items }

	UNIMAPSComposeTheme {
		// A surface container using the 'background' color from the theme
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			UNIMAPSComposeTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					Scaffold(
						topBar = {
							TopAppBar(
								colors = TopAppBarDefaults.topAppBarColors(
									containerColor = MaterialTheme.colorScheme.primaryContainer,
									titleContentColor = MaterialTheme.colorScheme.onPrimary
								),
								title = {
									Text("Catálogo")
								},
								navigationIcon = {
									IconButton(onClick = {
										atv.finish()
									}) {
										Icon(
											imageVector = Icons.Filled.ArrowBack,
											contentDescription = "Voltar",
											tint = MaterialTheme.colorScheme.onPrimary
										)
									}
								}
							)
						}
					) { innerPadding ->

						LazyColumn (
							modifier = Modifier
								.padding(innerPadding)
								.padding(16.dp)
						) {
							for (item in items) {
								item {
									val itm = item.getItem()
									if (itm != null) ItemCard(item = itm);
								}
							}
						}
					}
				}
			}
		}
	}
}