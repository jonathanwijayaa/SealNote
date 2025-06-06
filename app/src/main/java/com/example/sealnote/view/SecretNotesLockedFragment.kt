import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R // Pastikan ini adalah path yang benar ke R class Anda

// Definisi Warna dari XML
val LockedScreenBackground = Color(0xFF152332) // Background dari FrameLayout
val LockedScreenTextColor = Color(0xFFFFFFFE)    // Warna teks (putih)

@Composable
fun NotesLockedScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LockedScreenBackground // Efektif menjadi background layar
    ) {
        // Box digunakan untuk memusatkan konten di tengah layar
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // Memusatkan Column di tengah Box
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Memusatkan item di dalam Column
                verticalArrangement = Arrangement.Center // Memastikan konten di dalam Column juga terpusat
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock), // Pastikan drawable ini ada
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Fit // Sesuai dengan fitXY, namun Fit menjaga aspek rasio
                )

                // Spacer untuk marginTop pada lockedNotesText
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Notes Locked",
                    color = LockedScreenTextColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center // Sesuai dengan android:gravity="center"
                )
                // marginBottom="50dp" pada TextView di XML memengaruhi ukuran parent ConstraintLayout
                // yang wrap_content. Dalam Column yang terpusat ini, jika perlu ruang di bawah teks,
                // bisa ditambahkan Spacer lagi. Namun, untuk pemusatan visual, ini sudah cukup.
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF) // Preview dengan background putih luar
@Composable
fun NotesLockedScreenPreviewWithOuterBackground() {
    // Ini hanya untuk menunjukkan bagaimana Surface dengan warna LockedScreenBackground
    // akan terlihat jika ada background putih di luarnya (seperti struktur XML asli)
    Box(Modifier.fillMaxSize().background(Color.White)) {
        NotesLockedScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun NotesLockedScreenPreview() {
    // Preview langsung dengan background yang efektif terlihat oleh pengguna
    // Anda mungkin perlu membungkusnya dengan MaterialTheme jika Composable Anda
    // secara implisit bergantung pada nilai dari tema (misalnya, tipografi default).
    // MaterialTheme {
    NotesLockedScreen()
    // }
}
