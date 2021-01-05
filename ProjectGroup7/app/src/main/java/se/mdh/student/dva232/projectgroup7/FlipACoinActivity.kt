package se.mdh.student.dva232.projectgroup7

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_flip_a_coin.*
import kotlin.random.Random

/**
 * Represents FlipACoin activity.
 * Presents the user with choice between heads or tails, runs animation and shows user the outcome.
 * By definition:
 *      heads == true,
 *      tails == false
 * */
class FlipACoinActivity : AppCompatActivity() {
    private var userChoice=true;
    /**
     * Initializes FlipACoinActivity by adding onClick listeners on the buttons.
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_a_coin)
        findViewById<Button>(R.id.heads).setOnClickListener{
            flip(true)
        }
        findViewById<Button>(R.id.tails).setOnClickListener{
            flip(false)
        }
    }

    /**
     * Performs the flip a coin game. Runs the animation and runs displaying of the game outcome.
     * @param userChoice defines if user chose heads (true) or tails (false)
     * */
    private fun flip(choice: Boolean) {
        userChoice = choice
        findViewById<TextView>(R.id.flipResult).setText("")
        findViewById<TextView>(R.id.flipWinOrLoseText).setText("")
        findViewById<Button>(R.id.heads).visibility = View.GONE
        findViewById<Button>(R.id.tails).visibility = View.GONE
        animate()
    }

    /**
     * Shows the outcome of flipping the coin: what side was flipped and whether user won or lost.
     * */
    private fun showResult() {
        var outcome = evaluateOutcome()
        if(outcome) {
            findViewById<TextView>(R.id.flipResult).setText("It's HEADS")
        } else {
            findViewById<TextView>(R.id.flipResult).setText("It's TAILS")
        }
        if(outcome==userChoice) {
            findViewById<TextView>(R.id.flipWinOrLoseText).setText("You won!")
        } else {
            findViewById<TextView>(R.id.flipWinOrLoseText).setText("You lost...")
        }
    }

    /**
     * Evalueates outcome of flipping the coin.
     * @return true for heads, false for tails
     * */
    private fun evaluateOutcome(): Boolean {
        return Random.nextBoolean()
    }

    /**
     * Plays flipping the coin animation and invokes showing of end result upon animation end.
     * */
    private fun animate() {
        animationView.playAnimation()
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                //Do nothing
            }

            override fun onAnimationEnd(animation: Animator?) {
                showResult()
                findViewById<Button>(R.id.heads).visibility = View.VISIBLE
                findViewById<Button>(R.id.tails).visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
                //Do nothing
            }

            override fun onAnimationStart(animation: Animator?) {
                //Do nothing
            }
        })
    }

}