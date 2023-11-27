package com.example.bloco_notas.ListaNotas

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DecoracaoEspacoItem(context: Context) : RecyclerView.ItemDecoration() {
    private val espacoVert = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 7f, context.resources.displayMetrics
    ).toInt()
    private val espacoHorz = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics
    ).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = -espacoHorz
        outRect.right = -espacoHorz
        outRect.top = -espacoVert
        outRect.bottom = -espacoVert
    }
}