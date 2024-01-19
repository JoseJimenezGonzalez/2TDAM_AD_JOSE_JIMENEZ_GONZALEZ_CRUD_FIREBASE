package com.example.myapplication.adaptador

import com.example.myapplication.modelo.Juego

interface OnItemClickListener {
    fun onItemClick(juego: Juego)
    fun onLongItemClick(juego: Juego)
}