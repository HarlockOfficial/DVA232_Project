package se.mdh.student.dva232.projectgroup7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openDices(view: View) {
        val intent = Intent(this, DicesActivity::class.java)
        startActivity(intent)
    }
}