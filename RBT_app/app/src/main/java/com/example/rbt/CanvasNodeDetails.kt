package com.example.rbt

import android.graphics.Canvas
import android.graphics.Paint

class CanvasNodeDetails(canvas_ : Canvas?, screenRes_ : Int) {
    private var screenRes = screenRes_
    private var _canvas = canvas_
    private var rectCenterX = 0
    private var rectCenterY  = 0
    private val rectSize = 300
    private val detailsMainNodeRadius = rectSize/4
    private val detailsPointerNodeRadius = rectSize/8
    private var nodeX = 0
    private var nodeY = 0

    fun countBoxCenter(node: RBTNode){
        nodeX = node.getCenterX().toInt()
        nodeY = node.getCenterY().toInt()
        var rectStart: Int
        if(screenRes - 10 < nodeX + rectSize ) {
            rectStart = screenRes - rectSize - 10
        } else {
            rectStart = nodeX
        }
        rectCenterX = rectStart + rectSize/2
        rectCenterY = nodeY + rectSize/2
    }
    fun drawBGRectangle(){
        val bg = Paint()
        bg.color = 0xf5eeeeee.toInt()
        _canvas?.drawRoundRect(rectCenterX - rectSize/2.toFloat(),rectCenterY - rectSize/2.toFloat(),rectCenterX + rectSize/2.toFloat(),rectCenterY+rectSize/2.toFloat(),10F,10F,bg)
    }
    fun drawDetails(node: RBTNode?){
        if(node == null)
            return
        countBoxCenter(node)
        drawBGRectangle()

        val text = Paint()
        text.color = 0xff000000.toInt()
        text.textSize = 30F
        val textPaint = node.getTextPaint()
        textPaint.textSize = 40F
        _canvas?.drawCircle(rectCenterX.toFloat(),rectCenterY.toFloat(),detailsMainNodeRadius.toFloat(),node.getNodePaint())
        _canvas?.drawText(node.getKey().toString(), rectCenterX.toFloat(), rectCenterY.toFloat()+12F,textPaint)
        _canvas?.drawCircle(rectCenterX.toFloat(),(rectCenterY - detailsMainNodeRadius*1.1).toFloat(),detailsPointerNodeRadius.toFloat(),
            Paint()
        )
        _canvas?.drawCircle((rectCenterX - detailsMainNodeRadius/1.2).toFloat(),(rectCenterY + detailsMainNodeRadius/1.2).toFloat(),detailsPointerNodeRadius.toFloat(),
            Paint()
        )
        _canvas?.drawCircle((rectCenterX + detailsMainNodeRadius/1.2).toFloat(),(rectCenterY + detailsMainNodeRadius/1.2).toFloat(),detailsPointerNodeRadius.toFloat(),
            Paint()
        )
        _canvas?.drawText("TD", rectCenterX.toFloat(), rectCenterY.toFloat()+12F,textPaint)

    }
}