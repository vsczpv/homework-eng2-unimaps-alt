package br.univali.eng2.santissimo.unimaps_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme

object CatergoryControl {

	private val catergories: MutableList<Service> = ArrayList()

	init {
		for (i in 1..8) {
			catergories.add(ServiceControl.fetchServiceById(i)!!)
		}
	}
	fun getServices(): List<Service> = catergories
	fun getServiceCount() = catergories.size
}

class CatergoryActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val catergoryName = intent.extras!!.getString("catergory")
		setContent {
			CatergoryUI(this, catergoryName!!)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CatergoryUI(atv: CatergoryActivity? = CatergoryActivity(), catergory_name: String = "None") {
	UNIMAPSComposeTheme {
		// A surface container using the 'background' color from the theme
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			Scaffold (
				topBar = {
					TopAppBar(
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor = MaterialTheme.colorScheme.primaryContainer,
							titleContentColor = MaterialTheme.colorScheme.onPrimary
						),
						title = {
							Text(catergory_name)
						},
						navigationIcon = {
							IconButton(onClick = {
								atv!!.finish()
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
					modifier = Modifier.padding(innerPadding)
				) {
					for (srv in CatergoryControl.getServices()) {
						item {
							Widgets.ServiceCardButton(service = srv) {
								val navi = Intent(atv!!.baseContext, ServiceActivity::class.java)
								navi.putExtra("service", srv.id)
								atv.startActivity(navi)
							}
						}
					}
					/*
					items(CatergoryControl.getCatergoryCount()) {
						Widgets.ServiceCardButton(service = CatergoryControl.getCatergories()[it]) {
							val navi = Intent(atv!!.baseContext, ServiceActivity::class.java)
							navi.putExtra("service", CatergoryControl.getCatergories()[it].id)
							atv.startActivity(navi)
						}
					}

					 */
				}
			}
		}
	}
}