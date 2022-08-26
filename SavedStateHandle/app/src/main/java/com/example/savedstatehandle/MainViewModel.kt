package com.example.savedstatehandle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


class MainViewModel(
    private val savedStateHandle: SavedStateHandle
):ViewModel() {

    companion object {
        private const val KEY_TEXT = "text"
    }

    private var _savedData = savedStateHandle.getLiveData(KEY_TEXT, "")
    val savedData : LiveData<String> = _savedData


    private var _data = MutableLiveData("")
    val data : LiveData<String> = _data

    fun updateText(text:String){
        savedStateHandle[KEY_TEXT] = text
        _data.value = text
    }
}
