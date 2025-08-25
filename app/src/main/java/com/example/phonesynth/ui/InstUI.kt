package com.example.phonesynth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.phonesynth.R
import com.example.phonesynth.component.*
import kotlin.math.roundToInt
import com.example.phonesynth.ui.theme.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import java.lang.Math.floorMod
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sign

@Composable
fun InstrumentPanelForEnsemble(
    repo: Repository,
    audioController: AudioController,
    paramKey: AudioParam,
) {
    BoxWithConstraints {
        val paddingValue = 10.dp
        val width = maxWidth - paddingValue * 2
        val height = maxHeight - paddingValue * 2

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            when (paramKey) {
                AudioParam.NONE -> {
                    //none
                }
                AudioParam.TONE -> {
                    PitchSnapPanel(repo, width)
                    MakeTransposeKeyboard(repo)
                }
                AudioParam.VOLUME -> {
                    MutePanel(repo, width)
                }
                AudioParam.ARTICULATION -> {
//                    WaveFormPanel(repo, audioController)
                }
                else -> {
                    //none
                }
            }
            InitValuesPanel(repo)
        }
    }
}

@Composable
fun InstrumentPanel(
    repo: Repository,
    setWaveform: (Int) -> Unit
) {
    BoxWithConstraints {
        val paddingValue = 10.dp
        val width = maxWidth - paddingValue * 2
        val height = maxHeight - paddingValue * 2

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                MutePanel(repo, width / 2 - 1.dp)
                Spacer(modifier = Modifier.width(2.dp))
                PitchSnapPanel(repo, width - 1.dp)
            }
            MakeTransposeKeyboard(repo)
            HorizontalDivider(modifier = Modifier.padding(20.dp, 10.dp))
            WaveFormPanel(width, 100.dp, setWaveform)
            InitValuesPanel(repo)
        }
    }
}

@Composable
fun MutePanel(
    repo: Repository,
    width: Dp,
) {
    Row {
        MakePadButton(
            { repo.isMuted = true },
            { repo.isMuted = false },
            icon = R.drawable.volume_mute,
            textInput = "Mute: ${repo.isMuted}\n",
            width
        )
    }
}

@Composable
fun PitchSnapPanel(
    repo: Repository,
    width: Dp
) {
    Row {
        MakePadButton(
            { repo.isSnap = false },
            { repo.isSnap = true },
            icon = 0,
            textInput = "Pitch Snap: ${repo.isSnap}\n",
            width
        )
    }
}

@Composable
fun MakePadButton(
    onPressedFun: @Composable () -> Unit,
    onReleasedFun: @Composable () -> Unit,
    icon: Int = 0,
    textInput: String = "",
    width: Dp = 100.dp,
    height: Dp = 200.dp,
) {
    var isPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(width, height)
//            .border(
//                width = 0.dp,
//                color = Silver,
//                shape = RoundedCornerShape(10.dp)
//            )
            .background(
                color = LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // 押してる
                        isPress = true

                        tryAwaitRelease()
                        // 離した
                        isPress = false
                    }
                )
            }
    ) {
        if (isPress) {
            onPressedFun()
        } else {
            onReleasedFun()
        }

        Box(
            modifier = Modifier
                .size(width, height),
            contentAlignment = Alignment.Center
        ) {
            Row {
                if (icon != 0) {
                    Image(
                        painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp, 30.dp)
                    )
                }
                Column {
                    Text(text = textInput)
                }
            }
        }
    }
}

// TODO: WaveTableをより細かく制御する
@Composable
fun WaveFormPanel(
    width: Dp,
    height: Dp,
    setWaveform: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
    ) {
        MakeButton({ setWaveform(0) }, "Sin", width / 4, height)
        MakeButton({ setWaveform(1) }, "Tri", width / 4, height)
        MakeButton({ setWaveform(2) }, "Saw", width / 4, height)
        MakeButton({ setWaveform(3) }, "Squ", width / 4, height)
    }
}

