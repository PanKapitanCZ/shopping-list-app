package com.example.shoppinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppinglistapp.ui.theme.ShoppingListAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingListApp(
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}



@Composable
fun ShoppingListApp(paddingValues: PaddingValues) {

    val sharedPrefs = SharedPreferencesManager(context = LocalContext.current)

    var sItems by remember { mutableStateOf(sharedPrefs.getDataList()) }
    var itemName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                val newItem = ShoppingItem(
                    id = sItems.size + 1,
                    name = "",
                    quantity = "",
                )
                sItems = sItems + newItem
                itemName = ""
                sharedPrefs.saveDataList(sItems)
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer),
            border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(250.dp)
                .height(60.dp)
                .offset(y = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add button",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = (-50).dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Item",
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = (-15).dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            items(sItems) {
                    item ->
                if (item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                            editName, editedQuantity ->
                        sItems = sItems.map {
                            if (it.id == item.id) {
                                it.copy(isEditing = false, name = editName, quantity = editedQuantity)
                            } else{
                                it
                            }
                        }
                        sharedPrefs.saveDataList(sItems)
                    })
                } else {
                    ShoppingListItem(item = item, onEditClick = {
                        sItems = sItems.map {
                            if (it.id == item.id) {
                                it.copy(isEditing = true)
                            } else {
                                it
                            }
                        }
                        sharedPrefs.saveDataList(sItems)
                    }, onDeleteClick = {sItems = sItems - item
                        sharedPrefs.saveDataList(sItems)})
                }
            }
        }

    }
}
@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, String) -> Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically){
        Column {
            TextField(value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                label = { Text(text = "Thing")}
            )
            TextField(value = editedQuantity,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                label = { Text(text = "Quantity")}
            )
        }
        IconButton(onClick = { isEditing = false
            onEditComplete(editedName, editedQuantity)}
        ){
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(100.dp))
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(16.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(16.dp))
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

