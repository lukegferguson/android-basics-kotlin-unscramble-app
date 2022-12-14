package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {

    //Create backing properties so that values can be read outside the class but not changed from outside the class
    private val _currentScrambledWord = MutableLiveData<String>()
    //Convert scrambled word to TtsSpan of TYPE_VERBATIM so that screenreaders read individual letters
    //Otherwise they would attempt to read the scrambled word phonetically
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    //list to keep track of words already used this game
    private var wordsList: MutableList<String> = mutableListOf()
    //word currently being guessed
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    /*
    * Iterates _currentWordCount, updates _currentScrambledWord, and adds result to wordList.
    */
    private fun getNextWord(){
        currentWord = allWordsList.random() //Get a random word from the words list
        val tempWord = currentWord.toCharArray() //Convert to char array for shuffling

        //Shuffle the character array and verify shuffled word isn't the same as original word
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        //verify current word has not been used before in game
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
//TODO Implement logic for selecting words based on length for different difficulty levels
    }


    //Helper function to check if game is over, if not provide next word. Also allows getNextWord() to remain private
    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else false
    }

    //Private helper function to ensure that _score variable remains inaccessible outside GameViewModel class
    private fun increaseScore(){
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean{
        if (playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData () {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }



}