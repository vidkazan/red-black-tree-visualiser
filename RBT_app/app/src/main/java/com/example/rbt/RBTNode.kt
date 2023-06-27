package com.example.rbt

import android.graphics.Canvas
import android.graphics.Paint

class RBTNode(treePosition: String?, color : String?, key: String?,nodeSize : Float,keyP: String?,keyL: String?,keyR: String?)
{
    private val _treePosition : String?
    private val _color : String?
    private val _nodePaint = Paint()
    private val _textPaint = Paint()
    private val _key : String?
    private val _keyParent : String?
    private val _keyLeft : String?
    private val _keyRight : String?
    private val _size: Float
    private var _x : Float
    private var _y : Float
    init {
        _keyParent = keyP
        _keyLeft = keyL
        _keyRight = keyR
        _treePosition = treePosition
        _color = color
        _key = key
        _size = nodeSize
        _x = _size
        _y = _size
        setPaintProp()
    }
    private fun setPaintProp(){
        _nodePaint.flags = Paint.ANTI_ALIAS_FLAG
        _textPaint.flags = Paint.ANTI_ALIAS_FLAG
        _textPaint.textAlign = Paint.Align.CENTER
        _textPaint.textSize = _size * 0.6f
        if(this._color == "BLACK") {
            _textPaint.color = 0xffffffff.toInt()
            _nodePaint.color = 0xff353535.toInt()
        } else {
            _textPaint.color = 0xff000000.toInt()
            _nodePaint.color = 0xffff0000.toInt()
        }
    }
    fun getKeyParent() : String?{
        return _keyParent
    }
    fun getKeyLeft() : String?{
        return _keyLeft
    }
    fun getKeyRight() : String?{
        return _keyRight
    }
    fun getStart(): Float{
        if(this._key=="N")
            return _x - _size/6
        else
            return _x - _size/2

    }
    fun getEnd(): Float{
        if(this._key=="N")
            return _y - _size/6
        else
            return _y - _size/2
    }
    fun getTop(): Float{
        if(this._key=="N")
            return _x + _size/6
        else
            return _x + _size/2
    }
    fun getBottom(): Float{
        return if(this._key=="N")
            _y + _size/6
        else
            _y + _size/2
    }
    fun getTreePosition(): String? {
        return _treePosition
    }
    fun getSize() : Float{
        return _size
    }
    fun getKey() : String?{
        return _key
    }
    fun setCoordinates(x: Float, y : Float){
        _x = x
        _y = y
    }
    fun getCenterX(): Float{
        return _x
    }
    fun getCenterY(): Float {
        return _y
    }
    fun getNodePaint(): Paint{
        return _nodePaint
    }
    fun getTextPaint(): Paint{
        return _textPaint
    }
    fun drawNode(canvas: Canvas?, maxKeyLen : Int){
        when(maxKeyLen) {
            1->_textPaint.textSize *= 1
            2->_textPaint.textSize *= 0.9f
            3->_textPaint.textSize *= 0.8f
            else-> _textPaint.textSize *= 0.5f
        }
        canvas?.drawOval(getStart(),getEnd(),getTop(),getBottom(),_nodePaint)
        if(_key=="N"){
            return
        }
        when(_key!!.length) {
            1->canvas?.drawText(_key.toString(), getStart() + _size/2f, getBottom()-_size/3, _textPaint)
            2->canvas?.drawText(_key.toString(), getStart() + _size/2f, getBottom()-_size/3, _textPaint)
            3->canvas?.drawText(_key.toString(), getStart() + _size/2f, getBottom()-_size/3, _textPaint)
            else-> canvas?.drawText(_key.toString(), getStart() + _size/2f, getBottom()-_size/3, _textPaint)
        }
    }
    fun drawLine(canvas: Canvas?,stopX: Float, stopY: Float) {
        val paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.strokeWidth = _size/10
        if(_treePosition != null && _treePosition.isNotEmpty()) {
            when(_treePosition.last()) {
                'C' -> canvas?.drawLine(_x,_y,_x,0f,paint)
                else -> canvas?.drawLine(_x,_y,stopX,stopY,paint)
            }
        }
    }
}