@Composable
fun InitValuesPanel(
    repo: Repository,
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .clickable { repo.setInitValues() }
            .fillMaxWidth()
            .height(100.dp)
            .background(LightGray),
        contentAlignment = Alignment.Center
    ) {
        Row {
            Image(
                painterResource(id = R.drawable.refresh),
                contentDescription = null,
                modifier = Modifier.size(30.dp, 30.dp)
            )
            Column {
                Text(
                    text = "音程初期化\n気圧:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${repo.pressure.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun MakeButton(
    onPressedFun: @Composable () -> Unit,
    textInput: String = "",
    width: Dp = 80.dp,
    height: Dp = 80.dp,
) {
    var isPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(width, height)
            .border(
                width = 1.dp,
                color = WhiteSmoke,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = { isPress = true })
            .background(
                color = DarkGray,
                shape = RoundedCornerShape(10.dp)
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        if (isPress) {
            onPressedFun()
            isPress = false
        }

        Text(
            text = textInput,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MakeDraggable(repo: Repository) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 300.dp)
    ) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Purple500)
                .size(40.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        )
        repo.articulation.floatValue = 0.5f + (offsetX / 1000)
//        angle.x.value = (offsetY / 10)
    }
}

@Composable
fun PianoKeyboard(repo: Repository, keyWidth: Dp = 46.dp, keyHeight: Dp = 120.dp, keyWidthRetio: Float = 1/2f, keyHeightRetio: Float = 3 / 5f) {
    val octaveRange = 1
    val keyShift = 7    // 7と0以外はダメそう
    val whiteKeyWidth = keyWidth
    val blackKeyWidth = keyWidth * keyWidthRetio
    val whiteKeyHeight = keyHeight
    val blackKeyHeight = keyHeight * keyHeightRetio

    var pushedKey by remember { mutableStateOf(0) }

    fun keyArray(n: Int): Int {
        // nがマイナスにならないように12ずつシフト
        val upShift = (ceil(keyShift / 12f) * 12).toInt()
        return (n + upShift) % 12
    }

    fun isBlackKey(num: Int): Boolean {
        return when (keyArray(num)) {
            1, 3, 6, 8, 10 -> true
            else -> false
        }
    }

    @Composable
    fun keyUI(
        style: String,
        i: Int,
        offset: Dp,
    ) {
        val xOffset: Dp
        val index: Float
        val keyWidth: Dp
        val keyHeight: Dp
        val colorDefault: Color
        val colorSpecial: Color
        val colorPushed: Color

        when (style) {
            "black" -> {
                xOffset = offset
                index = 1f
                keyWidth = blackKeyWidth
                keyHeight = blackKeyHeight
                colorDefault = Color.Black
                colorSpecial = Color.Black
                colorPushed = DullGray
            }
            else -> {
                xOffset = 0.dp
                index = 0f
                keyWidth = whiteKeyWidth
                keyHeight = whiteKeyHeight
                colorDefault = Color.White
                colorSpecial = Teal200
                colorPushed = LightGray
            }
        }
        Box(
            modifier = Modifier
                .offset(x = xOffset)
                .zIndex(index)
                .width(keyWidth)
                .height(keyHeight)
                .background(
                    color = if (keyArray(i) == 0) {
                        colorSpecial
                    } else {
                        colorDefault
                    }
                )
                .border(
                    width = 1.dp,
                    color = Color.Black
                )
                .clickable {
                    pushedKey = i
                    repo.transpose = i
                }
                .then(
                    if (pushedKey == i) {
                        Modifier
                            .background(
                                color = colorPushed
                            )
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                    } else {
                        Modifier
                    }
                )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(whiteKeyHeight)
            .horizontalScroll(rememberScrollState())
    ) {
        // white key
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0 - keyShift..octaveRange * 12) {
                if (!isBlackKey(i)) {
                    keyUI("white", i, 0.dp)
                }
            }
        }

        // black key
        Row(modifier = Modifier.fillMaxWidth()) {
            // 鍵盤上部の白い隙間 = offset = {白鍵(3 or 4) - 黒鍵(2 or 3) - } / 白鍵(3 or 4)
            // 生成された黒鍵の幅分だけ勝手にズレるので、隙間オフセットを加算していけば良い
            // keyOffsetsは
            val offsetCtoE = (whiteKeyWidth * 3f - blackKeyWidth * 2) /3f
            val offsetFtoB = (whiteKeyWidth * 4f - blackKeyWidth * 3) /4f
            val keyOffsets = mapOf(
                1 to  offsetCtoE,
                3 to  offsetCtoE,
                6 to  offsetFtoB,
                8 to  offsetFtoB,
                10 to offsetFtoB
            )

            var xOffset = 0.dp
            val shiftDelta = 0.dp   // keyShiftで出る位置のズレ補正用
            for (i in 0 - keyShift..octaveRange * 12) {
                val note = keyArray(i)
                if (keyOffsets.containsKey(note)) {
                    xOffset += keyOffsets[note]!!

                    keyUI("black", i, xOffset - shiftDelta)

                    // 黒鍵のない間を飛ばす
                    when (note) {
                        3 ->  { xOffset += offsetCtoE - 1.dp }
                        10 -> { xOffset += offsetFtoB }
                    }
                }
            }
        }
    }
}

