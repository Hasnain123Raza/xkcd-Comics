package com.example.xkcdcomics.screens.list

import android.app.Application
import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.math.MathUtils
import androidx.lifecycle.*
import com.example.xkcdcomics.database.getComicDatabase
import com.example.xkcdcomics.domain.XKCDComic
import com.example.xkcdcomics.repository.ComicsRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getComicDatabase(application)
    private val repository = ComicsRepository(database)

    // List items to display
    val comics = repository.comics

    private var comicsToShow = (1..1)
    private var searchString = ""
    var searchFilter = SearchFilter.ASC

    enum class SearchFilter {
        ASC, DESC
    }

    fun applyFilter(listItems: List<XKCDComic>): List<XKCDComic> {
        var selected = listItems.filter { comicsToShow.contains(it.number) }

        if (searchString.isNotEmpty()) {
            selected.filter {
                it.title.contains(searchString, true)
            }.also { selected = it }
        }

        selected = when(searchFilter) {
            SearchFilter.ASC -> selected
                .sortedBy { it.number }
            SearchFilter.DESC -> selected
                .sortedByDescending { it.number }
        }

        return selected
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

    // Load the next few comics and fire events to update recycler view
    private var loadingComicsDebounce = false
    fun loadComics(amountToLoad: Int) {
        if (loadingComicsDebounce) return

        viewModelScope.launch {
            loadingComicsDebounce = true
            comicsToShow = 1..MathUtils.clamp(
                comicsToShow.last + amountToLoad,
                1, comics.value?.size?:1)
            if (comics.value != null) fireUpdateRecyclerAdapterDataEvent()
            repository.loadComics(amountToLoad)
            loadingComicsDebounce = false
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

                    searchString = view.text.toString()
                    fireUpdateRecyclerAdapterDataEvent()

                    return true
                }
                return false
            }

        }
    }

    init {
        loadComics(50)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
                return ListViewModel(application) as T
            }
            throw IllegalArgumentException()
        }

    }
}