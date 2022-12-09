package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {

    //Create backing properties so that values can be read outside the class but not changed from outside the class
    private lateinit var _currentScrambledWord: String
    val currentScrambledWord: String
        get() = _currentScrambledWord

    private var _score = 0
    val score: Int
        get() = _score

    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount

    //list to keep track of words already used this game
    private var wordsList: MutableList<String> = mutableListOf()
    //word currently being guessed
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created!")
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
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
            wordsList.add(currentWord)
        }

    }


    //Helper function to provide next word, allows getNextWord() to remain private
    fun nextWord(): Boolean {
        return if (currentWordCount < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else false
    }

    //Private helper function to ensure that _score variable remains inaccessible outside GameViewModel class
    private fun increaseScore(){
        _score += SCORE_INCREASE
    }

    fun isUserWordCorrect(playerWord: String): Boolean{
        if (playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData () {
        _score = 0
        _currentWordCount = 0
        wordsList.clear()
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }


}