@Composable
fun MakeTransposeKeyboard(repo: Repository) {
    Column {
        Text("Transpose")
        PianoKeyboard(repo, keyHeight = 150.dp, keyWidthRetio = 2/3f)
    }
}

@Composable
fun MakeSlider(text: String, onPressedFun: (Float) -> Unit) {
    var sliderPosition by remember { mutableStateOf(1f) }

    Column {
        Text(text)
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onPressedFun(sliderPosition)  //viewModel.audioEngine.setAmplitude()
            },
            valueRange = 0f..1f
        )
    }
}

@Composable
fun MakeSlider() {
    var sliderPosition by remember { mutableStateOf(0f) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
//                sensor.transpose = sliderPosition.toInt()
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 11,
            valueRange = 0f..12f
        )
//        Text(text = sliderPosition.toString())
    }
}

@Composable
fun MakeSwitch(
    assignedFun: @Composable () -> Unit,
    textInput: String = "",
) {
    var isChecked by remember { mutableStateOf(true) }

    Row {
        Switch(
            onCheckedChange = {
                isChecked = it
            },
            checked = isChecked
        )
        if (isChecked) {
            assignedFun()
        }
        Text(text = textInput)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownForEnsemble(
    selectedParam: AudioParam,
    onParamSelect: (AudioParam) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedParam.paramName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Parameter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AudioParam.entries.forEach { param ->
                    DropdownMenuItem(
                        text = { Text(param.paramName) },
                        onClick = {
                            onParamSelect(param)
                            expanded = false
                        }
                    )
                    if (param === AudioParam.NONE) HorizontalDivider(
                        modifier = Modifier.padding(
                            20.dp,
                            10.dp
                        )
                    )
                }
            }
        }
    }
}


// 以下Bell用UI



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownForBell(
    presetList: List<String>,
    selectAudio: (String) -> Unit,
    addAudio: () -> Unit,
    removeAudio: (String) -> Unit,
    renameAudio: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTone by remember { mutableStateOf(presetList.firstOrNull() ?: "") }
    var change by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = selectedTone,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(),
                label = { Text("Select Tone") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add New Tone") },
                    onClick = { addAudio() }
                )
                HorizontalDivider(modifier = Modifier.padding(20.dp, 10.dp))
                presetList.forEach { file ->
                    DropdownMenuItem(
                        text = { Text(file) },
                        onClick = {
                            selectedTone = file
                            expanded = false
                            change = true
                        }
                    )
                }
            }
        }
    }
    if (change) {
        selectAudio(selectedTone)
        change = false
    }
}
