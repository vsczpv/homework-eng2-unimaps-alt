package br.univali.eng2.santissimo.unimaps_compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme

object CatergoryControl {

	val catergories = mutableStateListOf<Pair<Service, Boolean>>()

	init {
		for (service in ServiceControl.loadedServices) {
			catergories.add(Pair(service.value.getService()!!, true))
		}
	}
	fun getServices(): List<Pair<Service,Boolean>> = catergories
	fun getServiceCount() = catergories.size

	fun search(term: String) {

		Log.i("CatergoryActivity UNIMAPS", "Search Attempt: $term")
		for (i in catergories.indices) {
			val srv = catergories[i].first
			if (srv.name.lowercase().contains(term.lowercase())) {
				catergories[i] = Pair(srv, true)
			} else {
				catergories[i] = Pair(srv, false)
			}
		}

	}

	fun searchReset() {
		Log.i("CatergoryActivity UNIMAPS", "Search Reset")
		for (i in catergories.indices) {
			catergories[i] = Pair(catergories[i].first, true)
		}
	}

	fun sort(how: String) {
		when (how) {
			"Alfabética" -> {
				catergories.sortBy { it.first.name }
			}
			"Horário" -> {
				catergories.sortBy {
					if (it.first.status == Service.ServiceStatus.Open) {
						it.first.closedTime
					} else {
						it.first.openTime
					}
				}
			}
			"Relevância" -> {
				catergories.sortByDescending { it.first.rating }
			}
			else -> {
				throw Exception("Invalid Sort")
			}
		}
	}
}

class CatergoryActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val catergoryName = intent.extras!!.getString("catergory")
		CatergoryControl.sort("Alfabética")
		setContent {
			CatergoryUI(this, catergoryName!!)
		}
	}

	override fun onPause() {
		super.onPause()
		CatergoryControl.searchReset()
	}

	override fun onStop() {
		super.onStop()
		CatergoryControl.searchReset()
	}

	override fun onNavigateUp(): Boolean {
		CatergoryControl.searchReset()
		return super.onNavigateUp()
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CatergoryUI(atv: CatergoryActivity? = CatergoryActivity(), catergory_name: String = "None") {
	val services = remember { CatergoryControl.catergories }

	var orderDrp by remember { mutableStateOf(false) }
	var filtrDrp by remember { mutableStateOf(false) }
	var orderSel by remember { mutableStateOf("Alfabética")}
	var filtrSel by remember { mutableStateOf("Nenhum")}

	var searchQuery  by remember { mutableStateOf("") }

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
							SearchBar(
								trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
								query = searchQuery,
								onQueryChange = { searchQuery = it },
								onSearch = { CatergoryControl.search(searchQuery) },
								active = false,
								onActiveChange = {},
								placeholder = { Text("Pesquise...") },
								modifier = Modifier
									.padding(bottom = 8.dp)
									.height(56.dp)
									.fillMaxSize()
							) {
								
							}
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
				Row (
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth()
				) {
					Box(
						modifier = Modifier
							.fillMaxHeight()
							.wrapContentSize(Alignment.TopStart)
							.padding(
								start = 12.dp
							)
					) {
						Row(
							modifier = Modifier.padding(innerPadding)
						) {
							Text(
								text = "Ordem: ${orderSel}",
								modifier = Modifier.padding(
									top = 12.dp
								)
							)
							IconButton(
								onClick = {
									orderDrp = true
								}
							) {
								Icon(
									Icons.Default.KeyboardArrowDown,
									contentDescription = null,
								)
							}
						}
						DropdownMenu(
							expanded = orderDrp,
							onDismissRequest = {
								orderDrp = false
							}
						) {
							DropdownMenuItem(
								text = { Text(text = "Alfabética") },
								onClick = {
									orderSel = "Alfabética"
									orderDrp = false
									CatergoryControl.sort(orderSel)
								},
							)
							DropdownMenuItem(
								text = { Text(text = "Horário") },
								onClick = {
									orderSel = "Horário"
									orderDrp = false
									CatergoryControl.sort(orderSel)
								}
							)
							DropdownMenuItem(
								text = { Text(text = "Relevância") },
								onClick = {
									orderSel = "Relevância"
									orderDrp = false
									CatergoryControl.sort(orderSel)
								}
							)
						}
					}
					Box(
						modifier = Modifier
							.fillMaxSize()
							.wrapContentSize(Alignment.TopEnd)
							.padding(
								start = 12.dp
							)
					) {
						Row(
							modifier = Modifier.padding(innerPadding)
						) {
							Text(
								text = "Filtro: ${filtrSel}",
								modifier = Modifier.padding(
									top = 12.dp
								)
							)
							IconButton(
								onClick = {
									filtrDrp = true
								}
							) {
								Icon(
									Icons.Default.KeyboardArrowDown,
									contentDescription = null,
								)
							}
						}
						DropdownMenu(
							expanded = filtrDrp,
							onDismissRequest = {
								filtrDrp = false
							}
						) {
							DropdownMenuItem(
								text = { Text(text = "Nenhum") },
								onClick = {
									filtrSel = "Nenhum"
									filtrDrp = false
								},
							)
							DropdownMenuItem(
								text = { Text(text = "Aberto") },
								onClick = {
									filtrSel = "Aberto"
									filtrDrp = false
								}
							)
							DropdownMenuItem(
								text = { Text(text = "Fechado") },
								onClick = {
									filtrSel = "Fechado"
									filtrDrp = false
								}
							)
						}
					}
				}
				LazyColumn (
					modifier = Modifier
						.padding(innerPadding)
						.padding(top = 48.dp)
				) {
					for (srv in services) {
						val show = when (filtrSel) {
							"Nenhum"  -> { true }
							"Aberto"  -> { srv.first.status == Service.ServiceStatus.Open }
							"Fechado" -> { srv.first.status == Service.ServiceStatus.Closed }
							else -> {
								throw Exception("Invalid Filter")
							}
						}
						if (show && srv.first.catergory == Service.catergoryFromString(catergory_name) && srv.second)
						{
							item {
								Widgets.ServiceCardButton(service = srv.first) {
									val navi =
										Intent(atv!!.baseContext, ServiceActivity::class.java)
									navi.putExtra("service", srv.first.id)
									atv.startActivity(navi)
								}
							}
						}
					}
				}
			}
		}
	}
}