package com.example.rbt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.core.text.isDigitsOnly
import kotlin.math.sqrt


class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr)
{
    private var _clickXPos = 0.0F
    private var _clickYPos = 0.0F
    private var _keysInTree = mutableListOf<Int>()
    private var _canvas : Canvas? = null
    private var _maxKeyLen : Int = 0
    private var _nodeSize : Float = 50f
    private var _nodeVerticalOffset : Float = 0f
    private var _nodes = listOf<RBTNode>()
    private var _response : String? = ""
    private var _drawDetails : Boolean = false
    private var _nodeForDetails : RBTNode? = null


    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                _clickXPos = event.x
                _clickYPos = event.y
                performClick()
                return true
            }
        }
        return false
    }
    override fun performClick(): Boolean {
        super.performClick()
        checkForShowDetails()
        invalidate()
        return true
    }

    private fun checkNodeForClick(node : RBTNode): Boolean {
        val nodeStart = node.getCenterX() - (node.getSize() / 1.5)
        val nodeEnd = node.getCenterX() + (node.getSize() / 1.5)
        val nodeTop = node.getCenterY() - (node.getSize() / 1.5)
        val nodeBottom = node.getCenterY() + (node.getSize() / 1.5)
        if(_clickXPos > nodeStart && _clickXPos < nodeEnd && _clickYPos > nodeTop && _clickYPos < nodeBottom ) {
            return true
        }
        return false
    }

    private fun checkForShowDetails() {
        for( node in _nodes) {
            if(checkNodeForClick(node)) {
                _drawDetails = true
                _nodeForDetails = node
                return
            }
        }
        _drawDetails = false
    }

    fun getKeysInTree() : List<Int>{
        return _keysInTree
    }
    fun setResponse(str : String?) {
            _response = str
        }
    private fun getNodesAsList(tree: List<NodeData>): List<RBTNode>{
        val nodes = mutableListOf<RBTNode>()
            for(node in tree) {
                nodes.add(RBTNode(node.treePosition, node.color, node.key, _nodeSize, node.keyParent,node.keyLeft,node.keyRight))
            }
        return nodes
    }
    fun updateKeysInTree(jsonTree: String?){
        if(jsonTree != null && jsonTree.isNotEmpty()) {
            val tree = ImportTree().getTree(jsonTree)
            if(tree != null) {
                _keysInTree.clear()
                for (node in tree) {
                    if(node.key!!.isDigitsOnly() || node.key.startsWith("-")) {
                        _keysInTree.add(node.key!!.toInt())
                        if (node.key.length > _maxKeyLen)
                            _maxKeyLen = node.key.length
                    }
                }
            }
        }
    }
    private fun getScreenRes() : Int{
        val displayMetrics = DisplayMetrics()
        display.apply { getRealMetrics(displayMetrics) }

        return displayMetrics.widthPixels
    }
    private fun drawNodes(canvas: Canvas?, nodes: List<RBTNode>?){
        if (nodes != null) {
            for(node in nodes) {
                if(node.getTreePosition()!! != "C") {
                    node.drawLine(canvas,
                        nodes.find {
                            node.getTreePosition()!!.dropLast(1) == it.getTreePosition()
                        }!!.getCenterX(),
                        nodes.find {
                            node.getTreePosition()!!.dropLast(1) == it.getTreePosition()
                        }!!.getCenterY()
                    )
                }
            }
            for(node in nodes) {
                node.drawNode(canvas, _maxKeyLen)
            }
        }
    }

    private fun setCoordinatesForNodes(){
        var currentPosX = _nodeSize/1.8f + _nodeSize/ sqrt(_nodeSize)
        var posY : Float
        for(node in _nodes) {
            if(node.getTreePosition() != null && node.getTreePosition()!!.isNotEmpty()) {
                posY = node.getTreePosition()!!.length * _nodeVerticalOffset
                node.setCoordinates(currentPosX, posY)
            }
            currentPosX+=_nodeSize/1.8f
        }
    }
    fun refreshDraw(jsonTree: String?){
        if(jsonTree != null && jsonTree.isNotEmpty()) {
            val tree = ImportTree().getTree(jsonTree)
            if(tree != null) {
                _nodeSize =
                    getScreenRes().toFloat() / tree.size * (1.24f + (sqrt(tree.size.toFloat()) / 25f))
                _nodeVerticalOffset = 1.1f * _nodeSize
                _nodes = getNodesAsList(tree)
                setCoordinatesForNodes()
                drawNodes(_canvas, _nodes)
            }
        } else{
            _canvas?.drawLine(0f,0f,0f,0f,Paint())
        }
        if(_drawDetails) {
            drawDetails(_nodeForDetails)
        }
    }
    fun drawDetails(node: RBTNode?){
        val bg = Paint()
        bg.color = 0xf2eeeeee.toInt()
        val text = Paint()
        text.color = 0xff000000.toInt()
        text.textSize = 30F
        if(node == null)
            return
        val screenRes = getScreenRes()
        val nodeX = node.getCenterX().toInt()
        val nodeY = node.getCenterY().toInt()
        val rectSize = 300
        var rectStart = 0
        var rectEnd = 0
        var rectTop = nodeY
        var rectBottom = nodeY + rectSize
        if(screenRes - 10 < nodeX + rectSize ) {
            rectStart = screenRes - rectSize - 10
            rectEnd = screenRes - 10
        } else {
            rectStart = nodeX
            rectEnd = nodeX + rectSize
        }
        val rectCenterX = rectStart + rectSize/2
        val rectCenterY = rectTop + rectSize/2
        val detailsMainNodeRadius = rectSize/4
        val detailsPointerNodeRadius = rectSize/8
        val textPaint = node.getTextPaint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 45F
        val smallTextPaint = Paint()
        smallTextPaint.textSize = 30F
        smallTextPaint.color = 0xffffffff.toInt()
        smallTextPaint.textAlign = Paint.Align.CENTER
        _canvas?.drawRoundRect(rectStart.toFloat(),rectTop.toFloat(),rectEnd.toFloat(),rectBottom.toFloat(),10F,10F,bg)
        _canvas?.drawCircle(rectCenterX.toFloat(),rectCenterY.toFloat(),detailsMainNodeRadius.toFloat(),node.getNodePaint())
        _canvas?.drawText(node.getKey().toString(), rectCenterX.toFloat(), rectCenterY.toFloat()+12F,textPaint)
        _canvas?.drawCircle(rectCenterX.toFloat(),(rectCenterY - detailsMainNodeRadius*1.1).toFloat(),detailsPointerNodeRadius.toFloat(),Paint())
        _canvas?.drawCircle((rectCenterX - detailsMainNodeRadius/1.2).toFloat(),(rectCenterY + detailsMainNodeRadius/1.2).toFloat(),detailsPointerNodeRadius.toFloat(),Paint())
        _canvas?.drawCircle((rectCenterX + detailsMainNodeRadius/1.2).toFloat(),(rectCenterY + detailsMainNodeRadius/1.2).toFloat(),detailsPointerNodeRadius.toFloat(),Paint())
        _canvas?.drawText(node.getKeyParent().toString(), (rectCenterX.toFloat()), (rectCenterY - detailsMainNodeRadius*1.1).toFloat()+12F,smallTextPaint)
        _canvas?.drawText(node.getKeyLeft().toString(), (rectCenterX - detailsMainNodeRadius/1.2).toFloat(), (rectCenterY + detailsMainNodeRadius/1.2).toFloat()+12F,smallTextPaint)
        _canvas?.drawText(node.getKeyRight().toString(), (rectCenterX + detailsMainNodeRadius/1.2).toFloat(), (rectCenterY + detailsMainNodeRadius/1.2).toFloat()+12F,smallTextPaint)

    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        _canvas = canvas
        refreshDraw(_response)
        _drawDetails = false
    }
}
