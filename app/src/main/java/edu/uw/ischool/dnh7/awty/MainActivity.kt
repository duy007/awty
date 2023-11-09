package edu.uw.ischool.dnh7.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import edu.uw.ischool.dnh7.awty.databinding.ActivityMainBinding
import org.w3c.dom.Text


const val ALARM_ACTION = "edu.uw.ischool.dnh7.awty,ALARM"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var receiver : BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.isEnabled = false


        binding.minute.addTextChangedListener(
            object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.button.isEnabled = s.toString().trim().isNotEmpty() && binding.phone.text.isNotEmpty() && binding.message.text.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {

                }

            }
        )

        binding.phone.addTextChangedListener(
            object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.button.isEnabled = s.toString().trim().isNotEmpty() && binding.minute.text.isNotEmpty() && binding.message.text.isNotEmpty()

                }

                override fun afterTextChanged(s: Editable?) {

                }

            }
        )

        binding.message.addTextChangedListener(
            object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.button.isEnabled = s.toString().trim().isNotEmpty() && binding.minute.text.isNotEmpty() && binding.phone.text.isNotEmpty()

                }

                override fun afterTextChanged(s: Editable?) {

                }

            }
        )

        binding.button.setOnClickListener {
            if (binding.button.text == this.getString(R.string.start)) {
                if (validInfo()) {
                    binding.button.text = this.getString(R.string.stop)
                    sentMessage()
                } else {
                    Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.button.text = this.getString(R.string.start)
                if (receiver != null) {
                    unregisterReceiver(receiver)
                    receiver = null
                }
            }
        }
    }

    private fun validInfo() : Boolean {
        return !binding.minute.text.toString().contains(".") && binding.phone.text.length > 3
    }

    private fun sentMessage() {
        val activityThis = this

        if (receiver == null) {
            Log.d("makeReciever", "making reciever")
            // Make sure of our BroadcastReceiver; remember how we can
            // create an object on the fly that inherits from a class?
            // Let's use that to create an anonymous subclass of the
            // BroadcastReceiver type, and then register it dynamically
            // since we don't really need much beyond just catching the
            // intent fired at us from the AlarmManager
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Toast.makeText(activityThis, getString(R.string.toast_message, binding.phone.text, binding.message.text), Toast.LENGTH_SHORT).show()
                    Log.d("sent", "alarm")
                }
            }
            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)
        }

        // Create the PendingIntent
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Get the Alarm Manager
        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            binding.minute.text.toString().toLong() * 60000,
            pendingIntent)
    }
}
