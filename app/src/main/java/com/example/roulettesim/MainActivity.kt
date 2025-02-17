package com.example.roulettesim

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.roulettesim.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var balance = 500 // Starting balance
    private var currentRound = 1 // Starting round number
    private val betAmounts = listOf(20, 40, 60)

    // UI Elements (declared here to be accessible throughout the activity)
    private lateinit var balanceTextView: TextView
    private lateinit var resultsTable: TableLayout
    private lateinit var scrollView: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        balanceTextView = findViewById(R.id.balance)
        resultsTable = findViewById(R.id.resultsTable)
        scrollView = findViewById(R.id.scrollView)

        val evenBetButtons = listOf(
            findViewById<Button>(R.id.betEven20),
            findViewById<Button>(R.id.betEven40),
            findViewById<Button>(R.id.betEven60)
        )
        val oddBetButtons = listOf(
            findViewById<Button>(R.id.betOdd20),
            findViewById<Button>(R.id.betOdd40),
            findViewById<Button>(R.id.betOdd60)
        )
        val skipButton: Button = findViewById(R.id.skipButton)
        val restartButton: Button = findViewById(R.id.restartButton)

        // Initialize the app state

        initializeState()

        fun spinRoulette(betType: String, betAmount: Int?) {
            // Generate a random number between 0 and 36
            val rouletteNumber = Random.nextInt(0, 37)
            var isZero = false;
            var isWin = false;
            if (rouletteNumber == 0 || rouletteNumber == 37) {
                isZero = true
            } else {
                isWin = (betType == "Even" && rouletteNumber % 2 == 0) ||
                        (betType == "Odd" && rouletteNumber % 2 != 0)
            }

            val winAmount = if (isWin && betAmount != null) betAmount * 2 else null
            if (winAmount != null) balance += winAmount
            if (betAmount != null) balance -= betAmount

            // Update balance display
            balanceTextView.text = "Balance: $$balance"

            // Add a new row to the results table
            addResultRow(currentRound, rouletteNumber, betAmount, winAmount, balance)

            // Increment the round number
            currentRound += 1

            // Scroll to the latest result
            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }

            // 0xFFB4B4B4

        }

        // Set up button listeners for even bets
        evenBetButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (balance >= betAmounts[index]) {
                    spinRoulette("Even", betAmounts[index])
                } else {
                    Toast.makeText(this, "Not enough balance!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up button listeners for odd bets
        oddBetButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (balance >= betAmounts[index]) {
                    spinRoulette("Odd", betAmounts[index])
                } else {
                    Toast.makeText(this, "Not enough balance!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Skip button action
        skipButton.setOnClickListener {
            spinRoulette("Skip", null)
        }

        // Restart button action
        restartButton.setOnClickListener {
            initializeState()
        }
    }

    // Function to reset the app to its initial state
    private fun initializeState() {
        balance = 500
        currentRound = 1
        balanceTextView.text = "Balance: $balance"
        resultsTable.removeAllViews()
        addTableHeader()
    }

    // Function to add the table header
    private fun addTableHeader() {
        val headerRow = TableRow(this).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }

        val headers = listOf("Round", "Last Number", "Bet", "Win", "Balance")
        headers.forEach { header ->
            val textView = TextView(this).apply {
                text = header
                setPadding(16, 16, 16, 16)
                setBackgroundColor(0xFFCCCCCC.toInt()) // Light gray background
                setTextColor(0xFF000000.toInt()) // Black text
            }
            headerRow.addView(textView)
        }

        resultsTable.addView(headerRow)
    }

    // Function to add a result row to the table
    private fun addResultRow(round: Int, lastNumber: Int, bet: Int?, win: Int?, balance: Int) {
        val resultRow = TableRow(this).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }

        // ROUND
        val textViewRound = TextView(this).apply {
            text = round.toString()
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFEEEEEE.toInt()) // Light background for rows
            setTextColor(0xFF000000.toInt()) // Black text
        }
        resultRow.addView(textViewRound)

        // LAST NUMBER
        val textViewNumber = TextView(this).apply {
            text = lastNumber.toString()
            setPadding(16, 16, 16, 16)
            if (lastNumber == 0 || lastNumber == 37) {
                setBackgroundColor(0xFFAAFFAA.toInt()) // Green for 0, 00
            } else if (lastNumber % 2 == 0) {
                setBackgroundColor(0xFFB4B4B4.toInt()) // Light gray for even numbers
            } else {
                setBackgroundColor(0xFFF44336.toInt()) // Red for odd numbers
            }
            setTextColor(0xFF000000.toInt()) // Black text
        }
        resultRow.addView(textViewNumber)

        // BET
        val textViewBet = TextView(this).apply {
            text = bet?.let { "$$it" } ?: ""
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFEEEEEE.toInt()) // Light background for rows
            setTextColor(0xFF000000.toInt()) // Black text
        }
        resultRow.addView(textViewBet)

        // BET
        val textViewWin = TextView(this).apply {
            text = win?.let { "$$it" } ?: ""
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFEEEEEE.toInt()) // Light background for rows
            setTextColor(0xFF000000.toInt()) // Black text
        }
        resultRow.addView(textViewWin)

        // BALANCE
        val textViewBalance = TextView(this).apply {
            text = balance.toString()
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFEEEEEE.toInt()) // Light background for rows
            if (balance > 500) {
                setTextColor(0xFF009900.toInt()) // Green for positive balance
            } else {
                setTextColor(0xFF000000.toInt()) // Black text
            }
        }
        resultRow.addView(textViewBalance)

        // Add Row to the table.
        resultsTable.addView(resultRow)
    }

}