package com.example.uploadfile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uploadfile.ui.theme.UploadFileTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registering file picker intent
        val pickFileLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                // You can process the selected file URI here
                Toast.makeText(this, "File selected: ${uri.path}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            UploadFileTheme {
                BoxWithFileUpload {
                    // This function is called when the user taps on the rectangular box
                    pickFileLauncher.launch("*/*")  // Launch file picker for all file types
                }
            }
        }
    }
}

@Composable
fun BoxWithFileUpload(onFileUploadClick: () -> Unit) {
    // Rectangle Box UI
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onFileUploadClick()  // Trigger the file picker when clicked
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap here to upload a file",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFileUploadBox() {
    UploadFileTheme {
        BoxWithFileUpload(onFileUploadClick = {})
    }
}
