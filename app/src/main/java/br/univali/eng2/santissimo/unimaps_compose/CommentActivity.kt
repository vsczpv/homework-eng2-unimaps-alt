package br.univali.eng2.santissimo.unimaps_compose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme

class CommentActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			CommentUI(this)
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CommentUI(atv: CommentActivity = CommentActivity(), service: Service = ServiceControl.fetchServiceById(0)!!) {
	val comments  = remember { service.comments.comments }
	val isEditing = remember { mutableStateOf(false) }
	val text      = remember { mutableStateOf("") }
	UNIMAPSComposeTheme {
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
							Text("Comentários")
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
				},
				floatingActionButton = {
					if (isEditing.value) {
						Column {
							FloatingActionButton(onClick = {
								text.value      = ""
								isEditing.value = false
							}) {
								Icon(
									imageVector = Icons.Filled.Clear,
									contentDescription = "Cancelar",
									tint = MaterialTheme.colorScheme.onPrimary
								)
							}
							FloatingActionButton(onClick = {
								comments.add(Service.CommentControl.Comment(text.value))
								text.value      = ""
								isEditing.value = false
							},
								modifier = Modifier.padding(top = 8.dp)
							) {
								Icon(
									imageVector = Icons.Filled.Done,
									contentDescription = "Ok",
									tint = MaterialTheme.colorScheme.onPrimary
								)
							}
						}
					} else {
						FloatingActionButton(onClick = {
							if (comments.size == 10) {
								Toast.makeText(atv.baseContext, "Limite Alcançado", Toast.LENGTH_SHORT).show()
								return@FloatingActionButton
							}
							isEditing.value = true
						}) {
							Icon(
								imageVector = Icons.Filled.Add,
								contentDescription = "Comentar",
								tint = MaterialTheme.colorScheme.onPrimary
							)
						}
					}
				}
			) { innerPadding ->
				if (isEditing.value) {
					Column (
						modifier = Modifier.padding(innerPadding)
					) {
						TextField(
							value = text.value,
							onValueChange = { text.value = it },
							label = {
								Text("Escreva seu Comentário")
							},
							modifier = Modifier
								.fillMaxSize()
						)
					}
				} else {
					LazyColumn(
						modifier = Modifier.padding(innerPadding)
					) {
						for (comm in comments) {
							item {
								Widgets.CommentCard(comment = comm)
							}
						}
					}
				}
			}
		}
	}
}