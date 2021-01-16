package com.example.xkcdcomics.screens.slide

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.math.MathUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xkcdcomics.domain.XKCDComic
import com.example.xkcdcomics.domain.xkcdComicDefault
import com.example.xkcdcomics.repository.ComicsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SlideViewModel @Inject constructor(val repository: ComicsRepository) : ViewModel() {

    // Current Comic
    private val _currentComic = MutableLiveData(xkcdComicDefault)
    val currentComic: LiveData<XKCDComic>
        get() = _currentComic

    // Current Comic Number
    private val _currentComicNumber = MutableLiveData(1)
    val currentComicNumber: LiveData<Int>
        get() = _currentComicNumber

    fun onFirstComic() {
        loadComic(1)
    }

    fun onLastComic() {
        loadComic(repository.latestComic.value?.number ?: 1)
    }

    fun onPreviousComic() {
        loadComic((currentComicNumber.value?:2) - 1)
    }

    fun onNextComic() {
        loadComic((currentComicNumber.value?:0) + 1)
    }

    fun onRandomComic() {
        loadComic((1..(repository.latestComic.value?.number?:1)).random())
    }

    fun generateEditorActionListener() : TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view.clearFocus()
                    val imm: InputMethodManager? =
                        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)

                    var number = currentComicNumber.value!!
                    try {
                        number = view.text.toString().toInt()
                    } catch (e: Exception) {

                    }
                    loadComic(number)

                    return true
                }
                return false
            }

        }
    }

    fun onShareMenuItemSelected() {

    }

    private var job: Job? = null
    fun loadComic(number: Int) {
        Log.d("Hello", number.toString())
        _currentComicNumber.value = MathUtils.clamp(
            number,
            1, repository.latestComic.value?.number ?: 1)

        job?.cancel()
        job = viewModelScope.launch {
            _currentComic.value = repository.loadComic(currentComicNumber.value ?: 1)
        }
    }
}