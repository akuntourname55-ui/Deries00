package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BigRed
import com.example.ui.theme.EvenGreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceCard
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.OddPurple
import com.example.ui.theme.SmallBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun AddPeriodDialog(
    onDismiss: () -> Unit,
    onSubmit: (dice: List<Int>, periodNumber: String?) -> Unit
) {
    var periodInput by remember { mutableStateOf("") }
    val diceValues = remember { mutableStateListOf(3, 4, 2, 5, 1, 6) }

    val currentTotal = diceValues.sum()
    val isBig = currentTotal >= 21
    val isEven = currentTotal % 2 == 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurfaceCard,
        title = {
            Text(
                text = "Input Hasil Dadu Manual",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Salin angka hasil dari layar game Sicbo 6 Anda untuk dianalisis polanya.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = periodInput,
                    onValueChange = { periodInput = it },
                    label = { Text("Nomor Periode (Opsional)", fontSize = 12.sp) },
                    placeholder = { Text("Otomatis jika dikosongkan", fontSize = 12.sp, color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Atur Nilai 6 Dadu (Tap untuk ubah nilai):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive 6 dice selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    diceValues.forEachIndexed { index, dieVal ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "D${index + 1}", fontSize = 10.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        // Cycle value 1 -> 6
                                        diceValues[index] = if (dieVal >= 6) 1 else dieVal + 1
                                    }
                            ) {
                                SicboDie(value = dieVal, size = 42.dp, isRedDie = index % 2 == 1)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Randomize Dice button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        for (i in 0 until 6) {
                            diceValues[i] = Random.nextInt(1, 7)
                        }
                    }) {
                        Text("Acak Dadu (Random)", fontSize = 11.sp, color = GoldLight)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Preview results
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightSurface)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total: $currentTotal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = if (isBig) "BESAR" else "KECIL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isBig) BigRed else SmallBlue
                    )
                    Text(
                        text = if (isEven) "GENAP" else "GANJIL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isEven) EvenGreen else OddPurple
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(diceValues.toList(), periodInput.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = MidnightBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_custom_period_button")
            ) {
                Text("Simpan Periode", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Batal")
            }
        }
    )
}
