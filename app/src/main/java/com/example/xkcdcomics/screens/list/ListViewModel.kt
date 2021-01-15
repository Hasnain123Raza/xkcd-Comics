package com.example.xkcdcomics.screens.list

import android.content.Context
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
import com.example.xkcdcomics.repository.ComicsRepository
import com.example.xkcdcomics.screens.list.SearchFilter.Companion.getAscendingSelection
import com.example.xkcdcomics.screens.list.SearchFilter.Companion.getDescendingSelection
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListViewModel @Inject constructor(val repository: ComicsRepository) : ViewModel() {

    // List items to display
    val comics = repository.comics
    val latestComic = repository.latestComic

    private var comicsToShow = 1
    val searchFilter = SearchFilter()

    fun applyFilter(): List<XKCDComic>? {
        return if (comics.value != null) {
            searchFilter.applyFilter(comicsToShow, comics.value!!)
        } else {
            null
        }
    }

    // Event to update recyclerview's comics data
    private val _updateRecyclerAdapterDataEvent = MutableLiveData(false)
    val updateRecyclerAdapterDataEvent : LiveData<Boolean>
        get() = _updateRecyclerAdapterDataEvent

    fun fireUpdateRecyclerAdapterDataEvent() {
        _updateRecyclerAdapterDataEvent.value = true
    }

    fun resetUpdateRecyclerAdapterDataEvent() {
        _updateRecyclerAdapterDataEvent.value = false
    }

    // Event to reset recyclerview's position
    private val _resetRecyclerViewPositionEvent = MutableLiveData(false)
    val resetRecyclerViewPositionEvent : LiveData<Boolean>
        get() = _resetRecyclerViewPositionEvent

    fun fireResetRecyclerViewPositionEvent() {
        _resetRecyclerViewPositionEvent.value = true
    }

    fun resetResetRecyclerViewPositionEvent() {
        _resetRecyclerViewPositionEvent.value = false
    }

    // Downloading flag
    private val _isDownloading = MutableLiveData(false)
    val isDownloading : LiveData<Boolean>
        get() = _isDownloading

    fun loadComics(amountToLoad: Int) {
        if (isDownloading.value!!) return

        viewModelScope.launch {
            _isDownloading.value = true
            comicsToShow = MathUtils.clamp(
                comicsToShow + amountToLoad,
                1, repository.latestComic.value?.number?:1)

            repository.loadComics(when (searchFilter.searchSetting) {
                SearchFilter.Setting.ASC -> getAscendingSelection(
                    comicsToShow, repository.latestComic.value?.number ?: 1)
                SearchFilter.Setting.DESC -> getDescendingSelection(
                    comicsToShow, repository.latestComic.value?.number ?: 1)
            })

            fireUpdateRecyclerAdapterDataEvent()
            _isDownloading.value = false
        }
    }

    // Generates the editor action listener for search editor
    fun generateEditorActionListener() : TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view.clearFocus()
                    val imm: InputMethodManager? =
                        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)

                    searchFilter.searchString = view.text.toString()
                    fireResetRecyclerViewPositionEvent()
                    fireUpdateRecyclerAdapterDataEvent()

                    return true
                }
                return false
            }

        }
    }
}