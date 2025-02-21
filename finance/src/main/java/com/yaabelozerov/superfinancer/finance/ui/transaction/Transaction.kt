package com.yaabelozerov.superfinancer.finance.ui.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.finance.R
import com.yaabelozerov.superfinancer.finance.domain.Transaction
import com.yaabelozerov.superfinancer.finance.ui.FinanceScreenEvent
import com.yaabelozerov.superfinancer.finance.ui.FinanceVM

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Transaction(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    viewModel: FinanceVM,
) {
    var detailsOpen by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .clickable {
                detailsOpen = !detailsOpen
            }
            .padding(horizontal = 12.dp)
            , verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                transaction.valueInRubles.toString().plus(" ₽"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(transaction.timestamp, fontStyle = FontStyle.Italic)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.right_arrow),
                contentDescription = null,
                modifier = Modifier.size(16.dp).run {
                    if (transaction.isWithdrawal) rotate(180f) else this
                },
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(transaction.goal.second, style = MaterialTheme.typography.titleLarge)
        }
        if (transaction.comment.isNotEmpty()) {
            Text(transaction.comment)
        }
        AnimatedVisibility(detailsOpen) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!transaction.isWithdrawal) Button(onClick = {
                    viewModel.onEvent(
                        FinanceScreenEvent.MakeTransaction(
                            transaction.goal.first,
                            transaction.valueInRubles,
                            transaction.comment,
                            false
                        )
                    )
                    detailsOpen = false
                }) { Text("Repeat") }
                Button(onClick = {
                    viewModel.onEvent(
                        FinanceScreenEvent.DeleteTransaction(transaction.id)
                    )
                }) { Text("Delete") }
                OutlinedButton(onClick = {
                    detailsOpen = false
                }) { Text("Cancel") }
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
    }
}
