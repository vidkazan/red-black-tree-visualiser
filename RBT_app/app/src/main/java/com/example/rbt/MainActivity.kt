package com.example.rbt

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import com.example.rbt.databinding.ActivityMainBinding


class   MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val client = Client()
    private var _numToAdd = 0
    private var _numToErase: Int? = null
    private var _numByButtons: String = ""

    fun getRandomWithExclusion(keys: List<Int>): Int {
        var rand = (-99..999).random()
        while (keys.contains(rand)) {
            rand = (-99..999).random()
        }
        return rand
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _numToAdd = getRandomWithExclusion(binding.canvas.getKeysInTree())
        binding.buttonAdd.text = "ADD $_numToAdd"
        binding.radioBtnFt.setOnClickListener {clickHandler("FT")}
        binding.radioBtnStd.setOnClickListener {clickHandler("STD")}
        binding.buttonAdd.setOnClickListener { clickHandler("ADD") }
        binding.buttonErase.setOnClickListener { clickHandler("ERASE") }
        binding.button1.setOnClickListener { clickNumHandler("1") }
        binding.button2.setOnClickListener { clickNumHandler("2") }
        binding.button3.setOnClickListener { clickNumHandler("3") }
        binding.button4.setOnClickListener { clickNumHandler("4") }
        binding.button5.setOnClickListener { clickNumHandler("5") }
        binding.button6.setOnClickListener { clickNumHandler("6") }
        binding.button7.setOnClickListener { clickNumHandler("7") }
        binding.button8.setOnClickListener { clickNumHandler("8") }
        binding.button9.setOnClickListener { clickNumHandler("9") }
        binding.button0.setOnClickListener { clickNumHandler("0") }
        binding.buttonMinus.setOnClickListener { clickNumHandler("-") }
        binding.buttonBack.setOnClickListener { clickNumHandler("c") }
        binding.buttonErase.setOnLongClickListener { longClickHandler("CLEAR") }
        binding.buttonAdd.setOnLongClickListener { longClickHandler("FILL") }
    }
    private fun generateRandomTree(size: Int){
        var s = size
        if(s > 50 || s < 1) {
            s = 50
        }
        binding.buttonAdd.text = "+$s nodes"
        while(s > 0) {
            _numToAdd = getRandomWithExclusion(binding.canvas.getKeysInTree())
            client.addNodeRequestSend(_numToAdd.toString())
            s -= 1
            val text = SpannableString("$_numToAdd ")
            text.setSpan(ForegroundColorSpan(0xff286E4C.toInt()), 0,text.length,0)
            binding.textSteps.append(text)
        }
    }
    private fun longClickHandler(type: String): Boolean {
        when (type) {
            "CLEAR" -> {
                binding.canvas.invalidate()
                binding.buttonErase.text = "cleared!"
                binding.textSteps.text = ""
                client.clearAllNodeRequestSend()
                val response  = sendGetRequest("CLEAR")
                if(response.isNotEmpty()) {
                    binding.canvas.setResponse(sendGetRequest("CLEAR"))
                }
            }
            "FILL" -> {
                binding.canvas.invalidate()
                if (_numByButtons.isNotEmpty() && checkNumByButtons("add")) {
                    generateRandomTree(_numByButtons.toInt())
                } else {
                    generateRandomTree(_numToAdd)
                }
                sendGetRequest("FILL")
            }
        }
        return true
    }
    private fun checkNumByButtons(type: String): Boolean {
        if (_numByButtons == "-")
            return false
        when (type) {
            "add" -> {
                if (_numByButtons.length <= 3
                    &&
                        ((binding.canvas.getKeysInTree().isEmpty()) || (binding.canvas.getKeysInTree().isNotEmpty()
                    &&
                        binding.canvas.getKeysInTree().indexOf(_numByButtons.toInt()) == -1))
                ) {
                    return true
                }
                return false
            }
            "erase" -> {
                if (_numByButtons.length <= 3
                    &&
                        ((binding.canvas.getKeysInTree().isEmpty()) || (binding.canvas.getKeysInTree().isNotEmpty()
                    &&
                        binding.canvas.getKeysInTree().indexOf(_numByButtons.toInt()) != -1))
                ) {
                    return true
                }
                return false
            }
            else -> return false
        }
    }
    private fun clickHandler(btnType: String) {
        if (btnType == "ERASE" && binding.buttonErase.text == "CLEARED!")
            return
        binding.canvas.invalidate()
        when (btnType) {
            "ADD" -> {
                if (_numByButtons.isNotEmpty() && checkNumByButtons("add")) {
                    client.addNodeRequestSend(_numByButtons)
                    val text = SpannableString("$_numByButtons ")
                    text.setSpan(ForegroundColorSpan(0xff286E4C.toInt()), 0,text.length,0)
                    binding.textSteps.append(text)
                } else if (_numByButtons.isNotEmpty() && !checkNumByButtons("add")) {
                    _numByButtons = ""
                    return
                } else {
                    val text = SpannableString("$_numToAdd ")
                    text.setSpan(ForegroundColorSpan(0xff286E4C.toInt()), 0,text.length,0)
                    binding.textSteps.append(text)
                    client.addNodeRequestSend(_numToAdd.toString())
                }
            }
            "ERASE" -> {
                if (_numByButtons.isNotEmpty() && checkNumByButtons("erase")) {
                    client.eraseNodeRequestSend(_numByButtons)
                } else if (_numByButtons.isNotEmpty() && !checkNumByButtons("erase")) {
                    _numByButtons = ""
                    return
                } else if (binding.canvas.getKeysInTree().size == 3 && _numToErase == binding.canvas.getKeysInTree()[1]) {
                    longClickHandler("CLEAR")
                    return
                } else {
                    client.eraseNodeRequestSend(_numToErase.toString())
                    val text = SpannableString("$_numToErase ")
                    text.setSpan(ForegroundColorSpan(0xffB02B2B.toInt()), 0,text.length,0)
                    binding.textSteps.append(text)
                }
            }
        }
        sendGetRequest(btnType)
    }
    private fun sendGetRequest(btnType: String) : String{
        val response = if(binding.radioBtnStd.isChecked){
            client.getTreeRequestSendStd()
        } else {
            client.getTreeRequestSendFt()
        }
        if(response == "ConnectException") {
            binding.buttonErase.text = "no server:("
            return ""
        } else if (response == "ServerResponseException"){
            binding.buttonErase.text = "crashed:("
            return ""
        } else {
            binding.canvas.setResponse(response)
        }
        if(response.isEmpty()) {
            return ""
        }
        _numByButtons = ""
        if(btnType != "STD" && btnType != "FT" && btnType != "FILL") {
            _numToAdd = getRandomWithExclusion(binding.canvas.getKeysInTree())
            binding.buttonAdd.text = "ADD ${_numToAdd}"
            _numToErase = 0
            binding.canvas.updateKeysInTree(response)
            if (binding.canvas.getKeysInTree().isNotEmpty()) {
                _numToErase = binding.canvas.getKeysInTree().randomOrNull()
                binding.buttonErase.text = "ERASE $_numToErase"
            }
        }
        return response
    }
    private fun clickNumHandler(btnType: String) {
        when (btnType) {
            "1" -> {
                _numByButtons += '1'
            }
            "2" -> {
                _numByButtons += '2'
            }
            "3" -> {
                _numByButtons += '3'
            }
            "4" -> {
                _numByButtons += '4'
            }
            "5" -> {
                _numByButtons += '5'
            }
            "6" -> {
                _numByButtons += '6'
            }
            "7" -> {
                _numByButtons += '7'
            }
            "8" -> {
                _numByButtons += '8'
            }
            "9" -> {
                _numByButtons += '9'
            }
            "0" -> {
                if (_numByButtons.isEmpty() || _numByButtons.last().isDigit())
                    _numByButtons += '0'
                else
                    return
            }
            "-" -> {
                if(_numByButtons.startsWith("-")){
                    _numByButtons.drop(1)
                } else {
                    _numByButtons = "-$_numByButtons"
                }
                return
            }
            "c" -> {
                if (_numByButtons.isNotEmpty())
                    _numByButtons = _numByButtons.dropLast(1)
                else {
                    return
                }
            }
        }
        binding.buttonAdd.text = "ADD $_numByButtons"
        binding.buttonErase.text = "ERASE $_numByButtons"
    }
}