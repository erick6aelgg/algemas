package com.unam.algeplus.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Vista personalizada que dibuja una balanza con dos platillos.
 * Se inclina al inicio para representar que la ecuación aún no está equilibrada.
 * Anima a la posición horizontal cuando el ejercicio se resuelve correctamente.
 */
public class BalanzaView extends View {

    private float tiltDegrees = -10f;

    private String leftText  = "x";
    private String rightText = "?";

    // Paints
    private final Paint beamPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint panFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint panBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fulcrumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chainPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BalanzaView(Context context) {
        super(context);
        init();
    }

    public BalanzaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        beamPaint.setColor(Color.parseColor("#2E8B2E"));
        beamPaint.setStrokeWidth(14f);
        beamPaint.setStyle(Paint.Style.STROKE);
        beamPaint.setStrokeCap(Paint.Cap.ROUND);

        panFillPaint.setColor(Color.parseColor("#D6EFD6"));
        panFillPaint.setStyle(Paint.Style.FILL);

        panBorderPaint.setColor(Color.parseColor("#2E8B2E"));
        panBorderPaint.setStyle(Paint.Style.STROKE);
        panBorderPaint.setStrokeWidth(3f);

        fulcrumPaint.setColor(Color.parseColor("#1B6B1B"));
        fulcrumPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.parseColor("#1A1A1A"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        chainPaint.setColor(Color.parseColor("#58C93F"));
        chainPaint.setStrokeWidth(4f);
        chainPaint.setStyle(Paint.Style.STROKE);
        chainPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    // ── API pública ───────────────────────────────────────────────────────────

    public void setEquation(String leftSide, String rightSide) {
        this.leftText  = leftSide;
        this.rightText = rightSide;
        this.tiltDegrees = -10f;
        invalidate();
    }

    /** Anima la balanza al estado equilibrado (beam horizontal). */
    public void animateToBalanced(Runnable onComplete) {
        ValueAnimator anim = ValueAnimator.ofFloat(tiltDegrees, 0f);
        anim.setDuration(700);
        anim.setInterpolator(new OvershootInterpolator(1.2f));
        anim.addUpdateListener(a -> {
            tiltDegrees = (float) a.getAnimatedValue();
            invalidate();
        });
        if (onComplete != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) { onComplete.run(); }
            });
        }
        anim.start();
    }

    // ── Dibujo ────────────────────────────────────────────────────────────────

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w  = getWidth();
        float h  = getHeight();
        float cx = w / 2f;
        float pivotY  = h * 0.45f;
        float beamLen = w * 0.38f;
        float chainLen = h * 0.14f;
        float panW    = Math.min(beamLen * 1.55f, 190f);
        float panH    = h * 0.22f;

        // ── Post / columna vertical ──────────────────────────────────────────
        fulcrumPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(cx - 6, pivotY, cx + 6, h * 0.85f, fulcrumPaint);

        // ── Base ─────────────────────────────────────────────────────────────
        RectF base = new RectF(cx - 50, h * 0.85f, cx + 50, h * 0.93f);
        canvas.drawRoundRect(base, 14, 14, fulcrumPaint);

        // ── Beam y contenido en canvas rotado ────────────────────────────────
        canvas.save();
        canvas.rotate(tiltDegrees, cx, pivotY);

        // Beam
        canvas.drawLine(cx - beamLen, pivotY, cx + beamLen, pivotY, beamPaint);

        // Cadenas
        canvas.drawLine(cx - beamLen, pivotY, cx - beamLen, pivotY + chainLen, chainPaint);
        canvas.drawLine(cx + beamLen, pivotY, cx + beamLen, pivotY + chainLen, chainPaint);

        // Platillo izquierdo
        float lx = cx - beamLen;
        float ly = pivotY + chainLen;
        RectF leftPan = new RectF(lx - panW / 2, ly, lx + panW / 2, ly + panH);
        canvas.drawRoundRect(leftPan, 12, 12, panFillPaint);
        canvas.drawRoundRect(leftPan, 12, 12, panBorderPaint);

        // Platillo derecho
        float rx = cx + beamLen;
        RectF rightPan = new RectF(rx - panW / 2, ly, rx + panW / 2, ly + panH);
        canvas.drawRoundRect(rightPan, 12, 12, panFillPaint);
        canvas.drawRoundRect(rightPan, 12, 12, panBorderPaint);

        // Texto en platillos
        textPaint.setTextSize(Math.min(panH * 0.38f, 34f));
        canvas.drawText(leftText,  lx, ly + panH * 0.62f, textPaint);
        canvas.drawText(rightText, rx, ly + panH * 0.62f, textPaint);

        canvas.restore();

        // ── Triángulo / pivote (dibujado encima del beam) ────────────────────
        Path tri = new Path();
        tri.moveTo(cx,      pivotY - 10);
        tri.lineTo(cx - 22, pivotY + 22);
        tri.lineTo(cx + 22, pivotY + 22);
        tri.close();
        canvas.drawPath(tri, fulcrumPaint);
    }
}
