import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sealnote.model.Scientific

class StealthScientificViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    private val calculator = Scientific()

    fun calculateSine(value: Double) {
        _result.value = calculator.sin(value).toString()
    }

    fun calculateSquareRoot(value: Double) {
        _result.value = calculator.squareRoot(value).toString()
    }

    fun calculateFactorial(value: Int) {
        if (value < 0) {
            _result.value = "Error"
        } else {
            _result.value = calculator.factorial(value).toString()
        }
    }

    fun clearResult() {
        _result.value = "0"
    }
}