package DI.Composables.HomeSection

import DI.Composables.GeneralTemplate
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomePageScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { HomePageHeaderSection() },
        contentBody = { HomePageBody(navController) }
    )
}

@Composable
fun HomePageHeaderSection() {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        ) {
            Column {
                Text(
                    text = "Hi, Welcome Back",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Good Morning",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = { }
                    )
                    .size(40.dp)
                    .background(Color(0xFF53dba9))
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFF53dba9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = "Notifications Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            )
            {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_total_balance),
                                contentDescription = "Total Balance Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Text(
                            text = "Total Balance",
                            color = Color.Black,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$7,783.00",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.8f)
                        .background(Color.White)
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_total_expense),
                                contentDescription = "Total Expense Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Text(
                            text = "Total Expense",
                            color = Color.Black,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "-\$1,187.40",
                        color = Color(0xFF008DDD),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Tick Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "30% Of Your Expenses, Looks Good.",
                    color = Color.Black,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HomePageBody(
    navController: NavController
) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val transactions = viewModel.filteredTransactions.value

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchTransactions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFFBF5))
            .padding(16.dp)
    ) {
        OverviewSection()
        TimeSelector()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(7.dp))

//            if (transactions.isNotEmpty()) {
//                GeneralTransactionSummary(
//                    navController = navController,
//                    transactions = transactions
//                )
//            } else {
//                Text("No transactions found.")
//            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun OverviewSection() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF53dba9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 0.5f },
                            modifier = Modifier.size(64.dp),
                            color = Color(0xFF0080FF),
                            strokeWidth = 6.dp,
                            trackColor = Color.White.copy(alpha = 0.3f),
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_savings),
                            contentDescription = "Car",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Savings\nOn Goals",
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            VerticalDivider(
                thickness = 1.dp, color = Color.White
            )

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_total_expense),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Revenue Last Week",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "$4,000.00",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    thickness = 1.dp, color = Color.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_food),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Food Last Week",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "-$100.00",
                            color = Color(0xFF0080FF),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSelector() {
    val options = listOf("Daily", "Weekly", "Monthly")
    var selectedOption by remember { mutableStateOf("Monthly") }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFDFF7E2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF53dba9) else Color.Transparent)
                        .clickable { selectedOption = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}


