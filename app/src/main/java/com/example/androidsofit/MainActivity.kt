package com.example.androidsofit

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.androidsofit.managers.SocketListener
import com.example.androidsofit.managers.WebSocketManager
import com.example.androidsofit.model.BitCoin
import com.example.androidsofit.model.Btc
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import okhttp3.*
import okio.ByteString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var mWebSocket: WebSocket? = null
    lateinit var webSocketBtc: WebSocketManager
    var btc:Btc? = null
    private var lineValue = ArrayList<Entry>()
    private var count = 0
    lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webSocketBtc = WebSocketManager()
        configureLineChart()
        initViews()
    }

    private fun initViews() {
       webSocketBtc.connectToSocket("live_trades_btcusd")
        webSocketBtc.socketListener(object :SocketListener{
            override fun onSuccess(bitCoin: BitCoin) {
                count++
                runOnUiThread{
                    if (bitCoin.event =="btc:subscription_succeeded"){
                        Toast.makeText(this@MainActivity, "Successfuly Connected", Toast.LENGTH_SHORT).show()
                    }else{
                        lineValue.add(Entry(count.toFloat(),bitCoin.data.price.toFloat()))
                        setlineChartData(lineValue)
//                        Toast.makeText(this@MainActivity, bitCoin.data.price.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(message: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setlineChartData(pricesHigh:ArrayList<Entry>){
        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        val highLineDataSet = LineDataSet(pricesHigh,"Live")
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        dataSets.add(highLineDataSet)

        val lineData = LineData(dataSets)
        var lineChart = findViewById<LineChart>(R.id.line_chart)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun configureLineChart(){
        val desc = Description()
        desc.text = "BTC-USD"
        desc.textSize = 20f
        lineChart = findViewById(R.id.line_chart)
        lineChart.description = desc
        val xAxis: XAxis = lineChart.xAxis
        xAxis.valueFormatter = object :ValueFormatter(){
            @RequiresApi(Build.VERSION_CODES.N)
            private val mFormat:SimpleDateFormat = SimpleDateFormat("HH mm", Locale.getDefault())

            @RequiresApi(Build.VERSION_CODES.N)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(System.currentTimeMillis()))
            }
        }

    }

}