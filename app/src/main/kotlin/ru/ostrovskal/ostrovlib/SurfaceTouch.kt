package ru.ostrovskal.ostrovlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import ru.ostrovskal.sshstd.Common.*
import ru.ostrovskal.sshstd.Size
import ru.ostrovskal.sshstd.Touch
import ru.ostrovskal.sshstd.utils.dp2px
import ru.ostrovskal.sshstd.utils.info
import ru.ostrovskal.sshstd.utils.withSave
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class SurfaceTouch(context: Context) : ru.ostrovskal.sshstd.Surface(context) {

	var idxRadio			= -1

	private var oldIdxRadio = -1

	private var rects 		= arrayOf<Rect>()
	private val touch		= Touch()
	private val touch2		= Touch()
	private val rectBmp     = Rect()
	private var bitmap		= BitmapFactory.decodeResource(resources, R.drawable.droid, BitmapFactory.Options().apply { inDensity = dMetrics.densityDpi } )

	private var temp  		= -1

	private var angle       = 0f
	private var len         = 0f
	private var oldLen      = 0f

	private val cell        = Size(0, 0)
	private val cellD       = Size(0, 0)

	private var c0          = PointF()
	private var c1          = PointF()

	private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = 0xff00ff00.toInt()
		strokeWidth = 2f
		textAlign = Paint.Align.CENTER
		textSize = 40f
		style = Paint.Style.STROKE
	}

	private val paintBase = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = 0xff00ffff.toInt()
		strokeWidth = 3f
		style = Paint.Style.FILL_AND_STROKE
	}

	private val paintC = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = 0xffff0000.toInt()
		strokeWidth = 1f
		style = Paint.Style.FILL_AND_STROKE
	}

	private val paintB = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = 0xff0000ff.toInt()
		strokeWidth = 1f
		style = Paint.Style.FILL_AND_STROKE
	}

	override fun updateState() {}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		idxRadio = touchGrp.isChecked()?.id ?: -1
		if(oldIdxRadio != idxRadio) { temp = -1; angle = 0f; oldLen = 1f; oldIdxRadio = idxRadio }
        if(event.actionMasked != MotionEvent.ACTION_MOVE) "a: ${event.actionMasked} i: ${event.actionIndex} c: ${event.pointerCount}".info()
		touch.event(event, touch2).apply {
			when(idxRadio) {
				actClick       -> click(rects) { idx -> temp = idx; }
				actDblClick    -> dblClick(rects) { idx -> temp = idx; }
				actDirect      -> if(temp == -1) {
									val clk = contains(rects, ptBegin)
									if(clk != -1) { rects[clk].apply { c0.x = centerX().toFloat(); c0.y = centerY().toFloat() }; temp = 0 }
								} else direction(cell, c0, false) { dir -> c1.x = ptCurrent.x; c1.y = ptCurrent.y; temp = dir }
				actDrag        -> drag(cellD) { o, e ->
									if (e) {
										val p = ptBegin
										if (temp == -1) temp = contains(rects, p)
										if (temp != -1) {
											val r = rects[temp]
											val x = p.x.roundToInt()
											val y = p.y.roundToInt()
											val w = cell.w / 2
											val h = cell.h / 2
											val left = (o.w + x) / cell.w
											val top = (o.h + y) / cell.h
											r.left = w + left * cell.w
											r.top = h + top * cell.h
											r.right = r.left + cell.w - 5
											r.bottom = r.top + cell.h - 5
										}
									} else temp = -1
								}
				actRotate      -> rotate(cellD, c0) { a, e ->
										if(temp == -1) {
											val clk = contains(rects, ptBegin)
											if(clk != -1) {
												c0.x = rects[clk].centerX().toFloat()
												c0.y = rects[clk].centerY().toFloat()
												c1.x = c0.x; c1.y = c0.y
												temp = 0
											}
										} else {
											if(e) {
												c1.x = ptCurrent.x; c1.y = ptCurrent.y
												angle = a
											} else temp = -1
										}
									}
				actScale       -> {
/*
					if(temp == -1) {
						touch2.apply {
							ptBegin.x = touch.ptBegin.x
							ptBegin.y = touch.ptBegin.y
							ptCurrent.x = ptBegin.x
							ptCurrent.y = ptBegin.y
							flags = TOUCH_PRESSED
						}
						if(isUnpressed) { temp = 0; flags = 0 }
					}
*/
						scale(touch2, cellD) { o, e ->
							if (e) {
								val p1 = ptCurrent
								val p2 = touch2.ptCurrent
								c1.x = p1.x; c1.y = p1.y
								c0.x = p2.x; c0.y = p2.y
								len = o
                                o.info()
							} else { temp = -1 }
						}
//					}
				}
			}
		}
		return true
	}
	
	@SuppressLint("DrawAllocation")
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val w = measuredWidth; val h = measuredHeight
		val col = (w.dp2px / 60); val row = (h.dp2px / 60)
		val ww = (w / col); val hh = (h / row)
		rectBmp.set(0, 0, w, h)
		cell.set(w / col, h / row)
		cellD.set(8, 8)
		var x = -ww; var y = -hh
		val count = col * row
		rects = Array(count) {
			x += ww
			if((it % col) == 0) { x = 0; y += hh }
			Rect(x, y, x + ww - 5, y + hh - 5)
		}
	}
	
	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		rects.forEach { canvas.drawRect(it, paintStroke) }
		when(idxRadio) {
			actClick,
			actDblClick	   -> if(temp != -1) canvas.drawBitmap(bitmap, null, rects[temp], paintBase)
			actDirect      -> {
								val mx = abs(c1.x - c0.x).coerceAtLeast(abs(c1.y - c0.y))
								when(temp) {
									DIRR, DIRL  -> canvas.drawLine(c0.x, c0.y, c1.x, c0.y, paintBase)
									DIRU, DIRD  -> canvas.drawLine(c0.x, c0.y, c0.x, c1.y, paintBase)
									DIRR or DIRU-> canvas.drawLine(c0.x, c0.y, c0.x + mx, c0.y - mx, paintBase)
									DIRR or DIRD-> canvas.drawLine(c0.x, c0.y, c0.x + mx, c0.y + mx, paintBase)
									DIRL or DIRU-> canvas.drawLine(c0.x, c0.y, c0.x - mx, c0.y - mx, paintBase)
									DIRL or DIRD-> canvas.drawLine(c0.x, c0.y, c0.x - mx, c0.y + mx, paintBase)
								}
							}
			actDrag         -> { if(temp in rects.indices) canvas.drawBitmap(bitmap, null, rects[temp], paintBase) }
			actRotate       -> if(temp != -1) {
									val ww = cell.w * 2f
									val hh = cell.h * 2f
									canvas.withSave {
										val x = ((c0.x - c1.x) / cell.w.toDouble()).pow(2.0)
										val y = ((c0.y - c1.y) / cell.h.toDouble()).pow(2.0)
										val s = sqrt(x + y).toFloat() / 2f
										val r = RectF(c0.x - ww, c0.y - hh, c0.x + ww, c0.y + hh)
										canvas.scale(s, s, c0.x, c0.y)
										canvas.rotate(angle, c0.x, c0.y)
										canvas.drawBitmap(bitmap, null, r, paintBase)
									}
									canvas.drawLine(c0.x, c0.y, c1.x, c1.y, paintBase)
								}
			actScale       -> {
								val x = c1.x + (c0.x - c1.x) / 2f
								val y = c1.y + (c0.y - c1.y) / 2f
								canvas.withSave {
									canvas.scale(len, len, x, y)
									canvas.drawBitmap(bitmap, null, rectBmp, paintStroke)
								}
								canvas.drawCircle(x, y, 5f, paintBase)
							}
		}
		canvas.drawCircle(c0.x, c0.y, 10f, paintC)
		canvas.drawCircle(c1.x, c1.y, 10f, paintB)
	}
}
