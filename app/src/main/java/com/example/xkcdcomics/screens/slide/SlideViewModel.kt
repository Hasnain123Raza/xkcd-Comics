package com.example.xkcdcomics.screens.slide

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.math.MathUtils
import androidx.lifecycle.*
import com.example.xkcdcomics.database.getComicDatabase
import com.example.xkcdcomics.domain.xkcdComic
import com.example.xkcdcomics.domain.xkcdComicDefault
import com.example.xkcdcomics.repository.ComicsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

class SlideViewModel(application: Application) : AndroidViewModel(application) {

    // Current Comic
    private val _currentComic = MutableLiveData(xkcdComicDefault)
    val currentComic: LiveData<xkcdComic>
        get() = _currentComic

    // Current Comic Number
    private val _currentComicNumber = MutableLiveData(1)
    val currentComicNumber: LiveData<Int>
        get() = _currentComicNumber

    private val database = getComicDatabase(application)
    private val repository = ComicsRepository(database)

    fun onFirstComic() {
        loadComic(1)
    }

    fun onLastComic() {
        loadComic(repository.lastComic.value!!.number)
    }

    fun onPreviousComic() {
        loadComic(currentComicNumber.value!! - 1)
    }

    fun onNextComic() {
        loadComic(currentComicNumber.value!! + 1)
    }

    fun onRandomComic() {
        loadComic((1..repository.lastComic.value!!.number).random())
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
        _currentComicNumber.value = MathUtils.clamp(
            number,
            1, repository.lastComic.value!!.number)

        job?.cancel()
        job = viewModelScope.launch {
            _currentComic.value = repository.loadComic(currentComicNumber.value!!)
        }
    }

    init {
        loadComic(1)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SlideViewModel::class.java)) {
                return SlideViewModel(application) as T
            }
            throw IllegalArgumentException()
        }

    }

}