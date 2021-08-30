package com.raywenderlich.timefighter

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {      //MainActivity is declared as extending AppCompatActivity

    private val TAG = MainActivity::class.java.simpleName

    private var score = 0       //var to keep track of score

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    private var gameStarted = false //controls if game has started or not

    private lateinit var countDownTimer: CountDownTimer //counts down to 0
    private var initialCountdown: Long = 60000 //time to count down from
    private var countDownInterval: Long = 1000 //controls the rate that the countdown timer runs
    private var timeLeft = 60 // holds how many second are left in the countdown


    override fun onCreate(savedInstanceState: Bundle?) {    //Entry point for the activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)      //takes layout and puts it on screen

        Log.d(TAG, "onCreate called. Score is: $score")

        gameScoreTextView = findViewById(R.id.game_score_text_view)     //searches through activity_main to find view
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

        tapMeButton.setOnClickListener { view ->        //attaches tap listener that calls increment score when tapped
            val bounceAnimation = AnimationUtils.loadAnimation(this,        //calls bounce.xml to control button animation when pressed
                R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & timeLeft: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()      //calls showinfo if about menu pressed
        }
        return true
    }

    private fun showInfo() {        //menu info from menu.xml
        val dialogTitle = getString(R.string.about_title,
        BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun incrementScore() {      //Increment score logic, updates score when tap me button is pressed
        if (!gameStarted) {
            startGame()
        }
        score++

        val newScore = getString(R.string.your_score, score)        //retrieves your_score from string.xml and appends the score to text
        gameScoreTextView.text = newScore

    }

    private fun resetGame() {       //reset game logic

        score = 0   //base score at start of game

        val initialScore = getString(R.string.your_score, score)        //stores score as string and inserts into strings.xml
        gameScoreTextView.text = initialScore       //updates text to show score

        val initialTimeLeft = getString(R.string.time_left, 60)     //same as above with time left
        timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountdown, countDownInterval) { //sets up 60000 milsecs counted in 1000 milsec cycles

            override fun onTick(millisUntilFinished: Long) {        //called every time interval
                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)        //updates time left display
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {      // calls endgame function
                endGame()
            }
        }

        gameStarted = false
    }

    private fun restoreGame() {     //if screen orientation changes, uses saved values to start game where left off

        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval){
            override fun onTick(millisUntilFinished: Long) {

                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }


    private fun startGame() {       //start game logic

        countDownTimer.start()      //starts counter/game
        gameStarted = true          //game is started


    }

    private fun endGame() {     //end game logic

        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()    //display of final score
        resetGame() //calls reset game

    }

    companion object {

        private const val SCORE_KEY = "SCORE_KEY"

        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }
}
