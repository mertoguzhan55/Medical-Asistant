package com.example.medical_asistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medical_asistant.ui.theme.MedicalAsistantTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicalAsistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuestionAnswerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Retrofit API Arayüzü
interface ApiService {
    @POST("ask-question")
    suspend fun askQuestion(@Body request: QuestionRequest): AnswerResponse
}

// Veri modelleri
data class QuestionRequest(val question: String)
data class AnswerResponse(val answer: String)

// Retrofit Client
object RetrofitClient {
    private const val BASE_URL = "***"  // FastAPI sunucunun adresini güncelle


    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// UI: Soru sorma ekranı
@Composable
fun QuestionAnswerScreen(modifier: Modifier = Modifier) {
    var questionText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("Cevap burada görünecek...") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Sorunuzu girin") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.instance.askQuestion(QuestionRequest(questionText))
                        responseText = response.answer
                    } catch (e: Exception) {
                        responseText = "Hata: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soruyu Gönder")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = responseText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionAnswerPreview() {
    MedicalAsistantTheme {
        QuestionAnswerScreen()
    }
}
