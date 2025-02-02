package app.withyourwallet.vote.android.ui.retailers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RetailersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the retailers fragment"
    }
    val text: LiveData<String> = _text
}
