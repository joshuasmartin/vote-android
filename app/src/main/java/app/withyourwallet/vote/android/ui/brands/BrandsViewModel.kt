package app.withyourwallet.vote.android.ui.brands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BrandsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the brands fragment"
    }
    val text: LiveData<String> = _text
}
