package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DarkGrayBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnimeFitViewModel
import java.io.InputStream

@Composable
fun FoodScannerScreen(
    viewModel: AnimeFitViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanningMeal.collectAsStateWithLifecycle()
    val scannedResult by viewModel.scannedResult.collectAsStateWithLifecycle()
    val scannedBitmap by viewModel.scannedBitmap.collectAsStateWithLifecycle()

    // Camera photo launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.onScanImage(bitmap)
        }
    }

    // Modern Android Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.onScanImage(bitmap)
                }
            } catch (_: Exception) {
            }
        }
    }

    // Editable fields for confirmed meal
    var editableName by remember(scannedResult) { mutableStateOf(scannedResult?.foodName ?: "") }
    var editablePortion by remember(scannedResult) { mutableStateOf(scannedResult?.portionSize ?: "") }
    var editableCalories by remember(scannedResult) { mutableStateOf(scannedResult?.calories?.toString() ?: "500") }
    var editableProtein by remember(scannedResult) { mutableStateOf(scannedResult?.protein?.toString() ?: "35.0") }
    var editableCarbs by remember(scannedResult) { mutableStateOf(scannedResult?.carbs?.toString() ?: "55.0") }
    var editableFat by remember(scannedResult) { mutableStateOf(scannedResult?.fat?.toString() ?: "15.0") }
    var selectedMealType by remember { mutableStateOf("Lunch") }
    var mealNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Scanner animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 240f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("food_scanner_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI CYBER FOOD SCANNER",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Smart Macro Computer Vision",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F2537),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GEMINI 3.5 AI",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- Viewfinder Box ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0A101C))
                .border(androidx.compose.foundation.BorderStroke(1.5.dp, if (isScanning) NeonMagenta else NeonCyan), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (scannedBitmap != null) {
                Image(
                    bitmap = scannedBitmap!!.asImageBitmap(),
                    contentDescription = "Scanned Meal",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Futuristic Camera HUD Reticle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color(0x6600F0FF),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FRAME FOOD IN RETICLE",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Take a photo or pick from gallery to estimate calories & macros",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Animated Laser Line when scanning
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .offset(y = laserOffsetY.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, NeonMagenta, NeonCyan, Color.Transparent)
                            )
                        )
                )
            }

            // Viewfinder HUD Corner Accents
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = "TARGET: FOOD",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = if (isScanning) "ANALYZING..." else "READY",
                    color = if (isScanning) NeonMagenta else NeonLime,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            if (isScanning) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC090D16),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Analyzing Food & Macronutrients...",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- Action Buttons (Camera / Gallery / Demo Presets) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("open_camera_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("CAMERA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("open_gallery_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
            ) {
                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text("GALLERY", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // 1-Tap Quick Demo Scans (Ideal for emulator testing)
        Column {
            Text(
                text = "OR TEST INSTANT PRESET MEALS:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip(label = "🍱 Bento Box", onClick = {
                    val dummy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
                    viewModel.onScanImage(dummy)
                })
                PresetChip(label = "🐟 Salmon Don", onClick = {
                    val dummy = Bitmap.createBitmap(301, 301, Bitmap.Config.ARGB_8888)
                    viewModel.onScanImage(dummy)
                })
                PresetChip(label = "🥤 Whey Shake", onClick = {
                    val dummy = Bitmap.createBitmap(305, 305, Bitmap.Config.ARGB_8888)
                    viewModel.onScanImage(dummy)
                })
            }
        }

        // --- Scanned Result & Confirmation Form ---
        if (scannedResult != null) {
            CyberCard(
                borderColor = NeonLime,
                backgroundColor = CyberSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI MEAL RECOGNIZED",
                                color = NeonLime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1B3828)
                        ) {
                            Text(
                                text = "GRADE ${scannedResult?.healthGrade}",
                                color = NeonLime,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Anime Power Tip
                    if (!scannedResult?.animePowerTip.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF162033),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300F0FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⚡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = scannedResult!!.animePowerTip,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Food Name Field
                    OutlinedTextField(
                        value = editableName,
                        onValueChange = { editableName = it },
                        label = { Text("Food Name", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("food_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkGrayBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    // Portion Size Field
                    OutlinedTextField(
                        value = editablePortion,
                        onValueChange = { editablePortion = it },
                        label = { Text("Portion Size", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("portion_size_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkGrayBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    // Editable Calories & Macros Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editableCalories,
                            onValueChange = { editableCalories = it },
                            label = { Text("Kcal", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calories_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = DarkGrayBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editableProtein,
                            onValueChange = { editableProtein = it },
                            label = { Text("Protein (g)", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("protein_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMagenta,
                                unfocusedBorderColor = DarkGrayBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editableCarbs,
                            onValueChange = { editableCarbs = it },
                            label = { Text("Carbs (g)", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("carbs_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkGrayBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editableFat,
                            onValueChange = { editableFat = it },
                            label = { Text("Fat (g)", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonLime,
                                unfocusedBorderColor = DarkGrayBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Meal Type Selector Pills
                    Text(
                        text = "Meal Category:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                            val isSelected = selectedMealType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NeonCyan else CyberSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMealType = type },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkGrayBorder
                                )
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Confirm and Save Button
                    Button(
                        onClick = {
                            val cals = editableCalories.toIntOrNull() ?: 500
                            val p = editableProtein.toFloatOrNull() ?: 30f
                            val c = editableCarbs.toFloatOrNull() ?: 50f
                            val f = editableFat.toFloatOrNull() ?: 15f

                            viewModel.saveScannedMeal(
                                foodName = editableName.ifBlank { "Power Meal" },
                                calories = cals,
                                protein = p,
                                carbs = c,
                                fat = f,
                                portionSize = editablePortion.ifBlank { "1 serving" },
                                mealType = selectedMealType,
                                notes = mealNotes
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_meal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "CONFIRM & LOG MEAL (+60 XP)",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearScannedResult() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkGrayBorder)
                    ) {
                        Text("CANCEL SCAN", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PresetChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkGrayBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
